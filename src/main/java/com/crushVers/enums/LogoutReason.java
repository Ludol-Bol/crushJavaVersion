package com.crushVers.enums;

import lombok.Getter;

@Getter
public enum LogoutReason {

    MANUAL("manual", "Пользователь вышел самостоятельно"),
    EXPIRED("expired", "Сессия истекла по времени"),
    FORCED("forced", "Админ принудительно завершил сессию"),
    KICKED("kicked", "Пользователь был выгнан (другая сессия)"),
    SYSTEM("system", "Системное завершение"),
    UNKNOWN("unknown", "Неизвестная причина");

    private final String code;
    private final String description;

    LogoutReason(String code, String description) {
        this.code = code;
        this.description = description;
    }


    /**
     * Получить enum по коду
     */
    public static LogoutReason fromCode(String code) {
        for (LogoutReason reason : values()) {
            if (reason.code.equals(code)) {
                return reason;
            }
        }
        return UNKNOWN;
    }

    /**
     * Получить enum по названию
     */
    public static LogoutReason fromName(String name) {
        try {
            return LogoutReason.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
