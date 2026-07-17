package com.crushVers.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class  Colors{
    /**
     * ИД
     */
    @DocumentId
    private String id;
    /**
     * Наименование цвета
     */
    @PropertyName("name")
    private String name;
    /**
     * Код цваета
     */
    @PropertyName("code")
    private String code;

    public Colors(String name, String code){
        this.name = name;
        this.code = code;
    }
}
