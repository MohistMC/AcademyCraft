package com.mohistmc.academy.skill;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;

/**
 * @author Mgazul
 * @date 2026/5/30 20:28
 */
public class PlayerAbilityData {

    public static final float BASE_MAX_CP = 2000;
    public static final float BASE_MAX_OVERLOAD = 500;
    public static final float BASE_CP_REGEN = 1.0f;

    private AbilityCategory currentAbility = null;
    private int playerLevel = 0;
    private float currentCp = BASE_MAX_CP;
    private float maxCp = BASE_MAX_CP;
    private float currentOverload = 0;
    private float maxOverload = BASE_MAX_OVERLOAD;
    private float cpRegenRate = BASE_CP_REGEN;

    private final Set<String> learnedSkills = new HashSet<>();
    private final Map<String, Float> skillProficiency = new HashMap<>();

    public AbilityCategory getCurrentAbility() {
        return currentAbility;
    }

    public void setCurrentAbility(AbilityCategory category) {
        this.currentAbility = category;
    }

    public boolean hasAbility() {
        return currentAbility != null;
    }

    public int getPlayerLevel() {
        return playerLevel;
    }

    public void setPlayerLevel(int level) {
        this.playerLevel = Math.max(0, Math.min(5, level));
    }

    public float getCurrentCp() {
        return currentCp;
    }

    public void setCurrentCp(float cp) {
        this.currentCp = Math.max(0, Math.min(cp, getMaxCp()));
    }

    public float getMaxCp() {
        return maxCp;
    }

    public void addMaxCp(float amount) {
        this.maxCp += amount;
    }

    public float getCurrentOverload() {
        return currentOverload;
    }

    public void setCurrentOverload(float overload) {
        this.currentOverload = Math.max(0, Math.min(overload, getMaxOverload()));
    }

    public void addOverload(float amount) {
        this.currentOverload = Math.min(currentOverload + amount, getMaxOverload());
    }

    public float getMaxOverload() {
        return maxOverload;
    }

    public void addMaxOverload(float amount) {
        this.maxOverload += amount;
    }

    public float getCpRegenRate() {
        return cpRegenRate;
    }

    public void addCpRegenRate(float multiplier) {
        this.cpRegenRate *= (1.0f + multiplier);
    }

    public boolean hasLearnedSkill(String skillId) {
        return learnedSkills.contains(skillId);
    }

    public void learnSkill(String skillId) {
        learnedSkills.add(skillId);
        if (!skillProficiency.containsKey(skillId)) {
            skillProficiency.put(skillId, 0.0f);
        }
    }

    public Set<String> getLearnedSkills() {
        return learnedSkills;
    }

    public float getProficiency(String skillId) {
        return skillProficiency.getOrDefault(skillId, 0.0f);
    }

    public void addProficiency(String skillId, float amount) {
        float current = skillProficiency.getOrDefault(skillId, 0.0f);
        skillProficiency.put(skillId, Math.min(1.0f, current + amount));
    }

    public void setProficiency(String skillId, float value) {
        skillProficiency.put(skillId, Math.max(0.0f, Math.min(1.0f, value)));
    }

    public boolean canLearnSkill(Skill skill) {
        if (skill.getCategory() != currentAbility) return false;
        if (learnedSkills.contains(skill.getId())) return false;
        if (skill.getLevel() > playerLevel + 1) return false;

        for (Skill.Prerequisite prereq : skill.getPrerequisites()) {
            String prereqId = prereq.getSkillId();
            if (prereqId.startsWith("any_level_")) {
                int requiredLevel = Integer.parseInt(prereqId.substring("any_level_".length()));
                boolean hasAnySkillAtLevel = SkillRegistry.getSkillsByCategory(currentAbility).stream()
                        .filter(s -> s.getLevel() == requiredLevel && !s.getId().equals(skill.getId()))
                        .anyMatch(s -> learnedSkills.contains(s.getId()));
                if (!hasAnySkillAtLevel) return false;
            } else {
                if (!learnedSkills.contains(prereqId)) return false;
                if (getProficiency(prereqId) < prereq.getProficiencyRequired()) return false;
            }
        }
        return true;
    }

    public boolean canUseSkill(Skill skill) {
        if (!hasLearnedSkill(skill.getId())) return false;
        if (currentCp < skill.getBaseCpCost()) return false;
        if (currentOverload >= maxOverload) return false;
        return true;
    }

    public void useSkill(Skill skill) {
        if (!canUseSkill(skill)) return;
        currentCp -= skill.getBaseCpCost();
        addOverload(skill.getBaseOverload());
        addProficiency(skill.getId(), 0.002f);
    }

    public void tick() {
        float overloadFactor = 1.0f - (currentOverload / maxOverload) * 0.5f;
        currentCp = Math.min(currentCp + cpRegenRate * overloadFactor, maxCp);
        currentOverload = Math.max(currentOverload - 0.5f, 0);
    }

    public void reset() {
        currentAbility = null;
        playerLevel = 0;
        currentCp = BASE_MAX_CP;
        maxCp = BASE_MAX_CP;
        currentOverload = 0;
        maxOverload = BASE_MAX_OVERLOAD;
        cpRegenRate = BASE_CP_REGEN;
        learnedSkills.clear();
        skillProficiency.clear();
    }

    public int computeEffectiveLevel() {
        if (!hasAbility()) return 0;
        int maxLevel = 0;
        for (String skillId : learnedSkills) {
            Skill skill = SkillRegistry.getSkill(skillId);
            if (skill != null && skill.getCategory() == currentAbility) {
                maxLevel = Math.max(maxLevel, skill.getLevel());
            }
        }
        return maxLevel;
    }

    public CompoundTag toSyncTag() {
        CompoundTag tag = new CompoundTag();
        if (hasAbility()) {
            tag.putString("ability", currentAbility.getId());
        }
        tag.putInt("level", playerLevel);
        tag.putFloat("cp", currentCp);
        tag.putFloat("max_cp", maxCp);
        tag.putFloat("overload", currentOverload);
        tag.putFloat("max_overload", maxOverload);
        tag.putFloat("cp_regen", cpRegenRate);

        ListTag learnedList = new ListTag();
        for (String skillId : learnedSkills) {
            learnedList.add(StringTag.valueOf(skillId));
        }
        tag.put("learned", learnedList);

        CompoundTag profTag = new CompoundTag();
        for (String skillId : learnedSkills) {
            profTag.putFloat(skillId, skillProficiency.getOrDefault(skillId, 0.0f));
        }
        tag.put("proficiency", profTag);
        return tag;
    }

    public static PlayerAbilityData fromSyncTag(CompoundTag tag) {
        PlayerAbilityData data = new PlayerAbilityData();
        if (tag.contains("ability")) {
            AbilityCategory cat = AbilityCategory.fromId(tag.getString("ability"));
            if (cat != null) data.setCurrentAbility(cat);
        }
        data.setPlayerLevel(tag.getInt("level"));
        data.setCurrentCp(tag.getFloat("cp"));
        if (tag.contains("max_cp")) data.addMaxCp(tag.getFloat("max_cp") - BASE_MAX_CP);
        data.setCurrentOverload(tag.getFloat("overload"));
        if (tag.contains("max_overload")) data.addMaxOverload(tag.getFloat("max_overload") - BASE_MAX_OVERLOAD);
        if (tag.contains("cp_regen")) data.addCpRegenRate(tag.getFloat("cp_regen") / BASE_CP_REGEN - 1.0f);

        if (tag.contains("learned")) {
            ListTag list = tag.getList("learned", net.minecraft.nbt.Tag.TAG_STRING);
            for (int i = 0; i < list.size(); i++) {
                data.learnSkill(list.getString(i));
            }
        }
        if (tag.contains("proficiency")) {
            CompoundTag profTag = tag.getCompound("proficiency");
            for (String key : profTag.getAllKeys()) {
                data.setProficiency(key, profTag.getFloat(key));
            }
        }
        return data;
    }
}
