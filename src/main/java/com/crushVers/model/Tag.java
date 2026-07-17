package com.crushVers.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tag {
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

    public Tag(String name){
        this.name = name;
    }
}
