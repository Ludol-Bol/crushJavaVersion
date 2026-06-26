package com.crushVers.service;

import com.crushVers.enums.LogoutReason;
import com.crushVers.model.User;
import com.crushVers.model.UserSession;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class UserSessionService {

    private static final Logger log = LoggerFactory.getLogger(UserSessionService.class);
    private static final String SESSIONS_COLLECTION = "user_sessions";

    private final FirestoreService firestoreService;

    public UserSessionService(FirestoreService firestoreService) {
        this.firestoreService = firestoreService;
    }

    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    /**
     * Сохранить сессию пользователя
     */
    public void saveSession(User user, String sessionId, HttpServletRequest request) {
        try {
            UserSession session = new UserSession(user.getId(), sessionId);
            // Определяем IP
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty()) {
                ip = request.getRemoteAddr();
            }
            session.setIpAddress(ip);
            // Определяем User-Agent и устройство
            String userAgent = request.getHeader("User-Agent");
            session.setUserAgent(userAgent);
            session.setDeviceType(detectDeviceType(userAgent));
            session.setBrowser(detectBrowser(userAgent));
            session.setOs(detectOS(userAgent));
            session.setExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000));
            // Сохраняем в Firestore
            String docId = getFirestore().collection(SESSIONS_COLLECTION).document().getId();
            session.setId(docId);
            getFirestore().collection(SESSIONS_COLLECTION).document(docId).set(session).get();
            log.info("Сессия пользователя: {} сохранена, (устройство входа: {})", user.getEmail(), session.getDeviceType());

        } catch (Exception e) {
            log.error("Ошибка сохранения сессии: {}", e.getMessage(), e);
        }
    }

    /**
     * Обновить активность сессии
     */
    public void updateActivity(String sessionId) {
        try {
            QuerySnapshot query = getFirestore()
                    .collection(SESSIONS_COLLECTION)
                    .whereEqualTo("sessionId", sessionId)
                    .limit(1)
                    .get()
                    .get();

            if (!query.isEmpty()) {
                String docId = query.getDocuments().get(0).getId();
                UserSession session = query.getDocuments().get(0).toObject(UserSession.class);

                if (session.getLastActivity() != null) {
                    long duration = (System.currentTimeMillis() - session.getLastActivity().getTime()) / 1000;
                    session.setSessionDuration((int) (session.getSessionDuration() + duration));
                }

                getFirestore().collection(SESSIONS_COLLECTION)
                        .document(docId)
                        .update("lastActivity", new Date())
                        .get();

                getFirestore().collection(SESSIONS_COLLECTION)
                        .document(docId)
                        .update("sessionDuration", session.getSessionDuration())
                        .get();

            }
        } catch (Exception e) {
            log.error("Ошибка обновления активности: {}", e.getMessage(), e);
        }
    }

    /**
     * Завершить сессию
     */
    public void endSession(String sessionId, LogoutReason reason) {
        try {
            QuerySnapshot query = getFirestore()
                    .collection(SESSIONS_COLLECTION)
                    .whereEqualTo("sessionId", sessionId)
                    .limit(1)
                    .get()
                    .get();

            if (!query.isEmpty()) {
                String docId = query.getDocuments().get(0).getId();
                getFirestore().collection(SESSIONS_COLLECTION)
                        .document(docId)
                        .update("active", false)
                        .get();

                getFirestore().collection(SESSIONS_COLLECTION)
                        .document(docId)
                        .update("logoutReason", reason.getCode())
                        .get();

                log.info("Сессия завершена: {} (причина: {})", sessionId, reason.getDescription());
            }
        } catch (Exception e) {
            log.error("Ошибка завершения сессии: {}", e.getMessage(), e);
        }
    }



    /**
     * Все активные сессии с данными пользовательей...тут
     */
    public List<Map<String, Object>> getActiveSessionsWithUsers() throws ExecutionException, InterruptedException {
        QuerySnapshot query = getFirestore()
                .collection(SESSIONS_COLLECTION)
                .whereEqualTo("active", true)
                .get()
                .get();

        List<Map<String, Object>> result = new ArrayList<>();
        for (var doc : query) {
            UserSession session = doc.toObject(UserSession.class);
            session.setId(doc.getId());

            User user = firestoreService.findById(session.getUserId());

            Map<String, Object> data = new HashMap<>();
            data.put("session", session);
            data.put("user", user);

            result.add(data);
        }
        return result;
    }

    /**
     * Онлайн пользователи (активны в последние 5 минут)
     */
    public List<Map<String, Object>> getOnlineUsersWithData() throws ExecutionException, InterruptedException {
        Date fiveMinutesAgo = new Date(System.currentTimeMillis() - 5 * 60 * 1000);

        QuerySnapshot query = getFirestore()
                .collection(SESSIONS_COLLECTION)
                .whereEqualTo("active", true)
                .whereGreaterThan("lastActivity", fiveMinutesAgo)
                .get()
                .get();

        List<Map<String, Object>> result = new ArrayList<>();
        for (var doc : query) {
            UserSession session = doc.toObject(UserSession.class);
            session.setId(doc.getId());

            User user = firestoreService.findById(session.getUserId());

            Map<String, Object> data = new HashMap<>();
            data.put("session", session);
            data.put("user", user);

            result.add(data);
        }
        return result;
    }

    /**
     * Количество активных сессий
     */
    public long getActiveSessionsCount() throws ExecutionException, InterruptedException {
        QuerySnapshot query = getFirestore()
                .collection(SESSIONS_COLLECTION)
                .whereEqualTo("active", true)
                .get()
                .get();
        return query.size();
    }


    /**
     * опредление устройств, пока web и mobile
     */
    private String detectDeviceType(String userAgent) {
        if (userAgent == null) return "web";
        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("mobile") || userAgent.contains("android") || userAgent.contains("iphone") || userAgent.contains("ipod") || userAgent.contains("blackberry") || userAgent.contains("windows phone")) {
            return "mobile";
        }
        return "web";
    }

    /**
     * опредление браузера
     */
    private String detectBrowser(String userAgent) {
        if (userAgent == null) return "unknown";
        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("chrome") && !userAgent.contains("edg")) return "Chrome";
        if (userAgent.contains("firefox")) return "Firefox";
        if (userAgent.contains("safari") && !userAgent.contains("chrome")) return "Safari";
        if (userAgent.contains("edg")) return "Edge";
        if (userAgent.contains("opera")) return "Opera";
        return "unknown";
    }

    /**
     * опредление ОС
     */
    private String detectOS(String userAgent) {
        if (userAgent == null) return "unknown";
        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("windows")) return "Windows";
        if (userAgent.contains("mac os") || userAgent.contains("macintosh")) return "macOS";
        if (userAgent.contains("linux")) return "Linux";
        if (userAgent.contains("android")) return "Android";
        if (userAgent.contains("iphone") || userAgent.contains("ios")) return "iOS";
        return "unknown";
    }

    /**
     * Очистить истекшие сессии (запускать по расписанию, пока хз где)
     */
    public void cleanExpiredSessions() {
        try {
            Date now = new Date();
            QuerySnapshot query = getFirestore()
                    .collection(SESSIONS_COLLECTION)
                    .whereLessThan("expiresAt", now)
                    .get()
                    .get();

            int count = 0;
            for (var doc : query) {
                doc.getReference().delete().get();
                count++;
            }

            if (count > 0) {
                log.info("Очищено {} старых сессий", count);
            }
        } catch (Exception e) {
            log.error("Ошибка очистки сессий: {}", e.getMessage(), e);
        }
    }
}