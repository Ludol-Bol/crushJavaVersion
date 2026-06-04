package com.crushVers.service;

import com.crushVers.model.UserRole;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class UserRoleService {
    private static final String USER_ROLES_COLLECTION = "user_roles";

    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    /**
     * Найти роль по названию
     */
    public UserRole findByName(String name) throws ExecutionException, InterruptedException {
        QuerySnapshot query = getFirestore()
                .collection(USER_ROLES_COLLECTION)
                .whereEqualTo("name", name)
                .limit(1)
                .get()
                .get();

        if (query.isEmpty()) {
            return null;
        }
        QueryDocumentSnapshot document = query.getDocuments().get(0);
        return document.toObject(UserRole.class);
    }

    /**
     * Найти роль по ID
     */
    public UserRole findById(String id) throws ExecutionException, InterruptedException {
        var document = getFirestore()
                .collection(USER_ROLES_COLLECTION)
                .document(id)
                .get()
                .get();

        if (!document.exists()) {
            return null;
        }

        return document.toObject(UserRole.class);
    }

    /**
     * Получить все роли
     */
    public List<UserRole> findAll() throws ExecutionException, InterruptedException {
        QuerySnapshot query = getFirestore()
                .collection(USER_ROLES_COLLECTION)
                .get()
                .get();

        List<UserRole> roles = new ArrayList<>();
        for (QueryDocumentSnapshot document : query) {
            roles.add(document.toObject(UserRole.class));
        }
        return roles;
    }

    /**
     * Сохранить роль, создать или обновить
     */
    public UserRole save(UserRole role) throws ExecutionException, InterruptedException {
        // Если ID не задан, Firestore сам создаст его
        if (role.getName() == null) {
            throw new IllegalArgumentException("Role name cannot be null");
        }
        // Проверяем, существует ли уже такая роль
        UserRole existing = findByName(role.getName());
        if (existing != null) {
            // Обновляем существующую
            String docId = getIdByName(role.getName());
            if (docId != null) {
                getFirestore()
                        .collection(USER_ROLES_COLLECTION)
                        .document(docId)
                        .set(role)
                        .get();
            }
            return role;
        } else {
            // Создаём новую
            role.setCreatedAt(new Date());
            var docRef = getFirestore()
                    .collection(USER_ROLES_COLLECTION)
                    .document();
            docRef.set(role).get();
            return role;
        }
    }

    /**
     * Получить ID по названию роли
     */
    public String getIdByName(String name) throws ExecutionException, InterruptedException {
        QuerySnapshot query = getFirestore()
                .collection(USER_ROLES_COLLECTION)
                .whereEqualTo("name", name)
                .limit(1)
                .get()
                .get();

        if (query.isEmpty()) {
            return null;
        }
        return query.getDocuments().get(0).getId();
    }

    /**
     * Удалить роль по названию
     */
    public void deleteByName(String name) throws ExecutionException, InterruptedException {
        String docId = getIdByName(name);
        if (docId != null) {
            getFirestore()
                    .collection(USER_ROLES_COLLECTION)
                    .document(docId)
                    .delete()
                    .get();
        }
    }

    /**
     * Получить количество ролей, вдруг пригодиться
     */
    public long count() throws ExecutionException, InterruptedException {
        QuerySnapshot query = getFirestore()
                .collection(USER_ROLES_COLLECTION)
                .get()
                .get();
        return query.size();
    }
}
