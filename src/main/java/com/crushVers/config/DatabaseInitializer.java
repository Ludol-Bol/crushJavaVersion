package com.crushVers.config;

import com.crushVers.model.User;
import com.crushVers.model.UserRole;
import com.crushVers.service.FirestoreService;
import com.crushVers.service.UserRoleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Инициализация БД
 */

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final UserRoleService userRoleService;
    private final FirestoreService firestoreService;

    public DatabaseInitializer(UserRoleService userRoleService, FirestoreService firestoreService) {
        this.userRoleService = userRoleService;
        this.firestoreService = firestoreService;
    }

    @Override
    public void run(String... args) throws ExecutionException, InterruptedException {
        // создание базовых ролей
        initUserRoles();
        //назначем роль User всем к кого ее нет
        assignDefaultRoleToAllUsers();
    }

    /**
     * Инициализация пользовательских ролей
     */
    private void initUserRoles() throws ExecutionException, InterruptedException {
        //дефольные роли
        String[][] defaultRoles = {
                {"USER", "Обычный пользователь с базовыми правами"},
                {"ADMIN", "Полный доступ ко всем функциям"},
        };

        for (String[] roleData : defaultRoles) {
            String roleName = roleData[0];
            String roleDescription = roleData[1];
            // Проверяем, существует ли уже такая роль
            UserRole existingRole = userRoleService.findByName(roleName);
            if (existingRole == null) {
                // Роль не существует — создаём
                UserRole newRole = new UserRole();
                newRole.setName(roleName);
                newRole.setDescription(roleDescription);
                userRoleService.save(newRole);
            }
        }
    }


    /**
     * Назначить роль USER всем пользователям, у которых её нет
     */
    private void assignDefaultRoleToAllUsers() throws ExecutionException, InterruptedException {
        // Получаем роль USER
        UserRole userRole = userRoleService.findByName("USER");
        if (userRole == null) {
            return;
        }
        // Получаем ID роли
        String userRoleId = userRoleService.getIdByName("USER");
        if (userRoleId == null) {
            return;
        }
        List<User> allUsers = firestoreService.findAllUsers();
        if (allUsers.isEmpty()) {
            return;
        }

        for (User user : allUsers) {
            List<String> roleIds = user.getRoleIds();
            // Если у пользователя нет ролей или нетUSER
            if (roleIds == null || !roleIds.contains(userRoleId)) {
                // Добавляем роль USER
                if (roleIds == null) {
                    roleIds = new ArrayList<>();
                }
                roleIds.add(userRoleId);
                user.setRoleIds(roleIds);
                // Сохраняем пользователя
                firestoreService.saveUser(user);
            }
        }
    }
}