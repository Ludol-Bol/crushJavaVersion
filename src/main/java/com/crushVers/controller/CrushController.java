package com.crushVers.controller;

import com.crushVers.model.Crush;
import com.crushVers.model.Universe;
import com.crushVers.service.BaseDictionaryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;



@RestController
@RequestMapping("/api")
public class CrushController {

    private static final String CRUSH_COLLECTION = "crush";
    private static final String UNIVERSE_COLLECTION = "universes";

    private final BaseDictionaryService dictionaryService;

    public CrushController(BaseDictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @GetMapping("/crushes")
    public Map<String, Object> getCrushes() {
        try {
            List<Crush> crushes = dictionaryService.findAll(CRUSH_COLLECTION, Crush.class);
            List<Universe> universes = dictionaryService.findAll(UNIVERSE_COLLECTION, Universe.class);
            Map<String, String> universeMap = new HashMap<>();
            for (Universe universe : universes) {
                universeMap.put(universe.getId(), universe.getName());
            }
            List<Map<String, Object>> result = new ArrayList<>();
            for (Crush crush : crushes) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", crush.getId());
                item.put("name", crush.getName());
                item.put("description", crush.getDescription());
                item.put("height", crush.getHeight());
                item.put("dateOfBirth", crush.getDateOfBirth());
                item.put("imageUrl", null); // Пока null, потом добавим картинки
                item.put("universeName", universeMap.get(crush.getUniverseId()));
                result.add(item);
            }

            return Map.of("success", true, "crushes", result);

        } catch (ExecutionException | InterruptedException e) {
            return Map.of("success", false, "message", "Ошибка загрузки крашей: " + e.getMessage());
        }
    }

    @GetMapping("/crushes/search")
    public Map<String, Object> searchCrushes(@RequestParam String query) {
        try {
            String searchQuery = query.toLowerCase().trim();
            if (searchQuery.isEmpty()) {
                return getCrushes();
            }
            // Сначала загружаем вселенные
            List<Universe> universes = dictionaryService.findAll(UNIVERSE_COLLECTION, Universe.class);
            Map<String, String> universeMap = new HashMap<>();
            Map<String, String> universeNameToId = new HashMap<>();
            for (Universe u : universes) {
                universeMap.put(u.getId(), u.getName());
                universeNameToId.put(u.getName().toLowerCase(), u.getId());
            }

            // Загружаем всех крашей и фильтруем на сервере
            List<Crush> allCrushes = dictionaryService.findAll(CRUSH_COLLECTION, Crush.class);

            List<Crush> filtered = allCrushes.stream()
                    .filter(crush -> {
                        String name = crush.getName() != null ? crush.getName().toLowerCase() : "";
                        String universeName = universeMap.get(crush.getUniverseId()) != null ?
                                universeMap.get(crush.getUniverseId()).toLowerCase() : "";
                        return name.contains(searchQuery) || universeName.contains(searchQuery);
                    })
                    .limit(50)
                    .toList();

            List<Map<String, Object>> result = new ArrayList<>();
            for (Crush crush : filtered) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", crush.getId());
                item.put("name", crush.getName());
                item.put("description", crush.getDescription());
                item.put("height", crush.getHeight());
                item.put("dateOfBirth", crush.getDateOfBirth());
                item.put("universeName", universeMap.get(crush.getUniverseId()));
                result.add(item);
            }

            return Map.of("success", true, "crushes", result);

        } catch (Exception e) {
            return Map.of("success", false, "message", e.getMessage());
        }
    }
}