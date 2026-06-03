package com.crushVers.controller;
import com.crushVers.dto.VerificationRequest;
import com.crushVers.model.User;
import com.crushVers.service.EmailService;
import com.crushVers.service.FirestoreService;
import com.crushVers.service.VerificationCodeService;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final FirestoreService firestoreService;
    private final VerificationCodeService verificationCodeService;
    private final EmailService emailService;

    public AuthController(FirestoreService firestoreService, VerificationCodeService verificationCodeService, EmailService emailService) {
        this.firestoreService = firestoreService;
        this.verificationCodeService = verificationCodeService;
        this.emailService = emailService;
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

    // Отправка кода на почту
    @PostMapping("/send-verification")
    public Map<String, Object> sendVerificationCode(@RequestBody VerificationRequest request) {
        try {
            // Проверяем email
            if (firestoreService.findByEmail(request.getEmail())!=null) {
                return Map.of("success", false, "message", "Этот email уже зарегистрирован");
            }

            // Проверяем, не занят ли nickname
            if (firestoreService.findByNickname(request.getNickname())!=null) {
                return Map.of("success", false, "message", "Этот никнейм уже занят");
            }

            // Сохраняем код в Redis и отправляем на почту
            verificationCodeService.saveAndSendCode(request.getEmail(), request.getNickname());

            return Map.of(
                    "success", true,
                    "message", "Код подтверждения отправлен на " + request.getEmail()
            );

        } catch (ExecutionException | InterruptedException e) {
            return Map.of("success", false, "message", "Ошибка: " + e.getMessage());
        }
    }

    //проверка кода и завершение регистрации
    @PostMapping("/verify-and-register")
    public Map<String, Object> verifyAndRegister(@RequestBody VerificationRequest request) {
        try {
            // Проверяем код в Redis
            if (!verificationCodeService.verifyCode(request.getEmail(), request.getVerificationCode())) {
                return Map.of("success", false, "message", "Неверный или истекший код подтверждения");
            }

            // Проверяем еще раз уникальность
            if (firestoreService.findByEmail(request.getEmail())!=null) {
                return Map.of("success", false, "message", "Этот email уже зарегистрирован");
            }

            if (firestoreService.findByEmail(request.getNickname())!=null) {
                return Map.of("success", false, "message", "Этот никнейм уже занят");
            }

            // Создаем пользователя
            User user = new User();
            user.setEmail(request.getEmail());
            user.setNickname(request.getNickname());
            user.setPasswordHash(firestoreService.hashPassword(request.getPassword()));
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
            user.setBirthDate(dateFormat.parse(request.getBirthDate()));
            user.setCreatedAt(new java.util.Date());
            firestoreService.saveUser(user);
            // Удаляем код из Redis
            verificationCodeService.deleteCode(request.getEmail());
            return Map.of(
                    "success", true,
                    "message", "Регистрация успешна!",
                    "redirectUrl", "/login"
            );

        } catch (Exception e) {
            return Map.of("success", false, "message", "Ошибка: " + e.getMessage());
        }
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