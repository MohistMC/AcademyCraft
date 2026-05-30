package com.mohistmc.academy.skill;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

public class PlayerAbilityDataCodec implements IAttachmentSerializer<CompoundTag, PlayerAbilityData> {

    @Override
    public PlayerAbilityData read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
        PlayerAbilityData data = new PlayerAbilityData();

        if (tag.contains("ability")) {
            String abilityId = tag.getString("ability");
            AbilityCategory cat = AbilityCategory.fromId(abilityId);
            if (cat != null) data.setCurrentAbility(cat);
        }

        data.setPlayerLevel(tag.getInt("level"));
        data.setCurrentCp(tag.getFloat("cp"));
        if (tag.contains("max_cp")) data.addMaxCp(tag.getFloat("max_cp") - PlayerAbilityData.BASE_MAX_CP);
        data.setCurrentOverload(tag.getFloat("overload"));
        if (tag.contains("max_overload")) data.addMaxOverload(tag.getFloat("max_overload") - PlayerAbilityData.BASE_MAX_OVERLOAD);
        if (tag.contains("cp_regen")) data.addCpRegenRate(tag.getFloat("cp_regen") / PlayerAbilityData.BASE_CP_REGEN - 1.0f);

        if (tag.contains("learned")) {
            ListTag list = tag.getList("learned", Tag.TAG_STRING);
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

        if (tag.contains("current_preset")) {
            data.setCurrentPreset(tag.getInt("current_preset"));
        }
        if (tag.contains("presets")) {
            CompoundTag presetsTag = tag.getCompound("presets");
            for (int p = 0; p < PlayerAbilityData.PRESET_COUNT; p++) {
                String presetKey = "preset_" + p;
                if (presetsTag.contains(presetKey)) {
                    CompoundTag presetTag = presetsTag.getCompound(presetKey);
                    for (int s = 0; s < SkillPreset.SLOT_COUNT; s++) {
                        String slotKey = "slot_" + s;
                        if (presetTag.contains(slotKey)) {
                            data.setSlot(p, s, presetTag.getString(slotKey));
                        }
                    }
                }
            }
        }

        if (tag.contains("ability_active")) {
            data.setAbilityActive(tag.getBoolean("ability_active"));
        }

        return data;
    }

    @Override
    public CompoundTag write(PlayerAbilityData data, HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();

        if (data.hasAbility()) {
            tag.putString("ability", data.getCurrentAbility().getId());
        }

        tag.putInt("level", data.getPlayerLevel());
        tag.putFloat("cp", data.getCurrentCp());
        tag.putFloat("max_cp", data.getMaxCp());
        tag.putFloat("overload", data.getCurrentOverload());
        tag.putFloat("max_overload", data.getMaxOverload());
        tag.putFloat("cp_regen", data.getCpRegenRate());

        ListTag learnedList = new ListTag();
        for (String skillId : data.getLearnedSkills()) {
            learnedList.add(net.minecraft.nbt.StringTag.valueOf(skillId));
        }
        tag.put("learned", learnedList);

        CompoundTag profTag = new CompoundTag();
        for (String skillId : data.getLearnedSkills()) {
            profTag.putFloat(skillId, data.getProficiency(skillId));
        }
        tag.put("proficiency", profTag);

        tag.putInt("current_preset", data.getCurrentPresetIndex());
        CompoundTag presetsTag = new CompoundTag();
        for (int p = 0; p < PlayerAbilityData.PRESET_COUNT; p++) {
            CompoundTag presetTag = new CompoundTag();
            SkillPreset preset = data.getPreset(p);
            for (int s = 0; s < SkillPreset.SLOT_COUNT; s++) {
                String skillId = preset.getSlot(s);
                if (skillId != null) {
                    presetTag.putString("slot_" + s, skillId);
                }
            }
            presetsTag.put("preset_" + p, presetTag);
        }
        tag.put("presets", presetsTag);

        tag.putBoolean("ability_active", data.isAbilityActive());

        return tag;
    }
}
