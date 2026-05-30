package com.mohistmc.academy.skill;

/**
 * @author Mgazul
 * @date 2026/5/30 20:26
 */
public enum AbilityCategory {
    ELECTROMASTER("electromaster"),
    MELTDOWNER("meltdowner"),
    TELEPORTER("teleporter"),
    VECMANIP("vecmanip");

    private final String id;

    AbilityCategory(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static AbilityCategory fromId(String id) {
        for (AbilityCategory cat : values()) {
            if (cat.id.equals(id)) return cat;
        }
        return null;
    }
}
