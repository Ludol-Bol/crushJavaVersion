package com.crushVers.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import jakarta.annotation.PostConstruct;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initializeFirebase() throws IOException {

        // Проверяем инициализацию Firebase
        if (FirebaseApp.getApps().isEmpty()) {
            // Загружаем файл с ключом из папки resources
            ClassPathResource serviceAccount = new ClassPathResource("serviceAccountKey.json");
            // Настраиваем параметры подключения
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount.getInputStream()))
                    .build();
            // Инициализируем Firebase
            FirebaseApp.initializeApp(options);
            //временно чисто пока для себя, потом вынести в логи нормальные
            System.out.println("✅ Firebase успешно подключен!");
        } else {
            System.out.println("ℹ️ Firebase уже был подключен");
        }
    }
}