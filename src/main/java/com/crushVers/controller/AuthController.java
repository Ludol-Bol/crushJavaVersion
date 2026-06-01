package com.crushVers.controller;
import com.crushVers.model.User;
import com.crushVers.service.FirestoreService;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final FirestoreService firestoreService;

    public AuthController(FirestoreService firestoreService) {
        this.firestoreService = firestoreService;
    }


    //авторизация
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginData,
                                     HttpSession session) {
        // получение введенных данных
        String login = loginData.get("username");
        String password = loginData.get("password");
        boolean rememberMe = Boolean.parseBoolean(loginData.get("rememberMe"));

        // Валидация
        if (login == null || login.trim().isEmpty()) {
            return Map.of("success", false, "message", "Введите логин или email");
        }
        if (password == null || password.trim().isEmpty()) {
            return Map.of("success", false, "message", "Введите пароль");
        }

        try {
            // поиск пользователя
            User user = firestoreService.findByEmailOrNickname(login);
            if (user == null) {
                    return Map.of(
                            "success", false,
                            "message", "Пользователь с таким логином/email не найден"
                    );
            }

            // Проверяем пароль
            boolean passwordMatches = firestoreService.checkPassword(password, user.getPasswordHash());

            if (passwordMatches) {
                session.setAttribute("user", user);
                session.setAttribute("userId", user.getId());
                session.setAttribute("userNickname", user.getNickname());
                return Map.of(
                        "success", true,
                        "message", "Добро пожаловать, " + user.getNickname() + "!",
                        "redirectUrl", "/dashboard",
                        "user", Map.of(
                                "id", user.getId(),
                                "email", user.getEmail(),
                                "nickname", user.getNickname()
                        )
                );
            } else {
                return Map.of(
                        "success", false,
                        "message", "Неверный пароль"
                );
            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return Map.of(
                    "success", false,
                    "message", "Ошибка базы данных: " + e.getMessage()
            );
        }
    }

    // проверка на авторизацию
    @GetMapping("/check-auth")
    public Map<String, Object> checkAuth(HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user != null) {
            return Map.of(
                    "authenticated", true,
                    "userId", user.getId(),
                    "email", user.getEmail(),
                    "username", user.getNickname()
            );
        }
        return Map.of("authenticated", false);
    }

    //выход
    @PostMapping("/logout")
    public Map<String, Object> logout(HttpSession session) {
        session.invalidate();
        return Map.of(
                "success", true,
                "message", "Вы вышли из системы",
                "redirectUrl", "/login"
        );
    }
}