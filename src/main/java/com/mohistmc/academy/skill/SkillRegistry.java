package com.mohistmc.academy.skill;

import com.mohistmc.academy.skill.ability.ArcGenEffect;
import com.mohistmc.academy.skill.ability.electromaster.BodyIntensifyEffect;
import com.mohistmc.academy.skill.ability.electromaster.ChargingEffect;
import com.mohistmc.academy.skill.ability.MagManipEffect;
import com.mohistmc.academy.skill.ability.electromaster.MagMovementEffect;
import com.mohistmc.academy.skill.ability.electromaster.MineDetectEffect;
import com.mohistmc.academy.skill.ability.electromaster.RailgunEffect;
import com.mohistmc.academy.skill.ability.electromaster.ThunderBoltEffect;
import com.mohistmc.academy.skill.ability.electromaster.ThunderClapEffect;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Mgazul
 * @date 2026/5/30 20:27
 */
public class SkillRegistry {

    private static final Map<String, Skill> SKILLS = new HashMap<>();
    private static final Map<String, SkillEffect> EFFECTS = new HashMap<>();
    private static final Map<AbilityCategory, List<Skill>> SKILLS_BY_CATEGORY = new HashMap<>();
    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;
        initialized = true;

        registerBuiltinEffects();
        registerElectromasterSkills();
        registerMeltdownerSkills();
        registerTeleporterSkills();
        registerVecmanipSkills();
        registerAerohandSkills();
        registerTelekinesisSkills();

        bindEffects();
    }

    public static void registerSkill(Skill skill) {
        SKILLS.put(skill.getId(), skill);
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

    private static void registerElectromasterSkills() {
        AbilityCategory cat = AbilityCategory.ELECTROMASTER;

        // 电弧激发
        registerSkill(new Skill.Builder("arc_gen", cat, 1)
                .cpCost(10).overload(5).build());

        // 电流回冲
        registerSkill(new Skill.Builder("charging", cat, 1)
                .prereq("arc_gen", 0.3f)
                .cpCost(5).overload(10).build());

        // 电磁牵引
        registerSkill(new Skill.Builder("mag_movement", cat, 2)
                .prereq("arc_gen", 1.0f)
                .cpCost(8).overload(15).build());

        // 磁场控制
        registerSkill(new Skill.Builder("mag_manip", cat, 2)
                .prereq("mag_movement", 0.5f)
                .cpCost(15).overload(20).build());

        // 生物电强化
        registerSkill(new Skill.Builder("body_intensify", cat, 3)
                .prereq("arc_gen", 1.0f).prereq("charging", 1.0f)
                .cpCost(30).overload(40).build());

        // 矿物探测
        registerSkill(new Skill.Builder("mine_detect", cat, 3)
                .prereq("mag_manip", 1.0f)
                .cpCost(20).overload(10).build());

        // 大脑训练课程(被动)
        registerSkill(new Skill.Builder("brain_course", cat, 3)
                .type(SkillType.PASSIVE)
                .anyLevelPrereq(3).build());

        // 雷击之枪
        registerSkill(new Skill.Builder("thunder_bolt", cat, 4)
                .prereq("charging", 0.7f)
                .cpCost(40).overload(30).build());

        // 超电磁炮
        registerSkill(new Skill.Builder("railgun", cat, 4)
                .prereq("thunder_bolt", 0.3f).prereq("mag_manip", 1.0f)
                .cpCost(80).overload(60).build());

        // 大脑训练课程(高级)(被动)
        registerSkill(new Skill.Builder("brain_course_advanced", cat, 4)
                .type(SkillType.PASSIVE)
                .anyLevelPrereq(4).build());

        // 终极落雷
        registerSkill(new Skill.Builder("thunder_clap", cat, 5)
                .prereq("thunder_bolt", 1.0f)
                .cpCost(100).overload(80).build());

        // 思维修养课程(被动)
        registerSkill(new Skill.Builder("mind_course", cat, 5)
                .type(SkillType.PASSIVE)
                .anyLevelPrereq(5).build());
    }

    private static void registerMeltdownerSkills() {
        AbilityCategory cat = AbilityCategory.MELTDOWNER;

        // 电子弹
        registerSkill(new Skill.Builder("electron_bomb", cat, 1)
                .cpCost(5).overload(2).build());

        // 辐射强化(被动)
        registerSkill(new Skill.Builder("rad_intensify", cat, 1)
                .type(SkillType.PASSIVE)
                .prereq("electron_bomb", 0.0f).build());

        // 散射弹
        registerSkill(new Skill.Builder("scatter_bomb", cat, 2)
                .prereq("electron_bomb", 0.8f)
                .cpCost(25).overload(50).build());

        // 光盾
        registerSkill(new Skill.Builder("light_shield", cat, 2)
                .prereq("electron_bomb", 0.5f)
                .cpCost(40).overload(30).build());

        // 原子崩坏
        registerSkill(new Skill.Builder("meltdowner", cat, 3)
                .prereq("light_shield", 0.8f).prereq("scatter_bomb", 0.8f)
                .cpCost(60).overload(50).build());

        // 矿物射线(基础)
        registerSkill(new Skill.Builder("mine_ray_basic", cat, 3)
                .prereq("scatter_bomb", 0.5f)
                .cpCost(10).overload(5).build());

        // 大脑训练课程(被动)
        registerSkill(new Skill.Builder("brain_course", cat, 3)
                .type(SkillType.PASSIVE)
                .anyLevelPrereq(3).build());

        // 矿物射线(专家)
        registerSkill(new Skill.Builder("mine_ray_expert", cat, 4)
                .prereq("mine_ray_basic", 0.5f)
                .cpCost(12).overload(5).build());

        // 射线弹幕
        registerSkill(new Skill.Builder("ray_barrage", cat, 4)
                .prereq("meltdowner", 0.3f)
                .cpCost(50).overload(40).build());

        // 喷射引擎
        registerSkill(new Skill.Builder("jet_engine", cat, 4)
                .prereq("meltdowner", 1.0f)
                .cpCost(35).overload(25).build());

        // 大脑训练课程(高级)(被动)
        registerSkill(new Skill.Builder("brain_course_advanced", cat, 4)
                .type(SkillType.PASSIVE)
                .anyLevelPrereq(4).build());

        // 矿物射线(幸运)
        registerSkill(new Skill.Builder("mine_ray_luck", cat, 5)
                .prereq("mine_ray_expert", 0.5f)
                .cpCost(15).overload(5).build());

        // 电子导弹
        registerSkill(new Skill.Builder("electron_missile", cat, 5)
                .prereq("ray_barrage", 0.5f)
                .cpCost(70).overload(60).build());

        // 思维修养课程(被动)
        registerSkill(new Skill.Builder("mind_course", cat, 5)
                .type(SkillType.PASSIVE)
                .anyLevelPrereq(5).build());
    }

    private static void registerTeleporterSkills() {
        AbilityCategory cat = AbilityCategory.TELEPORTER;

        // 威胁传送
        registerSkill(new Skill.Builder("threatening_teleport", cat, 1)
                .cpCost(15).overload(10).build());

        // 维度折叠定理(被动)
        registerSkill(new Skill.Builder("dim_folding_theorem", cat, 1)
                .type(SkillType.PASSIVE)
                .prereq("threatening_teleport", 0.0f).build());

        // 穿透传送
        registerSkill(new Skill.Builder("penetrate_teleport", cat, 2)
                .prereq("threatening_teleport", 0.3f)
                .cpCost(20).overload(15).build());

        // 标记传送
        registerSkill(new Skill.Builder("mark_teleport", cat, 2)
                .prereq("threatening_teleport", 0.5f)
                .cpCost(25).overload(15).build());

        // 大脑训练课程(被动)
        registerSkill(new Skill.Builder("brain_course", cat, 3)
                .type(SkillType.PASSIVE)
                .anyLevelPrereq(3).build());

        // 位置传送
        registerSkill(new Skill.Builder("location_teleport", cat, 3)
                .prereq("mark_teleport", 0.8f).prereq("penetrate_teleport", 0.8f)
                .cpCost(80).overload(40).build());

        // 维度折叠定理II(被动)
        registerSkill(new Skill.Builder("dim_folding_theorem_2", cat, 3)
                .type(SkillType.PASSIVE)
                .prereq("location_teleport", 0.0f).build());

        // 撕裂肉体
        registerSkill(new Skill.Builder("flesh_ripping", cat, 4)
                .prereq("location_teleport", 0.5f)
                .cpCost(50).overload(35).build());

        // 大脑训练课程(高级)(被动)
        registerSkill(new Skill.Builder("brain_course_advanced", cat, 4)
                .type(SkillType.PASSIVE)
                .anyLevelPrereq(4).build());

        // 闪烁
        registerSkill(new Skill.Builder("flashing", cat, 5)
                .prereq("flesh_ripping", 0.5f)
                .cpCost(60).overload(30).build());

        // 位移传送
        registerSkill(new Skill.Builder("shift_tp", cat, 5)
                .prereq("location_teleport", 0.8f)
                .cpCost(30).overload(20).build());

        // 空间波动(被动)
        registerSkill(new Skill.Builder("space_fluct", cat, 5)
                .type(SkillType.PASSIVE)
                .prereq("flesh_ripping", 0.5f).build());

        // 思维修养课程(被动)
        registerSkill(new Skill.Builder("mind_course", cat, 5)
                .type(SkillType.PASSIVE)
                .anyLevelPrereq(5).build());
    }

    private static void registerVecmanipSkills() {
        AbilityCategory cat = AbilityCategory.VECMANIP;

        // 定向冲击
        registerSkill(new Skill.Builder("dir_shock", cat, 1)
                .cpCost(8).overload(5).build());

        // 地面冲击
        registerSkill(new Skill.Builder("ground_shock", cat, 1)
                .cpCost(12).overload(10).build());

        // 矢量加速
        registerSkill(new Skill.Builder("vec_accel", cat, 2)
                .prereq("ground_shock", 0.5f)
                .cpCost(15).overload(10).build());

        // 矢量偏转(被动)
        registerSkill(new Skill.Builder("vec_deviation", cat, 2)
                .prereq("dir_shock", 0.5f)
                .type(SkillType.PASSIVE).build());

        // 定向爆破
        registerSkill(new Skill.Builder("dir_blast", cat, 3)
                .prereq("dir_shock", 1.0f).prereq("vec_accel", 0.5f)
                .cpCost(30).overload(25).build());

        // 风暴之翼
        registerSkill(new Skill.Builder("storm_wing", cat, 3)
                .prereq("vec_accel", 0.8f)
                .cpCost(25).overload(20).build());

        // 大脑训练课程(被动)
        registerSkill(new Skill.Builder("brain_course", cat, 3)
                .type(SkillType.PASSIVE)
                .anyLevelPrereq(3).build());

        // 血液回流
        registerSkill(new Skill.Builder("blood_retro", cat, 4)
                .prereq("dir_blast", 0.5f)
                .cpCost(60).overload(40).build());

        // 矢量反射
        registerSkill(new Skill.Builder("vec_reflection", cat, 4)
                .prereq("vec_deviation", 1.0f)
                .cpCost(40).overload(30).build());

        // 大脑训练课程(高级)(被动)
        registerSkill(new Skill.Builder("brain_course_advanced", cat, 4)
                .type(SkillType.PASSIVE)
                .anyLevelPrereq(4).build());

        // 等离子炮
        registerSkill(new Skill.Builder("plasma_cannon", cat, 5)
                .prereq("blood_retro", 0.5f).prereq("storm_wing", 0.8f)
                .cpCost(120).overload(100).build());

        // 思维修养课程(被动)
        registerSkill(new Skill.Builder("mind_course", cat, 5)
                .type(SkillType.PASSIVE)
                .anyLevelPrereq(5).build());
    }

    private static void registerAerohandSkills() {
        AbilityCategory cat = AbilityCategory.AEROHAND;

        // 火山球
        registerSkill(new Skill.Builder("volcanic_ball", cat, 1)
                .cpCost(10).overload(5).build());

        // 上升气流(被动)
        registerSkill(new Skill.Builder("ascending_air", cat, 1)
                .type(SkillType.PASSIVE)
                .build());

        // 空气刃
        registerSkill(new Skill.Builder("air_blade", cat, 2)
                .prereq("volcanic_ball", 0.5f)
                .cpCost(12).overload(15).build());

        // 气流(被动)
        registerSkill(new Skill.Builder("airflow", cat, 2)
                .type(SkillType.PASSIVE)
                .prereq("ascending_air", 0.5f).build());

        // 空气冷却
        registerSkill(new Skill.Builder("air_cooling", cat, 3)
                .prereq("ascending_air", 0.0f)
                .cpCost(20).overload(20).build());

        // 空气墙
        registerSkill(new Skill.Builder("air_wall", cat, 3)
                .prereq("air_blade", 0.5f)
                .cpCost(30).overload(25).build());

        // 空气喷射
        registerSkill(new Skill.Builder("air_jet", cat, 3)
                .prereq("airflow", 0.1f)
                .cpCost(15).overload(10).build());

        // 大脑训练课程(被动)
        registerSkill(new Skill.Builder("brain_course", cat, 3)
                .type(SkillType.PASSIVE)
                .anyLevelPrereq(3).build());

        // 攻击装甲(被动)
        registerSkill(new Skill.Builder("offense_armour", cat, 4)
                .type(SkillType.PASSIVE)
                .prereq("air_wall", 1.0f).build());

        // 轰炸长矛
        registerSkill(new Skill.Builder("bomber_lance", cat, 4)
                .prereq("air_wall", 0.5f)
                .cpCost(50).overload(40).build());

        // 大脑训练课程(高级)(被动)
        registerSkill(new Skill.Builder("brain_course_advanced", cat, 4)
                .type(SkillType.PASSIVE)
                .anyLevelPrereq(4).build());

        // 飞行(被动)
        registerSkill(new Skill.Builder("flying", cat, 5)
                .type(SkillType.PASSIVE)
                .prereq("offense_armour", 0.5f).build());

        // 风暴核心
        registerSkill(new Skill.Builder("storm_core", cat, 5)
                .prereq("air_wall", 1.0f)
                .cpCost(60).overload(50).build());

        // 空气分离器
        registerSkill(new Skill.Builder("aero_separator", cat, 5)
                .prereq("air_wall", 1.0f)
                .cpCost(80).overload(60).build());

        // 思维修养课程(被动)
        registerSkill(new Skill.Builder("mind_course", cat, 5)
                .type(SkillType.PASSIVE)
                .anyLevelPrereq(5).build());
    }

    private static void registerTelekinesisSkills() {
        AbilityCategory cat = AbilityCategory.TELEKINESIS;

        // 念力投掷
        registerSkill(new Skill.Builder("psycho_throwing", cat, 1)
                .cpCost(10).overload(5).build());

        // 念力传输
        registerSkill(new Skill.Builder("psycho_transmission", cat, 1)
                .cpCost(5).overload(5).build());

        // 念力针
        registerSkill(new Skill.Builder("psycho_needling", cat, 2)
                .prereq("psycho_throwing", 0.5f)
                .cpCost(12).overload(15).build());

        // 绝缘(被动)
        registerSkill(new Skill.Builder("insulation", cat, 2)
                .type(SkillType.PASSIVE)
                .prereq("psycho_transmission", 0.0f).build());

        // 巡航炸弹
        registerSkill(new Skill.Builder("cruise_bomb", cat, 3)
                .prereq("psycho_needling", 0.5f)
                .cpCost(25).overload(20).build());

        // 过载思维
        registerSkill(new Skill.Builder("overload_thinking", cat, 3)
                .prereq("insulation", 0.0f)
                .cpCost(0).overload(0).build());

        // 完美纸张(被动)
        registerSkill(new Skill.Builder("perfect_paper", cat, 3)
                .type(SkillType.PASSIVE)
                .prereq("insulation", 0.0f).build());

        // 大脑训练课程(被动)
        registerSkill(new Skill.Builder("brain_course", cat, 3)
                .type(SkillType.PASSIVE)
                .anyLevelPrereq(3).build());

        // 念力猛击
        registerSkill(new Skill.Builder("psycho_slam", cat, 4)
                .prereq("cruise_bomb", 0.5f)
                .cpCost(50).overload(40).build());

        // 念力硬化(被动)
        registerSkill(new Skill.Builder("psycho_harden", cat, 4)
                .type(SkillType.PASSIVE)
                .prereq("perfect_paper", 0.5f).build());

        // 大脑训练课程(高级)(被动)
        registerSkill(new Skill.Builder("brain_course_advanced", cat, 4)
                .type(SkillType.PASSIVE)
                .anyLevelPrereq(4).build());

        // 液态阴影(被动)
        registerSkill(new Skill.Builder("liquid_shadow", cat, 5)
                .type(SkillType.PASSIVE)
                .prereq("psycho_slam", 0.5f).build());

        // 纸张钻头
        registerSkill(new Skill.Builder("paper_drill", cat, 5)
                .prereq("perfect_paper", 1.0f)
                .cpCost(60).overload(50).build());

        // 思维修养课程(被动)
        registerSkill(new Skill.Builder("mind_course", cat, 5)
                .type(SkillType.PASSIVE)
                .anyLevelPrereq(5).build());
    }

}
