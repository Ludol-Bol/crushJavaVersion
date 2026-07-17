package com.crushVers.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Universe {
    /**
     * id
     */
    @DocumentId
    private String id;
    /**
     * название
     */
    @PropertyName("name")
    private String name;
    /**
     * Описание
     */
    @PropertyName("description")
    private String description;

    public Universe(String name){
        this.name = name;
    }

    public Universe(String name, String description){
        this.name = name;
        this.description = description;
    }
}
