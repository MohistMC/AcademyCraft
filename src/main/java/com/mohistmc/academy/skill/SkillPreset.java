package com.mohistmc.academy.skill;

import java.util.Arrays;

public class SkillPreset {

    public static final int SLOT_COUNT = 4;

    private final String[] slots = new String[SLOT_COUNT];

    public String getSlot(int index) {
        if (index < 0 || index >= SLOT_COUNT) return null;
        return slots[index];
    }

    public void setSlot(int index, String skillId) {
        if (index < 0 || index >= SLOT_COUNT) return;
        slots[index] = skillId;
    }

    public void clearSlot(int index) {
        if (index < 0 || index >= SLOT_COUNT) return;
        slots[index] = null;
    }

    public void clearAll() {
        Arrays.fill(slots, null);
    }

    public boolean isEmpty(int index) {
        return getSlot(index) == null;
    }

    public Skill getSkillInSlot(int index) {
        String id = getSlot(index);
        if (id == null) return null;
        return SkillRegistry.getSkill(id);
    }
}
