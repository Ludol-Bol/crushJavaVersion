package com.crushVers.enums;

import lombok.Getter;

@Getter
public enum TagGroup {

    APPEARANCE("appearance", "Внешность"),
    POSITION_IN_THE_PLOT("positionInThePlot", "Позиция в сюжете"),
    RACE("race", "Раса"),
    ACTIVITY("activity", "Деятельность"),
    WEAPON("weapon", "Оружие"),
    OTHER("other", "Другое");

    private final String code;
    private final String description;

    TagGroup(String code, String description) {
        this.code = code;
        this.description = description;
    }


    /**
     * Получить enum по коду
     */
    public static TagGroup fromCode(String code) {
        for (TagGroup tagGroup : values()) {
            if (tagGroup.code.equals(code)) {
                return tagGroup;
            }
        }
        return OTHER;
    }

}
