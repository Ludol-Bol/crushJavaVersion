package com.crushVers.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class UserTokenTest {

    private UserToken userToken;
    private Date testCreatedAt;
    private Date testExpiresAt;
    private static final long DAY_IN_MILLIS = 24 * 60 * 60 * 1000L;

    @BeforeEach
    void setUp() {
        testCreatedAt = new Date();
        testExpiresAt = new Date(System.currentTimeMillis() + 30 * DAY_IN_MILLIS);
        userToken = new UserToken();
        userToken.setId("token-id-123");
        userToken.setUserId("user-id-456");
        userToken.setToken("abc123def456");
        userToken.setCreatedAt(testCreatedAt);
        userToken.setExpiresAt(testExpiresAt);
    }

    @Test
    void testDefaultConstructor() {
        UserToken emptyToken = new UserToken();
        assertNotNull(emptyToken);
        assertNull(emptyToken.getId());
        assertNull(emptyToken.getUserId());
        assertNull(emptyToken.getToken());
        assertNull(emptyToken.getCreatedAt());
        assertNull(emptyToken.getExpiresAt());
    }

    @Test
    void testParameterizedConstructor() {
        String userId = "user-789";
        String token = "xyz789abc123";
        int daysValid = 30;
        UserToken newToken = new UserToken(userId, token, daysValid);
        assertEquals(userId, newToken.getUserId());
        assertEquals(token, newToken.getToken());
        assertNotNull(newToken.getCreatedAt());
        assertNotNull(newToken.getExpiresAt());
        long expectedExpiry = newToken.getCreatedAt().getTime() + daysValid * DAY_IN_MILLIS;
        long actualExpiry = newToken.getExpiresAt().getTime();
        assertTrue(Math.abs(expectedExpiry - actualExpiry) < 1000); // погрешность < 1 секунды
    }

    @Test
    void testParameterizedConstructorWithDifferentDays() {
        // Тест на 1 день
        UserToken oneDayToken = new UserToken("user1", "token1", 1);
        long oneDayExpiry = oneDayToken.getCreatedAt().getTime() + DAY_IN_MILLIS;
        assertTrue(Math.abs(oneDayExpiry - oneDayToken.getExpiresAt().getTime()) < 1000);

        // Тест на 7 дней
        UserToken weekToken = new UserToken("user2", "token2", 7);
        long weekExpiry = weekToken.getCreatedAt().getTime() + 7 * DAY_IN_MILLIS;
        assertTrue(Math.abs(weekExpiry - weekToken.getExpiresAt().getTime()) < 1000);

        // Тест на 365 дней (год)
        UserToken yearToken = new UserToken("user3", "token3", 365);
        long yearExpiry = yearToken.getCreatedAt().getTime() + 365 * DAY_IN_MILLIS;
        assertTrue(Math.abs(yearExpiry - yearToken.getExpiresAt().getTime()) < 1000);
    }


    @Test
    void testGetId() {
        assertEquals("token-id-123", userToken.getId());
    }

    @Test
    void testSetId() {
        userToken.setId("new-token-id");
        assertEquals("new-token-id", userToken.getId());
    }

    @Test
    void testSetIdNull() {
        userToken.setId(null);
        assertNull(userToken.getId());
    }

    @Test
    void testGetUserId() {
        assertEquals("user-id-456", userToken.getUserId());
    }

    @Test
    void testSetUserId() {
        userToken.setUserId("new-user-id");
        assertEquals("new-user-id", userToken.getUserId());
    }

    @Test
    void testSetUserIdNull() {
        userToken.setUserId(null);
        assertNull(userToken.getUserId());
    }

    @Test
    void testGetToken() {
        assertEquals("abc123def456", userToken.getToken());
    }

    @Test
    void testSetToken() {
        userToken.setToken("new_token_789");
        assertEquals("new_token_789", userToken.getToken());
    }

    @Test
    void testSetTokenNull() {
        userToken.setToken(null);
        assertNull(userToken.getToken());
    }

    @Test
    void testSetTokenEmpty() {
        userToken.setToken("");
        assertEquals("", userToken.getToken());
    }

    @Test
    void testGetCreatedAt() {
        assertEquals(testCreatedAt, userToken.getCreatedAt());
    }

    @Test
    void testSetCreatedAt() {
        Date newDate = new Date(System.currentTimeMillis() - 1000000);
        userToken.setCreatedAt(newDate);
        assertEquals(newDate, userToken.getCreatedAt());
    }

    @Test
    void testSetCreatedAtNull() {
        userToken.setCreatedAt(null);
        assertNull(userToken.getCreatedAt());
    }

    @Test
    void testGetExpiresAt() {
        assertEquals(testExpiresAt, userToken.getExpiresAt());
    }

    @Test
    void testSetExpiresAt() {
        Date newExpiry = new Date(System.currentTimeMillis() + 60 * DAY_IN_MILLIS);
        userToken.setExpiresAt(newExpiry);
        assertEquals(newExpiry, userToken.getExpiresAt());
    }

    @Test
    void testSetExpiresAtNull() {
        userToken.setExpiresAt(null);
        assertNull(userToken.getExpiresAt());
    }

    // ===== ТЕСТЫ ДЛЯ ПРОВЕРКИ СРОКОВ =====

    @Test
    void testTokenIsNotExpired() {
        UserToken freshToken = new UserToken("user", "token", 30);
        assertTrue(freshToken.getExpiresAt().after(new Date()));
    }

    @Test
    void testExpiredToken() {
        UserToken expiredToken = new UserToken("user", "token", 0);
        // Устанавливаем expiresAt в прошлое
        expiredToken.setExpiresAt(new Date(System.currentTimeMillis() - 1000));
        assertTrue(expiredToken.getExpiresAt().before(new Date()));
    }

    @Test
    void testExpiresAtAfterCreatedAt() {
        UserToken token = new UserToken("user", "token", 30);
        assertTrue(token.getExpiresAt().after(token.getCreatedAt()));
    }

    // уникальность
    @Test
    void testDifferentTokensAreDifferent() {
        UserToken token1 = new UserToken("user1", "token123", 30);
        UserToken token2 = new UserToken("user1", "token456", 30);

        assertNotEquals(token1.getToken(), token2.getToken());
    }

    @Test
    void testSameUserDifferentTokens() {
        String sameUserId = "user123";
        UserToken token1 = new UserToken(sameUserId, "token_abc", 30);
        UserToken token2 = new UserToken(sameUserId, "token_xyz", 30);

        assertEquals(token1.getUserId(), token2.getUserId());
        assertNotEquals(token1.getToken(), token2.getToken());
    }

    @Test
    void testZeroDaysValid() {
        UserToken zeroDayToken = new UserToken("user", "token", 0);
        // expiresAt должен быть примерно равен createdAt
        long diff = zeroDayToken.getExpiresAt().getTime() - zeroDayToken.getCreatedAt().getTime();
        assertTrue(diff >= 0 && diff < 1000);
    }

    @Test
    void testNegativeDaysValid() {
        // Отрицательное количество дней должно работать?
        UserToken negativeToken = new UserToken("user", "token", -5);
        // expiresAt будет меньше createdAt
        assertTrue(negativeToken.getExpiresAt().before(negativeToken.getCreatedAt()));
    }

    @Test
    void testVeryLongToken() {
        String longToken = "a".repeat(1000);
        userToken.setToken(longToken);
        assertEquals(1000, userToken.getToken().length());
    }

    @Test
    void testVeryLongUserId() {
        String longUserId = "b".repeat(500);
        userToken.setUserId(longUserId);
        assertEquals(500, userToken.getUserId().length());
    }

    @Test
    void testMaxDaysValid() {
        int maxDays = 365 * 10; // 10 лет
        UserToken longTermToken = new UserToken("user", "token", maxDays);
        long expectedDiff = (long) maxDays * DAY_IN_MILLIS;
        long actualDiff = longTermToken.getExpiresAt().getTime() - longTermToken.getCreatedAt().getTime();
        assertTrue(Math.abs(expectedDiff - actualDiff) < 1000);
    }

    @Test
    void testTokensForDifferentUsers() {
        UserToken tokenUser1 = new UserToken("user1", "token1", 30);
        UserToken tokenUser2 = new UserToken("user2", "token2", 30);
        assertNotEquals(tokenUser1.getUserId(), tokenUser2.getUserId());
        assertNotEquals(tokenUser1.getToken(), tokenUser2.getToken());
    }
}