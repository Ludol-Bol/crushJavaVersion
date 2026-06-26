package com.crushVers.model;
import com.crushVers.enums.LogoutReason;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import lombok.Getter;


import java.util.Date;

public class UserSession {


    @DocumentId
    @Getter
    private String id;

    /**
     * Название роли ID пользователя
     */
    @Getter
    @PropertyName("userId")
    private String userId;

    /**
     * ID сессии
     */
    @Getter
    @PropertyName("sessionId")
    private String sessionId;

    /**
     * ip Address
     */
    @Getter
    @PropertyName("ipAddress")
    private String ipAddress;

    /**
     *  Информация об устройстве пользователя.
     */
    @Getter
    @PropertyName("user_agent")
    private String userAgent;

    /**
     *  тип устройства(mobile-в будущем, web)
     */
    @Getter
    @PropertyName("device_type")
    private String deviceType;

    /**
     *  браузер(Chrome, Firefox, Safari и т.п)
     */
    @Getter
    @PropertyName("browser")
    private String browser;

    /**
     *  OC(Windows, macOS, Android, iOS)
     */
    @Getter
    @PropertyName("os")
    private String os;

    /**
     *  Время когда пользователь зашел в систему
     */
    @Getter
    @PropertyName("login_time")
    private Date loginTime;

    /**
     *  Время последней активности
     */
    @Getter
    @PropertyName("last_activity")
    private Date lastActivity;

    /**
     *  Время итечение сессии(пока 30 минут после бездействия?)
     */
    @Getter
    @PropertyName("expires_at")
    private Date expiresAt;

    /**
     *  Время активности в секундах(мб позже понадобиться для какой-нибудь статитики)
     */
    @Getter
    @PropertyName("session_duration")
    private int sessionDuration;

    /**
     *  Активна ли сессия(true,false)
     */
    @Getter
    @PropertyName("session_duration")
    private boolean active;

    /**
     *  Активна ли сессия
     */
    @Getter
    @PropertyName("logout_reason")
    private LogoutReason logoutReason;


    public UserSession() {}

    public UserSession(String userId, String sessionId) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.loginTime = new Date();
        this.lastActivity = new Date();
        this.active = true;
        this.sessionDuration = 0;
    }


    public void setId(String id) { this.id = id; }

    public void setUserId(String userId) { this.userId = userId; }

    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

    public void setBrowser(String browser) { this.browser = browser; }

    public void setOs(String os) { this.os = os; }

    public void setLoginTime(Date loginTime) { this.loginTime = loginTime; }

    public void setLastActivity(Date lastActivity) { this.lastActivity = lastActivity; }

    public void setExpiresAt(Date expiresAt) { this.expiresAt = expiresAt; }

    public void setSessionDuration(int sessionDuration) { this.sessionDuration = sessionDuration; }

    public void setActive(boolean active) { this.active = active; }

    public void setLogoutReason(LogoutReason logoutReason) { this.logoutReason = logoutReason; }
}