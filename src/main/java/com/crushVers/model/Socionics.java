package com.crushVers.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Socionics {
    /**
     * ИД соционики
     */
    @DocumentId
    private String id;
    /**
     * Аббревиатура?, пока хз как назвать
     */
    @PropertyName("short_id")
    private String shortId;
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

    public Socionics( String shortId,String fullName, String shortName, String description) {
        this.shortId = shortId;
        this.fullName = fullName;
        this.shortName = shortName;
        this.description = description;
    }
}