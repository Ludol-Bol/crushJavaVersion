package com.crushVers.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;

import java.util.Date;

/**
 * Модель для автовхода
 */
public class UserToken {
    @DocumentId
    private String id;
    /**
     * Пользователь
     */
    @PropertyName("userId")
    private String userId;
    /**
     * Токен входа
     */
    @PropertyName("token")
    private String token;
    /**
     * Дата создания
     */
    @PropertyName("createdAt")
    private Date createdAt;

    /**
     * Время существования
     */
    @PropertyName("expiresAt")
    private Date expiresAt;

    public UserToken() {}

    public UserToken(String userId, String token, int daysValid) {
        this.userId = userId;
        this.token = token;
        this.createdAt = new Date();
        this.expiresAt = new Date(System.currentTimeMillis() + daysValid * 24 * 60 * 60 * 1000L);
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }

    public void setUserId(String userId) { this.userId = userId; }

    public String getToken() { return token; }

    public void setToken(String token) { this.token = token; }

    public Date getCreatedAt() { return createdAt; }

    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getExpiresAt() { return expiresAt; }

    public void setExpiresAt(Date expiresAt) { this.expiresAt = expiresAt; }
}