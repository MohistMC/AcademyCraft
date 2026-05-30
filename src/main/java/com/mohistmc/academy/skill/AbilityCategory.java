package com.mohistmc.academy.skill;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public record AbilityCategory(String id) {

    private static final Map<String, AbilityCategory> REGISTRY = new LinkedHashMap<>();

    public static final AbilityCategory ELECTROMASTER = register(new AbilityCategory("electromaster"));
    public static final AbilityCategory MELTDOWNER = register(new AbilityCategory("meltdowner"));
    public static final AbilityCategory TELEPORTER = register(new AbilityCategory("teleporter"));
    public static final AbilityCategory VECMANIP = register(new AbilityCategory("vecmanip"));

    public static AbilityCategory register(AbilityCategory category) {
        REGISTRY.put(category.id, category);
        return category;
    }

    public String getTranslationKey() {
        return "item.academy.factor_" + id;
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
        if (!(o instanceof AbilityCategory(String id1))) return false;
        return id.equals(id1);
    }

    @Override
    public @NotNull String toString() {
        return "AbilityCategory{" + id + "}";
    }
}
