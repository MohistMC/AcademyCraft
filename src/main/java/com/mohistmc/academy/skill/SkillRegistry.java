package com.mohistmc.academy.skill;

import com.mohistmc.academy.skill.effect.ArcGenEffect;
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

        registerSkill(new Skill.Builder("arc_gen", cat, 1)
                .cpCost(10).overload(5).build());

        registerSkill(new Skill.Builder("charging", cat, 1)
                .prereq("arc_gen", 0.3f)
                .cpCost(5).overload(10).build());

        registerSkill(new Skill.Builder("mag_movement", cat, 2)
                .prereq("arc_gen", 1.0f)
                .cpCost(8).overload(15).build());

        registerSkill(new Skill.Builder("mag_manip", cat, 2)
                .prereq("mag_movement", 0.5f)
                .cpCost(15).overload(20).build());

        registerSkill(new Skill.Builder("body_intensify", cat, 3)
                .prereq("arc_gen", 1.0f).prereq("charging", 1.0f)
                .cpCost(30).overload(40).build());

        registerSkill(new Skill.Builder("mine_detect", cat, 3)
                .prereq("mag_manip", 1.0f)
                .cpCost(20).overload(10).build());

        registerSkill(new Skill.Builder("brain_course", cat, 3)
                .type(SkillType.PASSIVE)
                .anyLevelPrereq(3).build());

        registerSkill(new Skill.Builder("thunder_bolt", cat, 4)
                .prereq("charging", 0.7f)
                .cpCost(40).overload(30).build());

        registerSkill(new Skill.Builder("railgun", cat, 4)
                .prereq("thunder_bolt", 0.3f).prereq("mag_manip", 1.0f)
                .cpCost(80).overload(60).build());

        registerSkill(new Skill.Builder("brain_course_advanced", cat, 4)
                .type(SkillType.PASSIVE)
                .anyLevelPrereq(4).build());

        registerSkill(new Skill.Builder("thunder_clap", cat, 5)
                .prereq("thunder_bolt", 1.0f)
                .cpCost(100).overload(80).build());

        registerSkill(new Skill.Builder("mind_course", cat, 5)
                .type(SkillType.PASSIVE)
                .anyLevelPrereq(5).build());
    }

    private static void registerMeltdownerSkills() {
        AbilityCategory cat = AbilityCategory.MELTDOWNER;

        registerSkill(new Skill.Builder("electron_bomb", cat, 1)
                .cpCost(5).overload(2).build());

        registerSkill(new Skill.Builder("rad_intensify", cat, 1)
                .type(SkillType.PASSIVE)
                .prereq("electron_bomb", 0.0f).build());

        registerSkill(new Skill.Builder("scatter_bomb", cat, 2)
                .prereq("electron_bomb", 0.8f)
                .cpCost(25).overload(50).build());

        registerSkill(new Skill.Builder("light_shield", cat, 2)
                .prereq("electron_bomb", 0.5f)
                .cpCost(40).overload(30).build());

        registerSkill(new Skill.Builder("meltdowner", cat, 3)
                .prereq("light_shield", 0.8f).prereq("scatter_bomb", 0.8f)
                .cpCost(60).overload(50).build());

        registerSkill(new Skill.Builder("mine_ray_basic", cat, 3)
                .prereq("scatter_bomb", 0.5f)
                .cpCost(10).overload(5).build());

        registerSkill(new Skill.Builder("brain_course", cat, 3)
                .type(SkillType.PASSIVE)
                .anyLevelPrereq(3).build());

        registerSkill(new Skill.Builder("mine_ray_expert", cat, 4)
                .prereq("mine_ray_basic", 0.5f)
                .cpCost(12).overload(5).build());

        registerSkill(new Skill.Builder("ray_barrage", cat, 4)
                .prereq("meltdowner", 0.3f)
                .cpCost(50).overload(40).build());

        registerSkill(new Skill.Builder("jet_engine", cat, 4)
                .prereq("meltdowner", 1.0f)
                .cpCost(35).overload(25).build());

        registerSkill(new Skill.Builder("brain_course_advanced", cat, 4)
                .type(SkillType.PASSIVE)
                .anyLevelPrereq(4).build());

        registerSkill(new Skill.Builder("mine_ray_luck", cat, 5)
                .prereq("mine_ray_expert", 0.5f)
                .cpCost(15).overload(5).build());

        registerSkill(new Skill.Builder("electron_missile", cat, 5)
                .prereq("ray_barrage", 0.5f)
                .cpCost(70).overload(60).build());

        registerSkill(new Skill.Builder("mind_course", cat, 5)
                .type(SkillType.PASSIVE)
                .anyLevelPrereq(5).build());
    }

    private static void registerTeleporterSkills() {
        AbilityCategory cat = AbilityCategory.TELEPORTER;

        registerSkill(new Skill.Builder("threatening_teleport", cat, 1)
                .cpCost(15).overload(10).build());

        registerSkill(new Skill.Builder("dim_folding_theorem", cat, 1)
                .type(SkillType.PASSIVE)
                .prereq("threatening_teleport", 0.0f).build());

        registerSkill(new Skill.Builder("penetrate_teleport", cat, 2)
                .prereq("threatening_teleport", 0.3f)
                .cpCost(20).overload(15).build());

        registerSkill(new Skill.Builder("mark_teleport", cat, 2)
                .prereq("threatening_teleport", 0.5f)
                .cpCost(25).overload(15).build());

        registerSkill(new Skill.Builder("brain_course", cat, 3)
                .type(SkillType.PASSIVE)
                .anyLevelPrereq(3).build());

        registerSkill(new Skill.Builder("location_teleport", cat, 3)
                .prereq("mark_teleport", 0.8f).prereq("penetrate_teleport", 0.8f)
                .cpCost(80).overload(40).build());

        registerSkill(new Skill.Builder("dim_folding_theorem_2", cat, 3)
                .type(SkillType.PASSIVE)
                .prereq("location_teleport", 0.0f).build());

        registerSkill(new Skill.Builder("flesh_ripping", cat, 4)
                .prereq("location_teleport", 0.5f)
                .cpCost(50).overload(35).build());

        registerSkill(new Skill.Builder("brain_course_advanced", cat, 4)
                .type(SkillType.PASSIVE)
                .anyLevelPrereq(4).build());

        registerSkill(new Skill.Builder("flashing", cat, 5)
                .prereq("flesh_ripping", 0.5f)
                .cpCost(60).overload(30).build());

        registerSkill(new Skill.Builder("shift_tp", cat, 5)
                .prereq("location_teleport", 0.8f)
                .cpCost(30).overload(20).build());

        registerSkill(new Skill.Builder("space_fluct", cat, 5)
                .type(SkillType.PASSIVE)
                .prereq("flesh_ripping", 0.5f).build());

        registerSkill(new Skill.Builder("mind_course", cat, 5)
                .type(SkillType.PASSIVE)
                .anyLevelPrereq(5).build());
    }

    private static void registerVecmanipSkills() {
        AbilityCategory cat = AbilityCategory.VECMANIP;

        registerSkill(new Skill.Builder("dir_shock", cat, 1)
                .cpCost(8).overload(5).build());

        registerSkill(new Skill.Builder("ground_shock", cat, 1)
                .cpCost(12).overload(10).build());

        registerSkill(new Skill.Builder("vec_accel", cat, 2)
                .prereq("ground_shock", 0.5f)
                .cpCost(15).overload(10).build());

        registerSkill(new Skill.Builder("vec_deviation", cat, 2)
                .prereq("dir_shock", 0.5f)
                .type(SkillType.PASSIVE).build());

        registerSkill(new Skill.Builder("dir_blast", cat, 3)
                .prereq("dir_shock", 1.0f).prereq("vec_accel", 0.5f)
                .cpCost(30).overload(25).build());

        registerSkill(new Skill.Builder("storm_wing", cat, 3)
                .prereq("vec_accel", 0.8f)
                .cpCost(25).overload(20).build());

        registerSkill(new Skill.Builder("brain_course", cat, 3)
                .type(SkillType.PASSIVE)
                .anyLevelPrereq(3).build());

        registerSkill(new Skill.Builder("blood_retro", cat, 4)
                .prereq("dir_blast", 0.5f)
                .cpCost(60).overload(40).build());

        registerSkill(new Skill.Builder("vec_reflection", cat, 4)
                .prereq("vec_deviation", 1.0f)
                .cpCost(40).overload(30).build());

        registerSkill(new Skill.Builder("brain_course_advanced", cat, 4)
                .type(SkillType.PASSIVE)
                .anyLevelPrereq(4).build());

        registerSkill(new Skill.Builder("plasma_cannon", cat, 5)
                .prereq("blood_retro", 0.5f).prereq("storm_wing", 0.8f)
                .cpCost(120).overload(100).build());

        registerSkill(new Skill.Builder("mind_course", cat, 5)
                .type(SkillType.PASSIVE)
                .anyLevelPrereq(5).build());
    }
}
