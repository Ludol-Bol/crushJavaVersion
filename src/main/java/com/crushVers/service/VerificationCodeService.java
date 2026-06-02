package com.crushVers.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
public class VerificationCodeService {

    private final StringRedisTemplate redisTemplate;
    private final EmailService emailService;

    public VerificationCodeService(StringRedisTemplate redisTemplate, EmailService emailService) {
        this.redisTemplate = redisTemplate;
        this.emailService = emailService;
    }

    /**
     * Генерация 6-значного кода
     */
    public String generateCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    /**
     * Сохранение кода в Redis и отправка на почту
     */
    public void saveAndSendCode(String email, String nickname) {
        // Генерируем код
        String code = generateCode();
        // Сохраняем в Redis с TTL 5 минут (300 секунд)
        String key = "verification:" + email;
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);
        // Отправляем на почту
        emailService.sendVerificationCode(email, nickname, code);
        System.out.println("📧 Код " + code + " отправлен на " + email + " и сохранен в Redis");
    }

    /**
     * Проверка кода из Redis
     */
    public boolean verifyCode(String email, String code) {
        String key = "verification:" + email;
        String savedCode = redisTemplate.opsForValue().get(key);

        if (savedCode == null) {
            System.out.println("❌ Код не найден в Redis для " + email);
            return false;
        }

        if (savedCode.equals(code)) {
            // Удаляем код после успешной проверки
            redisTemplate.delete(key);
            System.out.println("✅ Код подтвержден для " + email);
            return true;
        }

        System.out.println("❌ Неверный код для " + email + ". Ожидался: " + savedCode);
        return false;
    }

    /**
     * Проверка, есть ли код для email
     */
    public boolean hasCode(String email) {
        String key = "verification:" + email;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * Удаление кода
     */
    public void deleteCode(String email) {
        String key = "verification:" + email;
        redisTemplate.delete(key);
        System.out.println("🗑️ Код удален из Redis для " + email);
    }

    /**
     * Получить оставшееся время жизни кода в секундах
     */
    public Long getRemainingTTL(String email) {
        String key = "verification:" + email;
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }
}
