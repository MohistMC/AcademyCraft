package com.mohistmc.academy.skill;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Mgazul
 * @date 2026/5/30 20:26
 */
public class AbilityCategory {

    private static final Map<String, AbilityCategory> REGISTRY = new LinkedHashMap<>();

    public static final AbilityCategory ELECTROMASTER = register(new AbilityCategory("electromaster"));
    public static final AbilityCategory MELTDOWNER = register(new AbilityCategory("meltdowner"));
    public static final AbilityCategory TELEPORTER = register(new AbilityCategory("teleporter"));
    public static final AbilityCategory VECMANIP = register(new AbilityCategory("vecmanip"));

    private final String id;

    public AbilityCategory(String id) {
        this.id = id;
    }

    public static AbilityCategory register(AbilityCategory category) {
        REGISTRY.put(category.id, category);
        return category;
    }

    public String getId() {
        return id;
    }

    public static AbilityCategory fromId(String id) {
        return REGISTRY.get(id);
    }

    public static Collection<AbilityCategory> all() {
        return new ArrayList<>(REGISTRY.values());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbilityCategory that)) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "AbilityCategory{" + id + "}";
    }
}
