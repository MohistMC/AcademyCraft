package com.mohistmc.academy.skill;

import com.mohistmc.academy.network.LearnSkillPacket;
import com.mohistmc.academy.terminal.AppRegistry;
import com.mohistmc.academy.terminal.TerminalApp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class PlayerAbilityData {

    public static final float BASE_MAX_CP = 2000;
    public static final float BASE_MAX_OVERLOAD = 500;
    public static final float BASE_CP_REGEN = 1.0f;
    public static final int PRESET_COUNT = 4;

    private AbilityCategory currentAbility = null;
    private int playerLevel = 0;
    private float currentCp = BASE_MAX_CP;
    private float maxCp = BASE_MAX_CP;
    private float currentOverload = 0;
    private float maxOverload = BASE_MAX_OVERLOAD;
    private float cpRegenRate = BASE_CP_REGEN;

    private final Set<String> learnedSkills = new HashSet<>();
    private final Map<String, Float> skillProficiency = new HashMap<>();

    private final SkillPreset[] presets = new SkillPreset[PRESET_COUNT];
    private int currentPreset = 0;
    private boolean abilityActive = false;

    private boolean terminalInstalled = false;
    private final Set<String> installedApps = new HashSet<>();
    private final Set<String> loadedMedia = new HashSet<>();
    private int misakaId = -1;


    // ==================== 开发者模式 ====================
    private boolean devMode = false;

    public PlayerAbilityData() {
        for (int i = 0; i < PRESET_COUNT; i++) {
            presets[i] = new SkillPreset();
        }
        installedApps.add(AppRegistry.SETTINGS.getAppId());
    }

    public boolean isDevMode() {
        return devMode;
    }

    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
    }

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
        this.playerLevel = Math.clamp(level, 0, 5);
    }

    public float getCurrentCp() {
        return currentCp;
    }

    public void setCurrentCp(float cp) {
        this.currentCp =  Math.clamp(cp, 0, getMaxCp());
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
        this.currentOverload = Math.clamp(overload, 0, getMaxOverload());
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
        if (devMode) return true;
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
        skillProficiency.put(skillId, Math.clamp(value, 0.0f, 1.0f));
    }

    public boolean canLearnSkill(Skill skill) {
        if (devMode) return true;
        if (skill.getCategory() != currentAbility) return false;
        if (learnedSkills.contains(skill.getId())) return false;
        if (skill.getLevel() > playerLevel + 1) return false;

        for (Skill.Prerequisite prereq : skill.getPrerequisites()) {
            String prereqId = prereq.skillId();
            if (prereqId.startsWith("any_level_")) {
                int requiredLevel = Integer.parseInt(prereqId.substring("any_level_".length()));
                boolean hasAnySkillAtLevel = SkillRegistry.getSkillsByCategory(currentAbility).stream()
                        .filter(s -> s.getLevel() == requiredLevel && !s.getId().equals(skill.getId()))
                        .anyMatch(s -> learnedSkills.contains(s.getId()));
                if (!hasAnySkillAtLevel) return false;
            } else {
                if (!learnedSkills.contains(prereqId)) return false;
                if (getProficiency(prereqId) < prereq.proficiencyRequired()) return false;
            }
        }
        return true;
    }

    public boolean canUseSkill(Skill skill) {
        if (isDevMode()) return true;
        if (!hasLearnedSkill(skill.getId())) return false;
        if (currentCp < skill.getBaseCpCost()) return false;
        return !(currentOverload >= maxOverload);
    }

    public void useSkill(Skill skill) {
        if (!canUseSkill(skill)) return;
        if (!isDevMode()) {
            currentCp -= skill.getBaseCpCost();
            addOverload(skill.getBaseOverload());
        }
        addProficiency(skill.getId(), 0.002f);
    }

    // ==================== 激活状态 ====================

    public boolean isAbilityActive() {
        return abilityActive;
    }

    public void setAbilityActive(boolean active) {
        this.abilityActive = active;
    }

    public void toggleAbilityActive() {
        this.abilityActive = !this.abilityActive;
    }

    // ==================== 预设系统 ====================

    public SkillPreset getPreset(int index) {
        if (index < 0 || index >= PRESET_COUNT) return presets[0];
        return presets[index];
    }

    public SkillPreset getCurrentPreset() {
        return presets[currentPreset];
    }

    public int getCurrentPresetIndex() {
        return currentPreset;
    }

    public void setCurrentPreset(int index) {
        this.currentPreset = Math.clamp(index, 0, PRESET_COUNT - 1);
    }

    public void setSlot(int presetIndex, int slotIndex, String skillId) {
        presets[presetIndex].setSlot(slotIndex, skillId);
    }

    public void clearSlot(int presetIndex, int slotIndex) {
        presets[presetIndex].clearSlot(slotIndex);
    }

    public String getSlotSkillId(int presetIndex, int slotIndex) {
        return presets[presetIndex].getSlot(slotIndex);
    }

    public Skill getSlotSkill(int presetIndex, int slotIndex) {
        return presets[presetIndex].getSkillInSlot(slotIndex);
    }

    // ==================== 数据终端 ====================

    public boolean isTerminalInstalled() {
        return terminalInstalled;
    }

    public void setTerminalInstalled(boolean installed) {
        this.terminalInstalled = installed;
    }

    public int getMisakaId() {
        return misakaId;
    }

    public void setMisakaId(int misakaId) {
        this.misakaId = misakaId;
    }

    public boolean hasApp(String appId) {
        return installedApps.contains(appId);
    }

    public boolean hasApp(TerminalApp app) {
        return installedApps.contains(app.getAppId());
    }

    public void installApp(String appId) {
        installedApps.add(appId);
    }

    public void installApp(TerminalApp app) {
        installedApps.add(app.getAppId());
    }

    public Set<String> getInstalledApps() {
        return installedApps;
    }

    public Set<String> getLoadedMedia() {
        return loadedMedia;
    }

    public boolean hasLoadedMedia(String mediaId) {
        return loadedMedia.contains(mediaId);
    }

    public void addLoadedMedia(String mediaId) {
        loadedMedia.add(mediaId);
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
        abilityActive = false;
        for (SkillPreset preset : presets) {
            preset.clearAll();
        }
        currentPreset = 0;
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

    public void syncTo(Player player) {
        PlayerAbilityData copy = PlayerAbilityData.fromSyncTag(this.toSyncTag());
        player.setData(AcademyAttachments.PLAYER_ABILITY, copy);
        if (player instanceof ServerPlayer serverPlayer) {
            LearnSkillPacket.syncToClient(serverPlayer);
        }
    }

    public CompoundTag toSyncTag() {
        CompoundTag tag = new CompoundTag();
        if (hasAbility()) {
            tag.putString("ability", currentAbility.id());
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

        tag.putInt("current_preset", currentPreset);
        CompoundTag presetsTag = new CompoundTag();
        for (int p = 0; p < PRESET_COUNT; p++) {
            CompoundTag presetTag = new CompoundTag();
            for (int s = 0; s < SkillPreset.SLOT_COUNT; s++) {
                String skillId = presets[p].getSlot(s);
                if (skillId != null) {
                    presetTag.putString("slot_" + s, skillId);
                }
            }
            presetsTag.put("preset_" + p, presetTag);
        }
        tag.put("presets", presetsTag);

        tag.putBoolean("ability_active", abilityActive);

        tag.putBoolean("terminal_installed", terminalInstalled);
        ListTag appList = new ListTag();
        for (String appId : installedApps) {
            appList.add(StringTag.valueOf(appId));
        }
        tag.put("installed_apps", appList);

        tag.putInt("misaka_id", misakaId);

        ListTag mediaList = new ListTag();
        for (String mediaId : loadedMedia) {
            mediaList.add(StringTag.valueOf(mediaId));
        }
        tag.put("loaded_media", mediaList);

        // 保存开发者模式状态
        tag.putBoolean("dev_mode", devMode);

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

        if (tag.contains("current_preset")) {
            data.setCurrentPreset(tag.getInt("current_preset"));
        }
        if (tag.contains("presets")) {
            CompoundTag presetsTag = tag.getCompound("presets");
            for (int p = 0; p < PRESET_COUNT; p++) {
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

        if (tag.contains("misaka_id")) {
            data.setMisakaId(tag.getInt("misaka_id"));
        }

        if (tag.contains("installed_apps")) {
            ListTag appList = tag.getList("installed_apps", net.minecraft.nbt.Tag.TAG_STRING);
            for (int i = 0; i < appList.size(); i++) {
                data.installApp(appList.getString(i));
            }
        }

        if (tag.contains("loaded_media")) {
            ListTag mediaList = tag.getList("loaded_media", net.minecraft.nbt.Tag.TAG_STRING);
            for (int i = 0; i < mediaList.size(); i++) {
                data.addLoadedMedia(mediaList.getString(i));
            }
        }

        // 读取开发者模式状态
        if (tag.contains("dev_mode")) {
            data.setDevMode(tag.getBoolean("dev_mode"));
        }

        return data;
    }
}
