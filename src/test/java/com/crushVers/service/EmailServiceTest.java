package com.crushVers.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private final String TEST_EMAIL = "test@example.com";
    private final String TEST_NICKNAME = "testuser";
    private final String TEST_CODE = "123456";
    private final String FROM_EMAIL = "noreply@crushverse.com";

    @BeforeEach
    void setUp() {
        // Используем рефлексию для установки fromEmail, т.к. @Value не работает в тестах
        try {
            var field = EmailService.class.getDeclaredField("fromEmail");
            field.setAccessible(true);
            field.set(emailService, FROM_EMAIL);
        } catch (Exception e) {
            fail("Не удалось установить fromEmail: " + e.getMessage());
        }
    }

    // тесты sendVerificationCode

    @Test
    void testSendVerificationCode_success() {
        // Вызываем метод
        emailService.sendVerificationCode(TEST_EMAIL, TEST_NICKNAME, TEST_CODE);
        // Проверяем, что mailSender.send() был вызван 1 раз
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendVerificationCode_messageContent() {
        // перехвадчик сообшения
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        // Вызываем метод
        emailService.sendVerificationCode(TEST_EMAIL, TEST_NICKNAME, TEST_CODE);
        // Перехватываем сообщение
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage message = messageCaptor.getValue();
        // Проверяем содержимое письма
        assertEquals(FROM_EMAIL, message.getFrom());
        assertEquals(TEST_EMAIL, message.getTo()[0]);
        assertEquals("Подтверждение регистрации в CrushVerse 💕", message.getSubject());
        assertTrue(message.getText().contains(TEST_NICKNAME));
        assertTrue(message.getText().contains(TEST_CODE));
        assertTrue(message.getText().contains("5 минут"));
    }

    @Test
    void testSendVerificationCode_withSpecialCharacters() {
        String specialNickname = "Test_User-123";
        String specialCode = "000000";
        emailService.sendVerificationCode(TEST_EMAIL, specialNickname, specialCode);
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage message = messageCaptor.getValue();
        assertTrue(message.getText().contains(specialNickname));
        assertTrue(message.getText().contains(specialCode));
    }

    @Test
    void testSendVerificationCode_emailWithPlus() {
        String emailWithPlus = "test+alias@example.com";
        emailService.sendVerificationCode(emailWithPlus, TEST_NICKNAME, TEST_CODE);
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage message = messageCaptor.getValue();
        assertEquals(emailWithPlus, message.getTo()[0]);
    }

    @Test
    void testSendVerificationCode_longNickname() {
        String longNickname = "a".repeat(50);
        emailService.sendVerificationCode(TEST_EMAIL, longNickname, TEST_CODE);
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage message = messageCaptor.getValue();
        assertTrue(message.getText().contains(longNickname));
    }

    // тесты для sendResetPasswordCode

    @Test
    void testSendResetPasswordCode_success() {
        emailService.sendResetPasswordCode(TEST_EMAIL, TEST_NICKNAME, TEST_CODE);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendResetPasswordCode_messageContent() {
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        emailService.sendResetPasswordCode(TEST_EMAIL, TEST_NICKNAME, TEST_CODE);
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage message = messageCaptor.getValue();
        assertEquals(FROM_EMAIL, message.getFrom());
        assertEquals(TEST_EMAIL, message.getTo()[0]);
        assertEquals("Сброс пароля в CrushVerse 🔐", message.getSubject());
        assertTrue(message.getText().contains(TEST_NICKNAME));
        assertTrue(message.getText().contains(TEST_CODE));
        assertTrue(message.getText().contains("5 минут"));
        assertTrue(message.getText().contains("сброс пароля"));
    }

    @Test
    void testSendResetPasswordCode_differentCodes() {
        String code1 = "111111";
        String code2 = "999999";
        emailService.sendResetPasswordCode(TEST_EMAIL, TEST_NICKNAME, code1);
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage message = messageCaptor.getValue();
        assertTrue(message.getText().contains(code1));
        assertFalse(message.getText().contains(code2));
    }

    // тесты для проверки полей

    @Test
    void testFromEmailIsSet() {
        assertNotNull(FROM_EMAIL);
    }

    @Test
    void testMailSenderIsCalled() {
        emailService.sendVerificationCode(TEST_EMAIL, TEST_NICKNAME, TEST_CODE);
        verify(mailSender, atLeastOnce()).send(any(SimpleMailMessage.class));
    }

    @Test
    void testBothMethodsUseCorrectSubject() {
        // Проверяем, что письма имеют разные subject
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        emailService.sendVerificationCode(TEST_EMAIL, TEST_NICKNAME, TEST_CODE);
        emailService.sendResetPasswordCode(TEST_EMAIL, TEST_NICKNAME, TEST_CODE);
        verify(mailSender, times(2)).send(messageCaptor.capture());
        var messages = messageCaptor.getAllValues();
        assertEquals("Подтверждение регистрации в CrushVerse 💕", messages.get(0).getSubject());
        assertEquals("Сброс пароля в CrushVerse 🔐", messages.get(1).getSubject());
        assertNotEquals(messages.get(0).getSubject(), messages.get(1).getSubject());
    }

    @Test
    void testBothMethodsHaveCorrectContentType() {
        // Проверяем, что оба письма содержат текст
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        emailService.sendVerificationCode(TEST_EMAIL, TEST_NICKNAME, TEST_CODE);
        emailService.sendResetPasswordCode(TEST_EMAIL, TEST_NICKNAME, TEST_CODE);
        verify(mailSender, times(2)).send(messageCaptor.capture());
        var messages = messageCaptor.getAllValues();
        assertNotNull(messages.get(0).getText());
        assertNotNull(messages.get(1).getText());
    }

    // тесты при ошибках

    @Test
    void testMailSenderThrowsException() {
        // Настраиваем мок: при вызове send выбрасываем исключение
        doThrow(new RuntimeException("SMTP error")).when(mailSender).send(any(SimpleMailMessage.class));
        // Метод должен выбросить исключение
        assertThrows(RuntimeException.class, () -> {
            emailService.sendVerificationCode(TEST_EMAIL, TEST_NICKNAME, TEST_CODE);
        });
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testMailSenderThrowsExceptionForResetPassword() {
        doThrow(new RuntimeException("SMTP error")).when(mailSender).send(any(SimpleMailMessage.class));
        assertThrows(RuntimeException.class, () -> {
            emailService.sendResetPasswordCode(TEST_EMAIL, TEST_NICKNAME, TEST_CODE);
        });
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}