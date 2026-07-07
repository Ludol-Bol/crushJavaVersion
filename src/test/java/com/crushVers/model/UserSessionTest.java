package com.crushVers.model;

import com.crushVers.enums.LogoutReason;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class UserSessionTest {

    private UserSession userSession;
    private final String TEST_USER_ID = "user-123";
    private final String TEST_SESSION_ID = "session-456";
    private Date testDate;

    @BeforeEach
    void setUp() {
        testDate = new Date();
        userSession = new UserSession(TEST_USER_ID, TEST_SESSION_ID);
    }

    // тесты для конструкторов

    @Test
    void testDefaultConstructor() {
        UserSession emptySession = new UserSession();
        assertNotNull(emptySession);
        assertNull(emptySession.getId());
        assertNull(emptySession.getUserId());
        assertNull(emptySession.getSessionId());
        assertNull(emptySession.getIpAddress());
        assertNull(emptySession.getUserAgent());
        assertNull(emptySession.getDeviceType());
        assertNull(emptySession.getBrowser());
        assertNull(emptySession.getOs());
        assertNull(emptySession.getLoginTime());
        assertNull(emptySession.getLastActivity());
        assertNull(emptySession.getExpiresAt());
        assertEquals(0, emptySession.getSessionDuration());
        assertFalse(emptySession.isActive());
        assertNull(emptySession.getLogoutReason());
    }

    @Test
    void testParameterizedConstructor() {
        assertEquals(TEST_USER_ID, userSession.getUserId());
        assertEquals(TEST_SESSION_ID, userSession.getSessionId());
        assertNotNull(userSession.getLoginTime());
        assertNotNull(userSession.getLastActivity());
        assertTrue(userSession.isActive());
        assertEquals(0, userSession.getSessionDuration());
        assertNull(userSession.getLogoutReason());
    }

    @Test
    void testParameterizedConstructorCreatesDifferentSessions() {
        UserSession session1 = new UserSession("user1", "session1");
        UserSession session2 = new UserSession("user2", "session2");

        assertNotEquals(session1.getUserId(), session2.getUserId());
        assertNotEquals(session1.getSessionId(), session2.getSessionId());
    }

    // тесты для геттеров и сеттеров

    @Test
    void testGetId() {
        assertNull(userSession.getId());
        userSession.setId("doc-123");
        assertEquals("doc-123", userSession.getId());
    }

    @Test
    void testSetId() {
        userSession.setId("new-doc-id");
        assertEquals("new-doc-id", userSession.getId());
    }

    @Test
    void testSetIdNull() {
        userSession.setId(null);
        assertNull(userSession.getId());
    }

    @Test
    void testGetUserId() {
        assertEquals(TEST_USER_ID, userSession.getUserId());
    }

    @Test
    void testSetUserId() {
        userSession.setUserId("new-user-id");
        assertEquals("new-user-id", userSession.getUserId());
    }

    @Test
    void testSetUserIdNull() {
        userSession.setUserId(null);
        assertNull(userSession.getUserId());
    }

    @Test
    void testGetSessionId() {
        assertEquals(TEST_SESSION_ID, userSession.getSessionId());
    }

    @Test
    void testSetSessionId() {
        userSession.setSessionId("new-session-id");
        assertEquals("new-session-id", userSession.getSessionId());
    }

    @Test
    void testSetSessionIdNull() {
        userSession.setSessionId(null);
        assertNull(userSession.getSessionId());
    }

    @Test
    void testGetIpAddress() {
        assertNull(userSession.getIpAddress());
        userSession.setIpAddress("192.168.1.1");
        assertEquals("192.168.1.1", userSession.getIpAddress());
    }

    @Test
    void testSetIpAddress() {
        userSession.setIpAddress("10.0.0.1");
        assertEquals("10.0.0.1", userSession.getIpAddress());
    }

    @Test
    void testSetIpAddressNull() {
        userSession.setIpAddress(null);
        assertNull(userSession.getIpAddress());
    }

    @Test
    void testGetUserAgent() {
        assertNull(userSession.getUserAgent());
        userSession.setUserAgent("Mozilla/5.0 (Windows NT 10.0) Chrome/120");
        assertEquals("Mozilla/5.0 (Windows NT 10.0) Chrome/120", userSession.getUserAgent());
    }

    @Test
    void testSetUserAgent() {
        userSession.setUserAgent("Mozilla/5.0 (Macintosh) Safari");
        assertEquals("Mozilla/5.0 (Macintosh) Safari", userSession.getUserAgent());
    }

    @Test
    void testSetUserAgentNull() {
        userSession.setUserAgent(null);
        assertNull(userSession.getUserAgent());
    }

    @Test
    void testGetDeviceType() {
        assertNull(userSession.getDeviceType());
        userSession.setDeviceType("web");
        assertEquals("web", userSession.getDeviceType());
    }

    @Test
    void testSetDeviceType() {
        userSession.setDeviceType("mobile");
        assertEquals("mobile", userSession.getDeviceType());
    }

    @Test
    void testSetDeviceTypeNull() {
        userSession.setDeviceType(null);
        assertNull(userSession.getDeviceType());
    }

    @Test
    void testGetBrowser() {
        assertNull(userSession.getBrowser());
        userSession.setBrowser("Chrome");
        assertEquals("Chrome", userSession.getBrowser());
    }

    @Test
    void testSetBrowser() {
        userSession.setBrowser("Firefox");
        assertEquals("Firefox", userSession.getBrowser());
    }

    @Test
    void testSetBrowserNull() {
        userSession.setBrowser(null);
        assertNull(userSession.getBrowser());
    }

    @Test
    void testGetOs() {
        assertNull(userSession.getOs());
        userSession.setOs("Windows");
        assertEquals("Windows", userSession.getOs());
    }

    @Test
    void testSetOs() {
        userSession.setOs("macOS");
        assertEquals("macOS", userSession.getOs());
    }

    @Test
    void testSetOsNull() {
        userSession.setOs(null);
        assertNull(userSession.getOs());
    }

    @Test
    void testGetLoginTime() {
        assertNotNull(userSession.getLoginTime());
        Date oldDate = userSession.getLoginTime();
        userSession.setLoginTime(testDate);
        assertEquals(testDate, userSession.getLoginTime());
    }

    @Test
    void testSetLoginTime() {
        Date newDate = new Date(System.currentTimeMillis() + 100000);
        userSession.setLoginTime(newDate);
        assertEquals(newDate, userSession.getLoginTime());
    }

    @Test
    void testSetLoginTimeNull() {
        userSession.setLoginTime(null);
        assertNull(userSession.getLoginTime());
    }

    @Test
    void testGetLastActivity() {
        assertNotNull(userSession.getLastActivity());
    }

    @Test
    void testSetLastActivity() {
        Date newDate = new Date();
        userSession.setLastActivity(newDate);
        assertEquals(newDate, userSession.getLastActivity());
    }

    @Test
    void testSetLastActivityNull() {
        userSession.setLastActivity(null);
        assertNull(userSession.getLastActivity());
    }

    @Test
    void testGetExpiresAt() {
        assertNull(userSession.getExpiresAt());
        userSession.setExpiresAt(new Date());
        assertNotNull(userSession.getExpiresAt());
    }

    @Test
    void testSetExpiresAt() {
        Date expiryDate = new Date(System.currentTimeMillis() + 30 * 60 * 1000);
        userSession.setExpiresAt(expiryDate);
        assertEquals(expiryDate, userSession.getExpiresAt());
    }

    @Test
    void testSetExpiresAtNull() {
        userSession.setExpiresAt(null);
        assertNull(userSession.getExpiresAt());
    }

    @Test
    void testGetSessionDuration() {
        assertEquals(0, userSession.getSessionDuration());
        userSession.setSessionDuration(120);
        assertEquals(120, userSession.getSessionDuration());
    }

    @Test
    void testSetSessionDuration() {
        userSession.setSessionDuration(300);
        assertEquals(300, userSession.getSessionDuration());
    }

    @Test
    void testSetSessionDurationZero() {
        userSession.setSessionDuration(0);
        assertEquals(0, userSession.getSessionDuration());
    }

    @Test
    void testGetActive() {
        assertTrue(userSession.isActive());
        userSession.setActive(false);
        assertFalse(userSession.isActive());
    }

    @Test
    void testSetActive() {
        userSession.setActive(false);
        assertFalse(userSession.isActive());
        userSession.setActive(true);
        assertTrue(userSession.isActive());
    }

    @Test
    void testGetLogoutReason() {
        assertNull(userSession.getLogoutReason());
        userSession.setLogoutReason(LogoutReason.MANUAL);
        assertEquals(LogoutReason.MANUAL, userSession.getLogoutReason());
    }

    @Test
    void testSetLogoutReason() {
        userSession.setLogoutReason(LogoutReason.EXPIRED);
        assertEquals(LogoutReason.EXPIRED, userSession.getLogoutReason());
    }

    @Test
    void testSetLogoutReasonAllTypes() {
        userSession.setLogoutReason(LogoutReason.MANUAL);
        assertEquals(LogoutReason.MANUAL, userSession.getLogoutReason());

        userSession.setLogoutReason(LogoutReason.EXPIRED);
        assertEquals(LogoutReason.EXPIRED, userSession.getLogoutReason());

        userSession.setLogoutReason(LogoutReason.FORCED);
        assertEquals(LogoutReason.FORCED, userSession.getLogoutReason());

        userSession.setLogoutReason(LogoutReason.KICKED);
        assertEquals(LogoutReason.KICKED, userSession.getLogoutReason());

        userSession.setLogoutReason(LogoutReason.SYSTEM);
        assertEquals(LogoutReason.SYSTEM, userSession.getLogoutReason());

        userSession.setLogoutReason(LogoutReason.UNKNOWN);
        assertEquals(LogoutReason.UNKNOWN, userSession.getLogoutReason());
    }

    @Test
    void testSetLogoutReasonNull() {
        userSession.setLogoutReason(null);
        assertNull(userSession.getLogoutReason());
    }

    @Test
    void testNewSessionIsActive() {
        UserSession newSession = new UserSession("user1", "session1");
        assertTrue(newSession.isActive());
    }

    @Test
    void testSessionDurationIsZeroOnCreation() {
        UserSession newSession = new UserSession("user1", "session1");
        assertEquals(0, newSession.getSessionDuration());
    }

    @Test
    void testLoginTimeAndLastActivityAreSetOnCreation() {
        UserSession newSession = new UserSession("user1", "session1");
        assertNotNull(newSession.getLoginTime());
        assertNotNull(newSession.getLastActivity());
        // loginTime и lastActivity должны быть примерно одинаковыми
        long diff = Math.abs(newSession.getLoginTime().getTime() - newSession.getLastActivity().getTime());
        assertTrue(diff < 1000); // меньше 1 секунды
    }

    @Test
    void testSessionFieldsAreIndependent() {
        UserSession session1 = new UserSession("user1", "session1");
        UserSession session2 = new UserSession("user2", "session2");

        session1.setActive(false);
        session1.setLogoutReason(LogoutReason.MANUAL);

        // session2 не должен измениться
        assertTrue(session2.isActive());
        assertNull(session2.getLogoutReason());
    }

    @Test
    void testIPv4Address() {
        userSession.setIpAddress("192.168.1.1");
        assertEquals("192.168.1.1", userSession.getIpAddress());
    }

    @Test
    void testIPv6Address() {
        userSession.setIpAddress("2001:0db8:85a3:0000:0000:8a2e:0370:7334");
        assertEquals("2001:0db8:85a3:0000:0000:8a2e:0370:7334", userSession.getIpAddress());
    }

    @Test
    void testWebDeviceType() {
        userSession.setDeviceType("web");
        assertEquals("web", userSession.getDeviceType());
    }

    @Test
    void testMobileDeviceType() {
        userSession.setDeviceType("mobile");
        assertEquals("mobile", userSession.getDeviceType());
    }

    @Test
    void testChromeBrowser() {
        userSession.setBrowser("Chrome");
        assertEquals("Chrome", userSession.getBrowser());
    }

    @Test
    void testFirefoxBrowser() {
        userSession.setBrowser("Firefox");
        assertEquals("Firefox", userSession.getBrowser());
    }

    @Test
    void testSafariBrowser() {
        userSession.setBrowser("Safari");
        assertEquals("Safari", userSession.getBrowser());
    }

    @Test
    void testWindowsOS() {
        userSession.setOs("Windows");
        assertEquals("Windows", userSession.getOs());
    }

    @Test
    void testMacOS() {
        userSession.setOs("macOS");
        assertEquals("macOS", userSession.getOs());
    }

    @Test
    void testAndroidOS() {
        userSession.setOs("Android");
        assertEquals("Android", userSession.getOs());
    }

    @Test
    void testiOS() {
        userSession.setOs("iOS");
        assertEquals("iOS", userSession.getOs());
    }
}