package com.crushVers.model;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Genre {
    /**
     * ИД
     */
    @DocumentId
    private String id;
    /**
     * Название тега
     */
    @PropertyName("name")
    private String name;
    /**
     * Описание (потом можно будет заполнить как то)
     */
    @PropertyName("description")
    private String description;

    public Genre(String name, String description){
        this.name = name;
        this.description = description;
    }
}
