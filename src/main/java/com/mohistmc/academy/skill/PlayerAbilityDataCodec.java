package com.mohistmc.academy.skill;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;

public class PlayerAbilityDataCodec implements IAttachmentSerializer<CompoundTag, PlayerAbilityData> {

    public static final PlayerAbilityDataCodec INSTANCE = new PlayerAbilityDataCodec();

    @Override
    public @NotNull PlayerAbilityData read(@NotNull IAttachmentHolder holder, CompoundTag tag, HolderLookup.@NotNull Provider provider) {
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

        if (tag.contains("terminal_installed")) {
            data.setTerminalInstalled(tag.getBoolean("terminal_installed"));
        }
        if (tag.contains("installed_apps")) {
            ListTag appList = tag.getList("installed_apps", Tag.TAG_STRING);
            for (int i = 0; i < appList.size(); i++) {
                data.installApp(appList.getString(i));
            }
        }

        if (tag.contains("loaded_media")) {
            ListTag mediaList = tag.getList("loaded_media", Tag.TAG_STRING);
            for (int i = 0; i < mediaList.size(); i++) {
                data.addLoadedMedia(mediaList.getString(i));
            }
        }

        if (tag.contains("misaka_id")) {
            data.setMisakaId(tag.getInt("misaka_id"));
        }

        // 开发者模式
        if (tag.contains("dev_mode")) {
            data.setDevMode(tag.getBoolean("dev_mode"));
        }

        // 冷却
        if (tag.contains("cooldowns")) {
            CompoundTag cdTag = tag.getCompound("cooldowns");
            for (String key : cdTag.getAllKeys()) {
                int cd = cdTag.getInt(key);
                if (cd > 0) data.setCooldown(key, cd);
            }
        }

        return data;
    }

    @Override
    public CompoundTag write(PlayerAbilityData data, HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();

        if (data.hasAbility()) {
            tag.putString("ability", data.getCurrentAbility().id());
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

        tag.putBoolean("terminal_installed", data.isTerminalInstalled());
        ListTag appList = new ListTag();
        for (String appId : data.getInstalledApps()) {
            appList.add(net.minecraft.nbt.StringTag.valueOf(appId));
        }
        tag.put("installed_apps", appList);

        ListTag mediaList = new ListTag();
        for (String mediaId : data.getLoadedMedia()) {
            mediaList.add(net.minecraft.nbt.StringTag.valueOf(mediaId));
        }
        tag.put("loaded_media", mediaList);

        tag.putInt("misaka_id", data.getMisakaId());

        // 开发者模式
        tag.putBoolean("dev_mode", data.isDevMode());

        // 冷却
        CompoundTag cdTag = new CompoundTag();
        data.cooldowns.forEach((skillId, ticks) -> {
            if (ticks > 0) cdTag.putInt(skillId, ticks);
        });
        tag.put("cooldowns", cdTag);

        return tag;
    }
}
