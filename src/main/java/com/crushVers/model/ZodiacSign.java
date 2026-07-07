package com.crushVers.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ZodiacSign {
    /**
     * ИД зз
     */
    @DocumentId
    private String id;
    /**
     * Наименование
     */
    @PropertyName("name")
    private String name;

    public ZodiacSign(String name, String description) {
        this.name = name;
    }
}