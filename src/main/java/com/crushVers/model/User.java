package com.crushVers.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import com.google.cloud.firestore.annotation.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class User {

    @DocumentId
    private String id;

    @PropertyName("email")
    private String email;

    @PropertyName("nickname")
    private String nickname;

    @PropertyName("password_hash")
    private String passwordHash;

    @PropertyName("created_at")
    @ServerTimestamp
    private Date createdAt;

    @PropertyName("icon")
    private String icon;

    @PropertyName("roles_id")
    private List<String> roleIds;

    // Пустой конструктор (обязателен для Firebase)
    public User() {
    }

    // Конструктор для создания нового пользователя
    public User(String email, String nickname, String passwordHash, Date birthDate) {
        this.email = email;
        this.nickname = nickname;
        this.passwordHash = passwordHash;
        this.icon = null;
    }


    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    @PropertyName("password_hash")
    public String getPasswordHash() {
        return passwordHash;
    }


    @PropertyName("created_at")
    public Date getCreatedAt() {
        return createdAt;
    }

    public String getIcon() {
        return icon;
    }


    public void setId(String id) {
        this.id = id;
    }

    @PropertyName("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @PropertyName("nickname")
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @PropertyName("password_hash")
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @PropertyName("created_at")
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @PropertyName("icon")
    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<String> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<String> roleIds) {
        this.roleIds = roleIds;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", createdAt=" + createdAt +
                ", icon='" + icon + '\'' +
                ", roledId='" + String.join(", ", roleIds) + '\'' +
                '}';
    }
}