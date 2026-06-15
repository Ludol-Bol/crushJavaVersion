package com.crushVers.controller;
import com.crushVers.dto.VerificationRequest;
import com.crushVers.model.User;
import com.crushVers.model.UserRole;
import com.crushVers.model.UserToken;
import com.crushVers.service.EmailService;
import com.crushVers.service.FirestoreService;
import com.crushVers.service.UserRoleService;
import com.crushVers.service.VerificationCodeService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.*;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final FirestoreService firestoreService;
    private final UserRoleService userRoleService;
    private final VerificationCodeService verificationCodeService;
    private final EmailService emailService;

    public AuthController(FirestoreService firestoreService, UserRoleService userRoleService,VerificationCodeService verificationCodeService,
                          EmailService emailService) {
        this.firestoreService = firestoreService;
        this.userRoleService = userRoleService;
        this.verificationCodeService = verificationCodeService;
        this.emailService = emailService;
    }

    /**
     * Автовход
     */
    @GetMapping("/check-auto-login")
    public Map<String, Object> checkAutoLogin(HttpServletRequest request, HttpSession session) {
        // Проверяем, есть ли уже сессия
        if (session.getAttribute("user") != null) {
            return Map.of("authenticated", true, "redirectUrl", "/main-page");
        }

        // Проверяем cookie с токеном
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("auto_login_token".equals(cookie.getName())) {
                    try {
                        // Ищем токен в БД
                        UserToken userToken = firestoreService.findTokenByValue(cookie.getValue());
                        if (userToken != null && userToken.getExpiresAt().after(new Date())) {
                            // Токен валиден — загружаем пользователя
                            User user = firestoreService.findById(userToken.getUserId());
                            if (user != null) {
                                session.setAttribute("user", user);
                                session.setAttribute("userId", user.getId());
                                session.setAttribute("userNickname", user.getNickname());
                                return Map.of("authenticated", true, "redirectUrl", "/main-page");
                            }
                        }
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return Map.of("authenticated", false);
    }


    //авторизация
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginData,
                                     HttpSession session, HttpServletResponse response) {
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

                if (rememberMe) {
                    String token = UUID.randomUUID().toString().replace("-", "");

                    // Сохраняем токен в БД (связываем с пользователем)
                    UserToken userToken = new UserToken();
                    userToken.setUserId(user.getId());
                    userToken.setToken(token);
                    userToken.setCreatedAt(new Date());
                    userToken.setExpiresAt(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000)); // 30 дней
                    firestoreService.saveUserToken(userToken);

                    // Сохраняем токен в cookie
                    Cookie cookie = new Cookie("auto_login_token", token);
                    cookie.setMaxAge(30 * 24 * 60 * 60); // 30 дней
                    cookie.setPath("/");
                    cookie.setHttpOnly(true);  // Защита от XSS
                    cookie.setSecure(false);   // Для localhost (true для HTTPS)
                    response.addCookie(cookie);
                }
                // =========================================

                return Map.of(
                        "success", true,
                        "message", "Добро пожаловать, " + user.getNickname() + "!",
                        "redirectUrl", "/main-page",
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
            if (!verificationCodeService.registrationCode(request.getEmail(), request.getVerificationCode())) {
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
            //делаем роль
            UserRole userRole = userRoleService.findByName("USER");
            if (userRole != null) {
                List<String> roleIds = new ArrayList<>();
                roleIds.add(userRole.getId());
                user.setRoleIds(roleIds);
            }
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


    /**
     *  Запрос на сброс пароля
     */
    @PostMapping("/forgot-password")
    public Map<String, Object> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.trim().isEmpty()) {
            return Map.of("success", false, "message", "Введите email");
        }
        try {
            User user = firestoreService.findByEmail(email);
            if (user == null) {
                return Map.of("success", false, "message", "Пользователь с таким email не найден");
            }
            verificationCodeService.saveAndSendResetCode(email, user.getNickname());
            return Map.of(
                    "success", true,
                    "message", "Код для сброса пароля отправлен на " + email,
                    "email", email
            );

        } catch (ExecutionException | InterruptedException e) {
            return Map.of("success", false, "message", "Ошибка: " + e.getMessage());
        }
    }

    /**
     * Проверка кода сброса пароля
     */
    @PostMapping("/verify-reset-code")
    public Map<String, Object> verifyResetCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");

        if (email == null || code == null) {
            return Map.of("success", false, "message", "Неверные данные");
        }
        boolean isValid = verificationCodeService.restCode(email, code);
        if (isValid) {
            return Map.of(
                    "success", true,
                    "message", "Код подтверждён",
                    "email", email
            );
        } else {
            return Map.of(
                    "success", false,
                    "message", "Неверный или истекший код"
            );
        }
    }

    /**
     *  Установка нового пароля
     */
    @PostMapping("/reset-password")
    public Map<String, Object> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");
        String confirmPassword = request.get("confirmPassword");
        // еще раз проверяем
        if (email == null || newPassword == null || confirmPassword == null) {
            return Map.of("success", false, "message", "Заполните все поля");
        }
        if (newPassword.length() < 6) {
            return Map.of("success", false, "message", "Пароль должен содержать минимум 6 символов");
        }
        if (!newPassword.equals(confirmPassword)) {
            return Map.of("success", false, "message", "Пароли не совпадают");
        }
        try {
            User user = firestoreService.findByEmail(email);
            if (user == null) {
                return Map.of("success", false, "message", "Пользователь не найден");
            }
            String newPasswordHash = firestoreService.hashPassword(newPassword);
            user.setPasswordHash(newPasswordHash);
            firestoreService.saveUser(user);
            verificationCodeService.deleteCode(email);
            return Map.of(
                    "success", true,
                    "message", "Пароль успешно изменён!",
                    "redirectUrl", "/login"
            );

        } catch (Exception e) {
            return Map.of("success", false, "message", "Ошибка: " + e.getMessage());
        }
    }

    //выход
    @PostMapping("/logout")
    public Map<String, Object> logout(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        // Удаляем токен из БД и cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("auto_login_token".equals(cookie.getName())) {
                    try {
                        firestoreService.deleteUserToken(cookie.getValue());
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        }
        session.invalidate();
        return Map.of("success", true, "redirectUrl", "/login");
    }
}