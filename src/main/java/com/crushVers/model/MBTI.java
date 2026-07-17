package com.crushVers.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MBTI {
    /**
     * ИД
     */
    @DocumentId
    private String id;
    /**
     * Полное наименование
     */
    @PropertyName("full_name")
    private String fullName;
    /**
     * Короткое наименование словом
     */
    @PropertyName("short_name")
    private String shortName;
    /**
     * Описание
     */
    @PropertyName("description")
    private String description;

    public MBTI( String fullName, String shortName, String description) {
        this.fullName = fullName;
        this.shortName = shortName;
        this.description = description;
    }
}
