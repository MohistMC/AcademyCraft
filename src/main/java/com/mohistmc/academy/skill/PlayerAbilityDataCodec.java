package com.mohistmc.academy.skill;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;


/**
 * @author Mgazul
 * @date 2026/5/30 20:30
 */
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

        return tag;
    }
}
