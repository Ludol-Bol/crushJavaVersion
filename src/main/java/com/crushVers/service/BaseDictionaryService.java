package com.crushVers.service;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class BaseDictionaryService {

    private static final Logger log = LoggerFactory.getLogger(BaseDictionaryService.class);

    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    /**
     * Найти по ID
     */
    public <T> T findById(String collectionName, Class<T> entityClass, String id)
            throws ExecutionException, InterruptedException {
        var document = getFirestore()
                .collection(collectionName)
                .document(id)
                .get()
                .get();

        if (!document.exists()) {
            return null;
        }

        T entity = document.toObject(entityClass);
        setId(entity, document.getId());
        return entity;
    }

    /**
     * Найти по полю (например, по name, short_id и т.д.)
     */
    public <T> T findByField(String collectionName, Class<T> entityClass,
                             String fieldName, String value)
            throws ExecutionException, InterruptedException {
        var query = getFirestore()
                .collection(collectionName)
                .whereEqualTo(fieldName, value)
                .limit(1)
                .get()
                .get();

        if (query.isEmpty()) {
            return null;
        }

        T entity = query.getDocuments().get(0).toObject(entityClass);
        setId(entity, query.getDocuments().get(0).getId());
        return entity;
    }

    /**
     * Получить все записи
     */
    public <T> List<T> findAll(String collectionName, Class<T> entityClass)
            throws ExecutionException, InterruptedException {
        var query = getFirestore()
                .collection(collectionName)
                .get()
                .get();

        List<T> result = new ArrayList<>();
        for (var document : query) {
            T entity = document.toObject(entityClass);
            setId(entity, document.getId());
            result.add(entity);
        }
        return result;
    }

    /**
     * Сохранить запись
     */
    public <T> T save(String collectionName, T entity)
            throws ExecutionException, InterruptedException {
        String documentId = getId(entity);
        if (documentId == null || documentId.isEmpty()) {
            documentId = getFirestore().collection(collectionName).document().getId();
            setId(entity, documentId);
        }

        getFirestore()
                .collection(collectionName)
                .document(documentId)
                .set(entity)
                .get();

        log.info("Сохранено в {}: {}", collectionName, entity);
        return entity;
    }

    /**
     * Удалить по ID
     */
    public void deleteById(String collectionName, String id)
            throws ExecutionException, InterruptedException {
        getFirestore()
                .collection(collectionName)
                .document(id)
                .delete()
                .get();
        log.info("Удалено из {}: {}", collectionName, id);
    }

    /**
     * Получить количество записей
     */
    public long count(String collectionName)
            throws ExecutionException, InterruptedException {
        var query = getFirestore()
                .collection(collectionName)
                .get()
                .get();
        return query.size();
    }

    // ===== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ =====

    private <T> String getId(T entity) {
        try {
            Method method = entity.getClass().getMethod("getId");
            return (String) method.invoke(entity);
        } catch (Exception e) {
            return null;
        }
    }

    private <T> void setId(T entity, String id) {
        try {
            Method method = entity.getClass().getMethod("setId", String.class);
            method.invoke(entity, id);
        } catch (Exception e) {
            log.warn("Не удалось установить ID для {}", entity.getClass().getSimpleName());
        }
    }
}