package com.crushVers.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("test-id-123");
        user.setEmail("test@example.com");
        user.setNickname("testuser");
        user.setPasswordHash("hashed_password_123");
        LocalDate localDate = LocalDate.of(1990, 1, 1);
        user.setCreatedAt(new Date());
        String base64Image = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==";
        user.setIcon(base64Image);
        assertEquals(base64Image, user.getIcon());
        user.setRoleIds(Arrays.asList("role1", "role2"));
    }

    @Test
    void testDefaultConstructor() {
        User emptyUser = new User();
        assertNotNull(emptyUser);
    }

    @Test
    void testParameterizedConstructor() {
        LocalDate localDate = LocalDate.of(1990, 1, 1);
        Date birthDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        User newUser = new User("new@example.com", "newuser", "hashed_pass", birthDate);
        assertEquals("new@example.com", newUser.getEmail());
        assertEquals("newuser", newUser.getNickname());
        assertEquals("hashed_pass", newUser.getPasswordHash());
        assertNull(newUser.getIcon());
    }

    @Test
    void testGetId() {
        assertEquals("test-id-123", user.getId());
    }

    @Test
    void testSetId() {
        user.setId("new-id-456");
        assertEquals("new-id-456", user.getId());
    }

    @Test
    void testGetEmail() {
        assertEquals("test@example.com", user.getEmail());
    }

    @Test
    void testSetEmail() {
        user.setEmail("changed@example.com");
        assertEquals("changed@example.com", user.getEmail());
    }

    @Test
    void testGetNickname() {
        assertEquals("testuser", user.getNickname());
    }

    @Test
    void testSetNickname() {
        user.setNickname("newuser");
        assertEquals("newuser", user.getNickname());
    }

    @Test
    void testGetPasswordHash() {
        assertEquals("hashed_password_123", user.getPasswordHash());
    }

    @Test
    void testSetPasswordHash() {
        user.setPasswordHash("new_hash_456");
        assertEquals("new_hash_456", user.getPasswordHash());
    }

    @Test
    void testGetCreatedAt() {
        assertNotNull(user.getCreatedAt());
    }

    @Test
    void testSetCreatedAt() {
        Date newDate = new Date();
        user.setCreatedAt(newDate);
        assertEquals(newDate, user.getCreatedAt());
    }

    @Test
    void testGetIcon() {
        String base64Image = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==";
        user.setIcon(base64Image);
        assertEquals(base64Image, user.getIcon());
    }

    @Test
    void testSetIcon() {
        String base64Image = "data:image/png;base64,iVBORw0KGgo...";
        user.setIcon(base64Image);
        assertTrue(user.getIcon().startsWith("data:image/"));
    }

    @Test
    void testSetIconNull() {
        user.setIcon(null);
        assertNull(user.getIcon());
    }



    @Test
    void testGetRoleIds() {
        List<String> roleIds = user.getRoleIds();
        assertNotNull(roleIds);
        assertEquals(2, roleIds.size());
        assertEquals("role1", roleIds.get(0));
        assertEquals("role2", roleIds.get(1));
    }

    @Test
    void testSetRoleIds() {
        List<String> newRoles = Arrays.asList("ADMIN", "USER");
        user.setRoleIds(newRoles);
        assertEquals(2, user.getRoleIds().size());
        assertEquals("ADMIN", user.getRoleIds().get(0));
    }

    @Test
    void testSetRoleIdsEmpty() {
        user.setRoleIds(List.of());
        assertTrue(user.getRoleIds().isEmpty());
    }

    @Test
    void testSetRoleIdsNull() {
        user.setRoleIds(null);
        assertNull(user.getRoleIds());
    }

    @Test
    void testToString() {
        String toString = user.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("test@example.com"));
        assertTrue(toString.contains("testuser"));
        assertTrue(toString.contains("test-id-123"));
    }

    @Test
    void testEmptyEmail() {
        user.setEmail("");
        assertEquals("", user.getEmail());
    }

    @Test
    void testNullEmail() {
        user.setEmail(null);
        assertNull(user.getEmail());
    }

    @Test
    void testVeryLongNickname() {
        String longNickname = "a".repeat(100);
        user.setNickname(longNickname);
        assertEquals(100, user.getNickname().length());
    }

    @Test
    void testUserWithMultipleRoles() {
        List<String> multipleRoles = Arrays.asList("role1", "role2", "role3", "role4");
        user.setRoleIds(multipleRoles);
        assertEquals(4, user.getRoleIds().size());
    }

    @Test
    void testUserWithNoRoles() {
        user.setRoleIds(null);
        assertNull(user.getRoleIds());
    }
}