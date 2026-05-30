package com.mohistmc.academy.skill;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mgazul
 * @date 2026/5/30 20:26
 */
public class Skill {
    private final String id;
    private final AbilityCategory category;
    private final int level;
    private final SkillType type;
    private final List<Prerequisite> prerequisites;
    private final float baseCpCost;
    private final float baseOverload;
    private SkillEffect effect;

    public Skill(String id, AbilityCategory category, int level, SkillType type,
                 List<Prerequisite> prerequisites, float baseCpCost, float baseOverload) {
        this.id = id;
        this.category = category;
        this.level = level;
        this.type = type;
        this.prerequisites = prerequisites;
        this.baseCpCost = baseCpCost;
        this.baseOverload = baseOverload;
    }

    public String getId() {
        return id;
    }

    public AbilityCategory getCategory() {
        return category;
    }

    public int getLevel() {
        return level;
    }

    public SkillType getType() {
        return type;
    }

    public List<Prerequisite> getPrerequisites() {
        return prerequisites;
    }

    public float getBaseCpCost() {
        return baseCpCost;
    }

    public float getBaseOverload() {
        return baseOverload;
    }

    public SkillEffect getEffect() {
        return effect;
    }

    public void setEffect(SkillEffect effect) {
        this.effect = effect;
    }

    public boolean hasEffect() {
        return effect != null;
    }

    public String getTranslationKey() {
        return "item.academy.factor_" + category.id() + "." + id;
    }

    public String getDescKey() {
        return getTranslationKey() + ".desc";
    }

    public record Prerequisite(String skillId, float proficiencyRequired) {
    }

    public static class Builder {
        private final String id;
        private final AbilityCategory category;
        private final int level;
        private SkillType type = SkillType.ACTIVE;
        private final List<Prerequisite> prerequisites = new ArrayList<>();
        private float baseCpCost = 0;
        private float baseOverload = 0;

        public Builder(String id, AbilityCategory category, int level) {
            this.id = id;
            this.category = category;
            this.level = level;
        }

        public Builder type(SkillType type) {
            this.type = type;
            return this;
        }

        public Builder prereq(String skillId, float proficiency) {
            this.prerequisites.add(new Prerequisite(skillId, proficiency));
            return this;
        }

        public Builder anyLevelPrereq(int level) {
            this.prerequisites.add(new Prerequisite("any_level_" + level, 0));
            return this;
        }

        public Builder cpCost(float cost) {
            this.baseCpCost = cost;
            return this;
        }

        public Builder overload(float overload) {
            this.baseOverload = overload;
            return this;
        }

        public Skill build() {
            return new Skill(id, category, level, type, prerequisites, baseCpCost, baseOverload);
        }
    }
}
