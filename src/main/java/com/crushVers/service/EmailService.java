package com.crushVers.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Отправка кода подтверждения на почту тест будет исправлен
     */
    public void sendVerificationCode(String toEmail, String nickname, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Подтверждение регистрации в CrushVerse 💕");
        message.setText(
                "Здравствуйте, " + nickname + "!\n\n" +
                        "Спасибо за регистрацию в CrushVerse!\n\n" +
                        "Ваш код подтверждения: " + code + "\n\n" +
                        "Код действителен в течение 5 минут.\n\n" +
                        "Если вы не регистрировались в CrushVerse, просто проигнорируйте это письмо.\n\n" +
                        "С любовью, команда CrushVerse 💕"
        );

        mailSender.send(message);
        System.out.println("📧 Письмо отправлено на " + toEmail + " с кодом: " + code);
    }

    /**
     * Отправка кода для сброса пароля
     */
    public void sendResetPasswordCode(String toEmail, String nickname, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Сброс пароля в CrushVerse 🔐");
        message.setText(
                "Здравствуйте, " + nickname + "!\n\n" +
                        "Вы запросили сброс пароля в CrushVerse.\n\n" +
                        "Ваш код для сброса пароля: " + code + "\n\n" +
                        "Код действителен в течение 5 минут.\n\n" +
                        "Если вы не запрашивали сброс пароля, просто проигнорируйте это письмо.\n\n" +
                        "С любовью, команда CrushVerse 💕"
        );

        mailSender.send(message);
    }

}