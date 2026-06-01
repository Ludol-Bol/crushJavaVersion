package com.crushVers.service;

import com.crushVers.model.User;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

@Service
public class FirestoreService {

    private static final String USERS_COLLECTION = "users";

    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    // Найти пользователя по email
    public User findByEmail(String email) throws ExecutionException, InterruptedException {
        QuerySnapshot query = getFirestore()
                .collection(USERS_COLLECTION)
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .get();

        if (query.isEmpty()) {
            return null;
        }

        QueryDocumentSnapshot document = query.getDocuments().get(0);
        User user = document.toObject(User.class);
        user.setId(document.getId());
        return user;
    }

    // Найти пользователя по имени пользователя
    public User findByNickname(String nickname) throws ExecutionException, InterruptedException {
        QuerySnapshot query = getFirestore()
                .collection(USERS_COLLECTION)
                .whereEqualTo("nickname", nickname)
                .limit(1)
                .get()
                .get();

        if (query.isEmpty()) {
            return null;
        }

        QueryDocumentSnapshot document = query.getDocuments().get(0);
        User user = document.toObject(User.class);
        user.setId(document.getId());
        return user;
    }


    // Найти пользователя по email или nickname
    public User findByEmailOrNickname(String login) throws ExecutionException, InterruptedException {
        User user = findByEmail(login);
        if (user == null) {
            user = findByNickname(login);
        }
        return user;
    }

    // Найти пользователя по ID
    public User findById(String id) throws ExecutionException, InterruptedException {
        var document = getFirestore()
                .collection(USERS_COLLECTION)
                .document(id)
                .get()
                .get();

        if (!document.exists()) {
            return null;
        }

        User user = document.toObject(User.class);
        user.setId(document.getId());
        return user;
    }

    // Сохранить пользователя
    public User saveUser(User user) throws ExecutionException, InterruptedException {
        String documentId = user.getId();

        if (documentId == null || documentId.isEmpty()) {
            documentId = getFirestore().collection(USERS_COLLECTION).document().getId();
            user.setId(documentId);
        }

        getFirestore()
                .collection(USERS_COLLECTION)
                .document(documentId)
                .set(user)
                .get();

        return user;
    }

    // Хэширование пароля (SHA-256)
    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Ошибка хэширования пароля", e);
        }
    }

    // Сравнивает введенный пароль с хэшем из БД
    public boolean checkPassword(String rawPassword, String hashedPasswordFromDB) {
        String hashedInputPassword = hashPassword(rawPassword);
        return hashedInputPassword.equals(hashedPasswordFromDB);
    }
}