package com.crushVers.login;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        // Добавляем данные для передачи в HTML
        model.addAttribute("title", "CrushVerse");
        model.addAttribute("message", "Добро пожаловать в CrushVerseApp!");
        model.addAttribute("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
        model.addAttribute("status", "✅ Сервер работает");

        return "index";  // имя HTML файла (без .html)
    }
}