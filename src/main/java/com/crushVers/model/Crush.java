package com.crushVers.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Crush {

    @DocumentId
    private String id;

    @PropertyName("name")
    private String name;

    @PropertyName("description")
    private String description;

    @PropertyName("universe_id")
    private String universeId;

    @PropertyName("color_hair_id")
    private String colorHairId;

    @PropertyName("color_eyes_id")
    private String colorEyesId;

    @PropertyName("height")
    private int height;

    @PropertyName("date_of_birth")
    private int dateOfBirth;

    @PropertyName("zodiac_sign_id")
    private String zodiacSignId;

    @PropertyName("socionics_id")
    private String socionicsId;

    @PropertyName("mbti_id")
    private String mbtiId;

    @PropertyName("tegs_id")
    private List<String> tegsIds;


    public Crush(String name, String shortName, String description) {
        this.name = name;
        this.description = description;
    }

    public Crush(String name, String shortName, String description,
                       String zodiacSignId, String socionicsId) {
        this.name = name;
        this.description = description;
        this.zodiacSignId = zodiacSignId;
        this.socionicsId = socionicsId;
    }

    public Crush(String name, String shortName, String description,
                       String zodiacSignId, String socionicsId, Universe universe) {
        this.name = name;
        this.description = description;
        this.zodiacSignId = zodiacSignId;
        this.socionicsId = socionicsId;
    }
}