package com.crushVers.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class UserRoleTest {

    private UserRole userRole;
    private Date testDate;

    @BeforeEach
    void setUp() {
        testDate = new Date();
        userRole = new UserRole();
        userRole.setId("role-id-123");
        userRole.setName("ROLE_USER");
        userRole.setDescription("Обычный пользователь");
        userRole.setCreatedAt(testDate);
    }

    @Test
    void testDefaultConstructor() {
        UserRole emptyRole = new UserRole();
        assertNotNull(emptyRole);
        assertNull(emptyRole.getId());
        assertNull(emptyRole.getName());
        assertNull(emptyRole.getDescription());
        assertNull(emptyRole.getCreatedAt());
    }

    @Test
    void testParameterizedConstructor() {
        Date customDate = new Date();
        UserRole newRole = new UserRole("ROLE_ADMIN", "Администратор", customDate);
        assertEquals("ROLE_ADMIN", newRole.getName());
        assertEquals("Администратор", newRole.getDescription());
        assertEquals(customDate, newRole.getCreatedAt());
        assertNull(newRole.getId());
    }

    @Test
    void testGetId() {
        assertEquals("role-id-123", userRole.getId());
    }

    @Test
    void testSetId() {
        userRole.setId("new-role-id");
        assertEquals("new-role-id", userRole.getId());
    }

    @Test
    void testSetIdNull() {
        userRole.setId(null);
        assertNull(userRole.getId());
    }

    @Test
    void testGetName() {
        assertEquals("ROLE_USER", userRole.getName());
    }

    @Test
    void testSetName() {
        userRole.setName("ROLE_ADMIN");
        assertEquals("ROLE_ADMIN", userRole.getName());
    }

    @Test
    void testSetNameEmpty() {
        userRole.setName("");
        assertEquals("", userRole.getName());
    }

    @Test
    void testSetNameNull() {
        userRole.setName(null);
        assertNull(userRole.getName());
    }

    @Test
    void testGetDescription() {
        assertEquals("Обычный пользователь", userRole.getDescription());
    }

    @Test
    void testSetDescription() {
        userRole.setDescription("Полный доступ");
        assertEquals("Полный доступ", userRole.getDescription());
    }

    @Test
    void testSetDescriptionEmpty() {
        userRole.setDescription("");
        assertEquals("", userRole.getDescription());
    }

    @Test
    void testSetDescriptionNull() {
        userRole.setDescription(null);
        assertNull(userRole.getDescription());
    }

    @Test
    void testGetCreatedAt() {
        assertEquals(testDate, userRole.getCreatedAt());
    }

    @Test
    void testSetCreatedAt() {
        Date newDate = new Date(System.currentTimeMillis() + 100000);
        userRole.setCreatedAt(newDate);
        assertEquals(newDate, userRole.getCreatedAt());
    }

    @Test
    void testSetCreatedAtNull() {
        userRole.setCreatedAt(null);
        assertNull(userRole.getCreatedAt());
    }


    @Test
    void testUserRole() {
        UserRole role = new UserRole("ROLE_USER", "Обычный пользователь", new Date());
        assertEquals("ROLE_USER", role.getName());
        assertEquals("Обычный пользователь", role.getDescription());
    }

    @Test
    void testAdminRole() {
        UserRole role = new UserRole("ROLE_ADMIN", "Полный доступ", new Date());
        assertEquals("ROLE_ADMIN", role.getName());
        assertEquals("Полный доступ", role.getDescription());
    }

    @Test
    void testVeryLongName() {
        String longName = "A".repeat(100);
        userRole.setName(longName);
        assertEquals(100, userRole.getName().length());
    }

    @Test
    void testVeryLongDescription() {
        String longDescription = "B".repeat(500);
        userRole.setDescription(longDescription);
        assertEquals(500, userRole.getDescription().length());
    }

    @Test
    void testFutureCreatedAt() {
        Date futureDate = new Date(System.currentTimeMillis() + 1000000000L);
        userRole.setCreatedAt(futureDate);
        assertTrue(userRole.getCreatedAt().after(new Date()));
    }

    @Test
    void testToString() {
        String toString = userRole.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("role-id-123"));
        assertTrue(toString.contains("ROLE_USER"));
        assertTrue(toString.contains("Обычный пользователь"));
    }


    @Test
    void testTwoDifferentRolesAreNotEqual() {
        UserRole role1 = new UserRole("ROLE_USER", "User", new Date());
        UserRole role2 = new UserRole("ROLE_ADMIN", "Admin", new Date());
        assertNotEquals(role1.getName(), role2.getName());
        assertNotEquals(role1.getDescription(), role2.getDescription());
    }

    @Test
    void testSameRoleProperties() {
        Date sameDate = new Date();
        UserRole role1 = new UserRole("ROLE_USER", "User", sameDate);
        UserRole role2 = new UserRole("ROLE_USER", "User", sameDate);
        assertEquals(role1.getName(), role2.getName());
        assertEquals(role1.getDescription(), role2.getDescription());
        assertEquals(role1.getCreatedAt(), role2.getCreatedAt());
    }
}