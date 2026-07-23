package com.crushVers.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationCodeServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private VerificationCodeService verificationCodeService;

    private final String TEST_EMAIL = "test@example.com";
    private final String TEST_NICKNAME = "testuser";
    private final String TEST_CODE = "123456";


    @Test
    void testGenerateCode_returns6Digits() {
        String code = verificationCodeService.generateCode();
        assertNotNull(code);
        assertEquals(6, code.length());
        assertTrue(code.matches("\\d{6}"));
    }

    @Test
    void testGenerateCode_returnsDifferentCodes() {
        String code1 = verificationCodeService.generateCode();
        String code2 = verificationCodeService.generateCode();
        assertNotEquals(code1, code2);
    }

    @Test
    void testGenerateCode_rangeIsCorrect() {
        for (int i = 0; i < 100; i++) {
            String code = verificationCodeService.generateCode();
            int codeInt = Integer.parseInt(code);
            assertTrue(codeInt >= 100000 && codeInt <= 999999);
        }
    }

    @Test
    void testSaveAndSendCode_savesToRedis() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        verificationCodeService.saveAndSendCode(TEST_EMAIL, TEST_NICKNAME);
        verify(valueOperations, times(1)).set(
                eq("verification:" + TEST_EMAIL),
                anyString(),
                eq(5L),
                eq(TimeUnit.MINUTES)
        );
    }

    @Test
    void testSaveAndSendCode_sendsEmail() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        verificationCodeService.saveAndSendCode(TEST_EMAIL, TEST_NICKNAME);
        verify(emailService, times(1)).sendVerificationCode(
                eq(TEST_EMAIL),
                eq(TEST_NICKNAME),
                anyString()
        );
    }

    @Test
    void testSaveAndSendCode_codeLength() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        ArgumentCaptor<String> codeCaptor = ArgumentCaptor.forClass(String.class);
        verificationCodeService.saveAndSendCode(TEST_EMAIL, TEST_NICKNAME);
        verify(valueOperations).set(
                eq("verification:" + TEST_EMAIL),
                codeCaptor.capture(),
                eq(5L),
                eq(TimeUnit.MINUTES)
        );

        String capturedCode = codeCaptor.getValue();
        assertEquals(6, capturedCode.length());
        assertTrue(capturedCode.matches("\\d{6}"));
    }

    @Test
    void testSaveAndSendCode_sameCodeInEmailAndRedis() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        ArgumentCaptor<String> redisCodeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> emailCodeCaptor = ArgumentCaptor.forClass(String.class);
        verificationCodeService.saveAndSendCode(TEST_EMAIL, TEST_NICKNAME);
        verify(valueOperations).set(
                eq("verification:" + TEST_EMAIL),
                redisCodeCaptor.capture(),
                eq(5L),
                eq(TimeUnit.MINUTES)
        );
        verify(emailService).sendVerificationCode(
                eq(TEST_EMAIL),
                eq(TEST_NICKNAME),
                emailCodeCaptor.capture()
        );
        assertEquals(redisCodeCaptor.getValue(), emailCodeCaptor.getValue());
    }

    @Test
    void testRegistrationCode_correctCode() {
        String key = "verification:" + TEST_EMAIL;
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(TEST_CODE);
        boolean result = verificationCodeService.registrationCode(TEST_EMAIL, TEST_CODE);
        assertTrue(result);
        verify(redisTemplate, times(1)).delete(key);
    }

    @Test
    void testRegistrationCode_wrongCode() {
        String key = "verification:" + TEST_EMAIL;
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(TEST_CODE);
        boolean result = verificationCodeService.registrationCode(TEST_EMAIL, "999999");
        assertFalse(result);
        verify(redisTemplate, never()).delete(key);
    }

    @Test
    void testRegistrationCode_codeNotFound() {
        String key = "verification:" + TEST_EMAIL;
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(null);
        boolean result = verificationCodeService.registrationCode(TEST_EMAIL, TEST_CODE);
        assertFalse(result);
        verify(redisTemplate, never()).delete(key);
    }

    @Test
    void testRegistrationCode_deletesAfterSuccess() {
        String key = "verification:" + TEST_EMAIL;
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(TEST_CODE);
        verificationCodeService.registrationCode(TEST_EMAIL, TEST_CODE);
        verify(redisTemplate, times(1)).delete(key);
    }

    @Test
    void testRegistrationCode_notDeletesAfterFailure() {
        String key = "verification:" + TEST_EMAIL;
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(TEST_CODE);
        verificationCodeService.registrationCode(TEST_EMAIL, "999999");
        verify(redisTemplate, never()).delete(key);
    }


    @Test
    void testRestCode_correctCode() {
        String key = "reset:" + TEST_EMAIL;
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(TEST_CODE);
        boolean result = verificationCodeService.restCode(TEST_EMAIL, TEST_CODE);
        assertTrue(result);
        verify(redisTemplate, times(1)).delete(key);
    }

    @Test
    void testRestCode_wrongCode() {
        String key = "reset:" + TEST_EMAIL;
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(TEST_CODE);
        boolean result = verificationCodeService.restCode(TEST_EMAIL, "999999");
        assertFalse(result);
        verify(redisTemplate, never()).delete(key);
    }

    @Test
    void testRestCode_codeNotFound() {
        String key = "reset:" + TEST_EMAIL;
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(null);
        boolean result = verificationCodeService.restCode(TEST_EMAIL, TEST_CODE);
        assertFalse(result);
        verify(redisTemplate, never()).delete(key);
    }

    @Test
    void testRestCode_deletesAfterSuccess() {
        String key = "reset:" + TEST_EMAIL;
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(TEST_CODE);
        verificationCodeService.restCode(TEST_EMAIL, TEST_CODE);
        verify(redisTemplate, times(1)).delete(key);
    }

    @Test
    void testRestCode_notDeletesAfterFailure() {
        String key = "reset:" + TEST_EMAIL;
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(TEST_CODE);
        verificationCodeService.restCode(TEST_EMAIL, "999999");
        verify(redisTemplate, never()).delete(key);
    }

    @Test
    void testSaveAndSendResetCode_savesToRedis() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        verificationCodeService.saveAndSendResetCode(TEST_EMAIL, TEST_NICKNAME);
        verify(valueOperations, times(1)).set(
                eq("reset:" + TEST_EMAIL),
                anyString(),
                eq(5L),
                eq(TimeUnit.MINUTES)
        );
    }

    @Test
    void testSaveAndSendResetCode_sendsEmail() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        verificationCodeService.saveAndSendResetCode(TEST_EMAIL, TEST_NICKNAME);
        verify(emailService, times(1)).sendResetPasswordCode(
                eq(TEST_EMAIL),
                eq(TEST_NICKNAME),
                anyString()
        );
    }

    @Test
    void testSaveAndSendResetCode_correctPrefix() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verificationCodeService.saveAndSendResetCode(TEST_EMAIL, TEST_NICKNAME);
        verify(valueOperations).set(
                keyCaptor.capture(),
                anyString(),
                eq(5L),
                eq(TimeUnit.MINUTES)
        );
        assertTrue(keyCaptor.getValue().startsWith("reset:"));
    }

    @Test
    void testHasCode_whenCodeExists() {
        String key = "verification:" + TEST_EMAIL;
        when(redisTemplate.hasKey(key)).thenReturn(true);
        boolean result = verificationCodeService.hasCode(TEST_EMAIL);
        assertTrue(result);
    }

    @Test
    void testHasCode_whenCodeNotExists() {
        String key = "verification:" + TEST_EMAIL;
        when(redisTemplate.hasKey(key)).thenReturn(false);
        boolean result = verificationCodeService.hasCode(TEST_EMAIL);
        assertFalse(result);
    }

    @Test
    void testDeleteCode() {
        String key = "verification:" + TEST_EMAIL;
        verificationCodeService.deleteCode(TEST_EMAIL);
        verify(redisTemplate, times(1)).delete(key);
    }

    @Test
    void testDeleteCode_withDifferentEmail() {
        String email1 = "user1@example.com";
        String email2 = "user2@example.com";
        verificationCodeService.deleteCode(email1);
        verify(redisTemplate, times(1)).delete("verification:" + email1);
        verify(redisTemplate, never()).delete("verification:" + email2);
    }

    @Test
    void testGetRemainingTTL() {
        String key = "verification:" + TEST_EMAIL;
        long expectedTTL = 300;
        when(redisTemplate.getExpire(key, TimeUnit.SECONDS)).thenReturn(expectedTTL);
        Long result = verificationCodeService.getRemainingTTL(TEST_EMAIL);
        assertEquals(expectedTTL, result);
    }

    @Test
    void testGetRemainingTTL_whenKeyNotFound() {
        String key = "verification:" + TEST_EMAIL;
        when(redisTemplate.getExpire(key, TimeUnit.SECONDS)).thenReturn(-2L);
        Long result = verificationCodeService.getRemainingTTL(TEST_EMAIL);
        assertEquals(-2L, result);
    }

    @Test
    void testRegistrationAndResetCodesDontConflict() {
        String registrationKey = "verification:" + TEST_EMAIL;
        String resetKey = "reset:" + TEST_EMAIL;
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(registrationKey)).thenReturn("111111");
        when(valueOperations.get(resetKey)).thenReturn("222222");
        boolean regResult = verificationCodeService.registrationCode(TEST_EMAIL, "111111");
        assertTrue(regResult);
        verify(redisTemplate, times(1)).delete(registrationKey);
        boolean resetResult = verificationCodeService.restCode(TEST_EMAIL, "222222");
        assertTrue(resetResult);
        verify(redisTemplate, times(1)).delete(resetKey);
    }
}