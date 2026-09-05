package com.mohistmc.academy.skill;

import com.mohistmc.academy.skill.ability.MagManipEffect;
import com.mohistmc.academy.skill.ability.aerohand.AeroSeparatorEffect;
import com.mohistmc.academy.skill.ability.aerohand.AirBladeEffect;
import com.mohistmc.academy.skill.ability.aerohand.AirCoolingEffect;
import com.mohistmc.academy.skill.ability.aerohand.AirJetEffect;
import com.mohistmc.academy.skill.ability.aerohand.AirWallEffect;
import com.mohistmc.academy.skill.ability.aerohand.BomberLanceEffect;
import com.mohistmc.academy.skill.ability.aerohand.StormCoreEffect;
import com.mohistmc.academy.skill.ability.aerohand.VolcanicBallEffect;
import com.mohistmc.academy.skill.ability.electromaster.ArcGenEffect;
import com.mohistmc.academy.skill.ability.electromaster.BodyIntensifyEffect;
import com.mohistmc.academy.skill.ability.electromaster.ChargingEffect;
import com.mohistmc.academy.skill.ability.electromaster.MagMovementEffect;
import com.mohistmc.academy.skill.ability.electromaster.MineDetectEffect;
import com.mohistmc.academy.skill.ability.electromaster.RailgunEffect;
import com.mohistmc.academy.skill.ability.electromaster.ThunderBoltEffect;
import com.mohistmc.academy.skill.ability.electromaster.ThunderClapEffect;
import com.mohistmc.academy.skill.ability.meltdowner.ElectronBombEffect;
import com.mohistmc.academy.skill.ability.meltdowner.ElectronMissileEffect;
import com.mohistmc.academy.skill.ability.meltdowner.JetEngineEffect;
import com.mohistmc.academy.skill.ability.meltdowner.LightShieldEffect;
import com.mohistmc.academy.skill.ability.meltdowner.MeltdownerEffect;
import com.mohistmc.academy.skill.ability.meltdowner.MineRayBasicEffect;
import com.mohistmc.academy.skill.ability.meltdowner.MineRayExpertEffect;
import com.mohistmc.academy.skill.ability.meltdowner.MineRayLuckEffect;
import com.mohistmc.academy.skill.ability.meltdowner.RayBarrageEffect;
import com.mohistmc.academy.skill.ability.meltdowner.ScatterBombEffect;
import com.mohistmc.academy.skill.ability.telekinesis.CruiseBombEffect;
import com.mohistmc.academy.skill.ability.telekinesis.OverloadThinkingEffect;
import com.mohistmc.academy.skill.ability.telekinesis.PaperDrillEffect;
import com.mohistmc.academy.skill.ability.telekinesis.PsychoNeedlingEffect;
import com.mohistmc.academy.skill.ability.telekinesis.PsychoSlamEffect;
import com.mohistmc.academy.skill.ability.telekinesis.PsychoThrowingEffect;
import com.mohistmc.academy.skill.ability.telekinesis.PsychoTransmissionEffect;
import com.mohistmc.academy.skill.ability.teleporter.FlashingEffect;
import com.mohistmc.academy.skill.ability.teleporter.FleshRippingEffect;
import com.mohistmc.academy.skill.ability.teleporter.LocationTeleportEffect;
import com.mohistmc.academy.skill.ability.teleporter.MarkTeleportEffect;
import com.mohistmc.academy.skill.ability.teleporter.PenetrateTeleportEffect;
import com.mohistmc.academy.skill.ability.teleporter.ShiftTpEffect;
import com.mohistmc.academy.skill.ability.teleporter.ThreateningTeleportEffect;
import com.mohistmc.academy.skill.ability.vecmanip.BloodRetroEffect;
import com.mohistmc.academy.skill.ability.vecmanip.DirBlastEffect;
import com.mohistmc.academy.skill.ability.vecmanip.DirShockEffect;
import com.mohistmc.academy.skill.ability.vecmanip.GroundShockEffect;
import com.mohistmc.academy.skill.ability.vecmanip.PlasmaCannonEffect;
import com.mohistmc.academy.skill.ability.vecmanip.StormWingEffect;
import com.mohistmc.academy.skill.ability.vecmanip.VecAccelEffect;
import com.mohistmc.academy.skill.ability.vecmanip.VecReflectionEffect;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SkillRegistry {

    private static final Map<String, Skill> SKILLS = new HashMap<>();
    private static final Map<String, SkillEffect> EFFECTS = new HashMap<>();
    private static final Map<AbilityCategory, List<Skill>> SKILLS_BY_CATEGORY = new HashMap<>();
    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;
        initialized = true;

        registerBuiltinEffects();
        registerAllSkills();

        bindEffects();
    }

    public static void registerSkill(Skill skill) {
        // 跨职业通用课(brain_course/brain_course_advanced/mind_course)在所有职业下用相同
        // id 注册, 全局索引 SKILLS 无法区分职业, 故仅以 PUT-IF-ABSENT 保留首次注册的那一份,
        // 消除原先"后注册覆盖前注册"的隐式且不确定行为。渲染类 getSkill(id) 对通用课走
        // generic 翻译/图标路径, 任意职业副本显示完全一致; 职业敏感的查询统一走
        // getSkill(AbilityCategory, id) (#26), 由 SKILLS_BY_CATEGORY 按职业正确解析。
        SKILLS.putIfAbsent(skill.getId(), skill);
        SKILLS_BY_CATEGORY.computeIfAbsent(skill.getCategory(), k -> new ArrayList<>()).add(skill);
        SkillEffect effect = EFFECTS.get(skill.getId());
        if (effect != null) {
            skill.setEffect(effect);
        }
    }

    public static void registerEffect(SkillEffect effect) {
        EFFECTS.put(effect.getId(), effect);
        Skill skill = SKILLS.get(effect.getId());
        if (skill != null) {
            skill.setEffect(effect);
        }
    }

    private static void registerBuiltinEffects() {
        registerEffect(new ArcGenEffect());
        registerEffect(new ChargingEffect());
        registerEffect(new MagMovementEffect());
        registerEffect(new MagManipEffect());
        registerEffect(new BodyIntensifyEffect());
        registerEffect(new MineDetectEffect());
        registerEffect(new ThunderBoltEffect());
        registerEffect(new RailgunEffect());
        registerEffect(new ThunderClapEffect());

        // Teleporter
        registerEffect(new ThreateningTeleportEffect());
        registerEffect(new PenetrateTeleportEffect());
        registerEffect(new MarkTeleportEffect());
        registerEffect(new LocationTeleportEffect());
        registerEffect(new FleshRippingEffect());
        registerEffect(new FlashingEffect());
        registerEffect(new ShiftTpEffect());

        // Meltdowner
        registerEffect(new ElectronBombEffect());
        registerEffect(new ScatterBombEffect());
        registerEffect(new LightShieldEffect());
        registerEffect(new MeltdownerEffect());
        registerEffect(new MineRayBasicEffect());
        registerEffect(new MineRayExpertEffect());
        registerEffect(new MineRayLuckEffect());
        registerEffect(new RayBarrageEffect());
        registerEffect(new JetEngineEffect());
        registerEffect(new ElectronMissileEffect());

        // Vecmanip
        registerEffect(new DirShockEffect());
        registerEffect(new GroundShockEffect());
        registerEffect(new VecAccelEffect());
        registerEffect(new DirBlastEffect());
        registerEffect(new StormWingEffect());
        registerEffect(new BloodRetroEffect());
        registerEffect(new VecReflectionEffect());
        registerEffect(new PlasmaCannonEffect());

        // Aerohand
        registerEffect(new VolcanicBallEffect());
        registerEffect(new AirBladeEffect());
        registerEffect(new AirCoolingEffect());
        registerEffect(new AirJetEffect());
        registerEffect(new AirWallEffect());
        registerEffect(new BomberLanceEffect());
        registerEffect(new StormCoreEffect());
        registerEffect(new AeroSeparatorEffect());

        // Telekinesis
        registerEffect(new PsychoThrowingEffect());
        registerEffect(new PsychoTransmissionEffect());
        registerEffect(new PsychoNeedlingEffect());
        registerEffect(new CruiseBombEffect());
        registerEffect(new OverloadThinkingEffect());
        registerEffect(new PsychoSlamEffect());
        registerEffect(new PaperDrillEffect());
    }

    private static void bindEffects() {
        for (Map.Entry<String, SkillEffect> entry : EFFECTS.entrySet()) {
            Skill skill = SKILLS.get(entry.getKey());
            if (skill != null) {
                skill.setEffect(entry.getValue());
            }
        }
    }

    public static Skill getSkill(String id) {
        return SKILLS.get(id);
    }

    /**
     * 按职业解析技能。技能注册表中 brain_course / brain_course_advanced / mind_course
     * 这三个通用被动技能在所有职业下使用相同 id 注册，全局按 id 查找会恒定返回最后注册
     * 的职业（念力）实例，导致其它职业无法学习。按当前职业解析可得到正确的技能对象。
     */
    public static Skill getSkill(AbilityCategory category, String id) {
        return getSkillsByCategory(category).stream()
                .filter(s -> s.getId().equals(id))
                .findFirst().orElse(null);
    }

    public static SkillEffect getEffect(String id) {
        return EFFECTS.get(id);
    }

    public static List<Skill> getSkillsByCategory(AbilityCategory category) {
        return SKILLS_BY_CATEGORY.getOrDefault(category, Collections.emptyList());
    }

    public static List<Skill> getAllSkills() {
        return new ArrayList<>(SKILLS.values());
    }

    public static List<Skill> getSkillsByLevel(AbilityCategory category, int level) {
        return getSkillsByCategory(category).stream()
                .filter(s -> s.getLevel() == level)
                .collect(Collectors.toList());
    }

    // ==================== 内置职业注册 ====================
    // ==================== 内置职业注册（数据驱动） ====================
    // 原先 6 个 registerXxxSkills() 方法里约 410 行 new Skill.Builder(...) 样板
    // 全部外置为下方固定大小的静态表 BUILTIN_SKILLS，由 registerAllSkills() 一次性
    // 有界循环注册。注册顺序 / 字段与重构前逐字节一致，运行时行为不变。

    private record Req(String id, float ratio) {}

    private record SkillDef(
            String id,
            AbilityCategory category,
            int level,
            SkillType type,
            List<Req> reqs,
            int anyLevel,
            float cpCost,
            float overload) {}

    private static final List<SkillDef> BUILTIN_SKILLS = buildSkillTable();

    private static List<SkillDef> buildSkillTable() {
        List<SkillDef> defs = new ArrayList<>();
        AbilityCategory c;

        // ===== 电磁使 ELECTROMASTER =====
        c = AbilityCategory.ELECTROMASTER;
        defs.add(new SkillDef("arc_gen", c, 1, SkillType.ACTIVE, List.of(), 0, 10, 5));
        defs.add(new SkillDef("charging", c, 1, SkillType.ACTIVE, List.of(new Req("arc_gen", 0.3f)), 0, 5, 10));
        defs.add(new SkillDef("mag_movement", c, 2, SkillType.ACTIVE, List.of(new Req("arc_gen", 1.0f)), 0, 8, 15));
        defs.add(new SkillDef("mag_manip", c, 2, SkillType.ACTIVE, List.of(new Req("mag_movement", 0.5f)), 0, 15, 20));
        defs.add(new SkillDef("body_intensify", c, 3, SkillType.ACTIVE, List.of(new Req("arc_gen", 1.0f), new Req("charging", 1.0f)), 0, 30, 40));
        defs.add(new SkillDef("mine_detect", c, 3, SkillType.ACTIVE, List.of(new Req("mag_manip", 1.0f)), 0, 20, 10));
        defs.add(new SkillDef("brain_course", c, 3, SkillType.PASSIVE, List.of(), 3, 0, 0));
        defs.add(new SkillDef("thunder_bolt", c, 4, SkillType.ACTIVE, List.of(new Req("charging", 0.7f)), 0, 40, 30));
        defs.add(new SkillDef("railgun", c, 4, SkillType.ACTIVE, List.of(new Req("thunder_bolt", 0.3f), new Req("mag_manip", 1.0f)), 0, 80, 60));
        defs.add(new SkillDef("brain_course_advanced", c, 4, SkillType.PASSIVE, List.of(), 4, 0, 0));
        defs.add(new SkillDef("thunder_clap", c, 5, SkillType.ACTIVE, List.of(new Req("thunder_bolt", 1.0f)), 0, 100, 80));
        defs.add(new SkillDef("mind_course", c, 5, SkillType.PASSIVE, List.of(), 5, 0, 0));

        // ===== 融解者 MELTDOWNER =====
        c = AbilityCategory.MELTDOWNER;
        defs.add(new SkillDef("electron_bomb", c, 1, SkillType.ACTIVE, List.of(), 0, 5, 2));
        defs.add(new SkillDef("rad_intensify", c, 1, SkillType.PASSIVE, List.of(new Req("electron_bomb", 0.0f)), 0, 0, 0));
        defs.add(new SkillDef("scatter_bomb", c, 2, SkillType.ACTIVE, List.of(new Req("electron_bomb", 0.8f)), 0, 25, 50));
        defs.add(new SkillDef("light_shield", c, 2, SkillType.ACTIVE, List.of(new Req("electron_bomb", 0.5f)), 0, 40, 30));
        defs.add(new SkillDef("meltdowner", c, 3, SkillType.ACTIVE, List.of(new Req("light_shield", 0.8f), new Req("scatter_bomb", 0.8f)), 0, 60, 50));
        defs.add(new SkillDef("mine_ray_basic", c, 3, SkillType.ACTIVE, List.of(new Req("scatter_bomb", 0.5f)), 0, 10, 5));
        defs.add(new SkillDef("brain_course", c, 3, SkillType.PASSIVE, List.of(), 3, 0, 0));
        defs.add(new SkillDef("mine_ray_expert", c, 4, SkillType.ACTIVE, List.of(new Req("mine_ray_basic", 0.5f)), 0, 12, 5));
        defs.add(new SkillDef("ray_barrage", c, 4, SkillType.ACTIVE, List.of(new Req("meltdowner", 0.3f)), 0, 50, 40));
        defs.add(new SkillDef("jet_engine", c, 4, SkillType.ACTIVE, List.of(new Req("meltdowner", 1.0f)), 0, 35, 25));
        defs.add(new SkillDef("brain_course_advanced", c, 4, SkillType.PASSIVE, List.of(), 4, 0, 0));
        defs.add(new SkillDef("mine_ray_luck", c, 5, SkillType.ACTIVE, List.of(new Req("mine_ray_expert", 0.5f)), 0, 15, 5));
        defs.add(new SkillDef("electron_missile", c, 5, SkillType.ACTIVE, List.of(new Req("ray_barrage", 0.5f)), 0, 70, 60));
        defs.add(new SkillDef("mind_course", c, 5, SkillType.PASSIVE, List.of(), 5, 0, 0));

        // ===== 传送者 TELEPORTER =====
        c = AbilityCategory.TELEPORTER;
        defs.add(new SkillDef("threatening_teleport", c, 1, SkillType.ACTIVE, List.of(), 0, 15, 10));
        defs.add(new SkillDef("dim_folding_theorem", c, 1, SkillType.PASSIVE, List.of(new Req("threatening_teleport", 0.0f)), 0, 0, 0));
        defs.add(new SkillDef("penetrate_teleport", c, 2, SkillType.ACTIVE, List.of(new Req("threatening_teleport", 0.3f)), 0, 20, 15));
        defs.add(new SkillDef("mark_teleport", c, 2, SkillType.ACTIVE, List.of(new Req("threatening_teleport", 0.5f)), 0, 25, 15));
        defs.add(new SkillDef("brain_course", c, 3, SkillType.PASSIVE, List.of(), 3, 0, 0));
        defs.add(new SkillDef("location_teleport", c, 3, SkillType.ACTIVE, List.of(new Req("mark_teleport", 0.8f), new Req("penetrate_teleport", 0.8f)), 0, 80, 40));
        defs.add(new SkillDef("flesh_ripping", c, 4, SkillType.ACTIVE, List.of(new Req("location_teleport", 0.5f)), 0, 50, 35));
        defs.add(new SkillDef("brain_course_advanced", c, 4, SkillType.PASSIVE, List.of(), 4, 0, 0));
        defs.add(new SkillDef("flashing", c, 5, SkillType.ACTIVE, List.of(new Req("flesh_ripping", 0.5f)), 0, 60, 30));
        defs.add(new SkillDef("shift_tp", c, 5, SkillType.ACTIVE, List.of(new Req("location_teleport", 0.8f)), 0, 30, 20));
        defs.add(new SkillDef("space_fluct", c, 5, SkillType.PASSIVE, List.of(new Req("flesh_ripping", 0.5f)), 0, 0, 0));
        defs.add(new SkillDef("mind_course", c, 5, SkillType.PASSIVE, List.of(), 5, 0, 0));

        // ===== 矢量操作 VECMANIP =====
        c = AbilityCategory.VECMANIP;
        defs.add(new SkillDef("dir_shock", c, 1, SkillType.ACTIVE, List.of(), 0, 8, 5));
        defs.add(new SkillDef("ground_shock", c, 1, SkillType.ACTIVE, List.of(), 0, 12, 10));
        defs.add(new SkillDef("vec_accel", c, 2, SkillType.ACTIVE, List.of(new Req("ground_shock", 0.5f)), 0, 15, 10));
        defs.add(new SkillDef("vec_deviation", c, 2, SkillType.PASSIVE, List.of(new Req("dir_shock", 0.5f)), 0, 0, 0));
        defs.add(new SkillDef("dir_blast", c, 3, SkillType.ACTIVE, List.of(new Req("dir_shock", 1.0f), new Req("vec_accel", 0.5f)), 0, 30, 25));
        defs.add(new SkillDef("storm_wing", c, 3, SkillType.ACTIVE, List.of(new Req("vec_accel", 0.8f)), 0, 25, 20));
        defs.add(new SkillDef("brain_course", c, 3, SkillType.PASSIVE, List.of(), 3, 0, 0));
        defs.add(new SkillDef("blood_retro", c, 4, SkillType.ACTIVE, List.of(new Req("dir_blast", 0.5f)), 0, 60, 40));
        defs.add(new SkillDef("vec_reflection", c, 4, SkillType.ACTIVE, List.of(new Req("vec_deviation", 1.0f)), 0, 40, 30));
        defs.add(new SkillDef("brain_course_advanced", c, 4, SkillType.PASSIVE, List.of(), 4, 0, 0));
        defs.add(new SkillDef("plasma_cannon", c, 5, SkillType.ACTIVE, List.of(new Req("blood_retro", 0.5f), new Req("storm_wing", 0.8f)), 0, 120, 100));
        defs.add(new SkillDef("mind_course", c, 5, SkillType.PASSIVE, List.of(), 5, 0, 0));

        // ===== 念力操作 AEROHAND =====
        c = AbilityCategory.AEROHAND;
        defs.add(new SkillDef("volcanic_ball", c, 1, SkillType.ACTIVE, List.of(), 0, 10, 5));
        defs.add(new SkillDef("ascending_air", c, 1, SkillType.PASSIVE, List.of(), 0, 0, 0));
        defs.add(new SkillDef("air_blade", c, 2, SkillType.ACTIVE, List.of(new Req("volcanic_ball", 0.5f)), 0, 12, 15));
        defs.add(new SkillDef("airflow", c, 2, SkillType.PASSIVE, List.of(new Req("ascending_air", 0.5f)), 0, 0, 0));
        defs.add(new SkillDef("air_cooling", c, 3, SkillType.ACTIVE, List.of(new Req("ascending_air", 0.0f)), 0, 20, 20));
        defs.add(new SkillDef("air_wall", c, 3, SkillType.ACTIVE, List.of(new Req("air_blade", 0.5f)), 0, 30, 25));
        defs.add(new SkillDef("air_jet", c, 3, SkillType.ACTIVE, List.of(new Req("airflow", 0.1f)), 0, 15, 10));
        defs.add(new SkillDef("brain_course", c, 3, SkillType.PASSIVE, List.of(), 3, 0, 0));
        defs.add(new SkillDef("offense_armour", c, 4, SkillType.PASSIVE, List.of(new Req("air_wall", 1.0f)), 0, 0, 0));
        defs.add(new SkillDef("bomber_lance", c, 4, SkillType.ACTIVE, List.of(new Req("air_wall", 0.5f)), 0, 50, 40));
        defs.add(new SkillDef("brain_course_advanced", c, 4, SkillType.PASSIVE, List.of(), 4, 0, 0));
        defs.add(new SkillDef("flying", c, 5, SkillType.PASSIVE, List.of(new Req("offense_armour", 0.5f)), 0, 0, 0));
        defs.add(new SkillDef("storm_core", c, 5, SkillType.ACTIVE, List.of(new Req("air_wall", 1.0f)), 0, 60, 50));
        defs.add(new SkillDef("aero_separator", c, 5, SkillType.ACTIVE, List.of(new Req("air_wall", 1.0f)), 0, 80, 60));
        defs.add(new SkillDef("mind_course", c, 5, SkillType.PASSIVE, List.of(), 5, 0, 0));

        // ===== 念力使 TELEKINESIS =====
        c = AbilityCategory.TELEKINESIS;
        defs.add(new SkillDef("psycho_throwing", c, 1, SkillType.ACTIVE, List.of(), 0, 10, 5));
        defs.add(new SkillDef("psycho_transmission", c, 1, SkillType.ACTIVE, List.of(), 0, 5, 5));
        defs.add(new SkillDef("psycho_needling", c, 2, SkillType.ACTIVE, List.of(new Req("psycho_throwing", 0.5f)), 0, 12, 15));
        defs.add(new SkillDef("insulation", c, 2, SkillType.PASSIVE, List.of(new Req("psycho_transmission", 0.0f)), 0, 0, 0));
        defs.add(new SkillDef("cruise_bomb", c, 3, SkillType.ACTIVE, List.of(new Req("psycho_needling", 0.5f)), 0, 25, 20));
        defs.add(new SkillDef("overload_thinking", c, 3, SkillType.ACTIVE, List.of(new Req("insulation", 0.0f)), 0, 0, 0));
        defs.add(new SkillDef("perfect_paper", c, 3, SkillType.PASSIVE, List.of(new Req("insulation", 0.0f)), 0, 0, 0));
        defs.add(new SkillDef("brain_course", c, 3, SkillType.PASSIVE, List.of(), 3, 0, 0));
        defs.add(new SkillDef("psycho_slam", c, 4, SkillType.ACTIVE, List.of(new Req("cruise_bomb", 0.5f)), 0, 50, 40));
        defs.add(new SkillDef("psycho_harden", c, 4, SkillType.PASSIVE, List.of(new Req("perfect_paper", 0.5f)), 0, 0, 0));
        defs.add(new SkillDef("brain_course_advanced", c, 4, SkillType.PASSIVE, List.of(), 4, 0, 0));
        defs.add(new SkillDef("liquid_shadow", c, 5, SkillType.PASSIVE, List.of(new Req("psycho_slam", 0.5f)), 0, 0, 0));
        defs.add(new SkillDef("paper_drill", c, 5, SkillType.ACTIVE, List.of(new Req("perfect_paper", 1.0f)), 0, 60, 50));
        defs.add(new SkillDef("mind_course", c, 5, SkillType.PASSIVE, List.of(), 5, 0, 0));

        return defs;
    }

    /**
     * 有界循环注册：遍历固定的 BUILTIN_SKILLS 表（一次性构建，大小恒定），对每个定义
     * 用 Skill.Builder 还原出与重构前完全一致的 Skill 对象后注册。无递归 / 无
     * while(true) / 无动态扩容，不会无限增值或 OOM。
     */
    private static void registerAllSkills() {
        for (SkillDef d : BUILTIN_SKILLS) {
            Skill.Builder b = new Skill.Builder(d.id, d.category, d.level);
            b.type(d.type);
            for (Req r : d.reqs) {
                b.prereq(r.id, r.ratio);
            }
            if (d.anyLevel > 0) {
                b.anyLevelPrereq(d.anyLevel);
            }
            b.cpCost(d.cpCost);
            b.overload(d.overload);
            registerSkill(b.build());
        }
    }
}
