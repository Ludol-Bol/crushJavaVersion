package com.crushVers.model;

import com.crushVers.enums.TagGroup;
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

    @PropertyName("group")
    private TagGroup tagGroup;

    @PropertyName("description")
    private String description;

    public Tag(String name, TagGroup tagGroup, String description) {
        this.name = name;
        this.tagGroup = tagGroup;
        this.description = description;
    }
}
