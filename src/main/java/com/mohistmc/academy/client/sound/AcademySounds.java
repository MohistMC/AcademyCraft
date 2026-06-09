package com.mohistmc.academy.client.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 自定义音效播放工具类。
 * 替代旧代码的 ACSounds，使用 Minecraft 内置音效系统播放 Academy 自定义音效。
 *
 * @author Mgazul
 */
public class AcademySounds {

    public static final String MODID = "academy";

    // ==================== Electromaster ====================
    public static final SoundEvent EM_ARC_WEAK = get("em.arc_weak");
    public static final SoundEvent EM_ARC_STRONG = get("em.arc_strong");
    public static final SoundEvent EM_MINEDETECT = get("em.minedetect");
    public static final SoundEvent EM_RAILGUN = get("em.railgun");
    public static final SoundEvent EM_MOVE_LOOP = get("em.move_loop");
    public static final SoundEvent EM_CHARGE_LOOP = get("em.charge_loop");
    public static final SoundEvent EM_INTENSIFY_ACTIVATE = get("em.intensify_activate");
    public static final SoundEvent EM_INTENSIFY_LOOP = get("em.intensify_loop");
    public static final SoundEvent EM_LF_LOOP = get("em.lf_loop");
    public static final SoundEvent EM_MAG_MANIP = get("em.mag_manip");

    // ==================== Meltdowner ====================
    public static final SoundEvent MD_BALLSHOOT = get("md.ballshoot");
    public static final SoundEvent MD_RAY_SMALL = get("md.ray_small");
    public static final SoundEvent MD_SHIELD_STARTUP = get("md.shield_startup");
    public static final SoundEvent MD_SHIELD_LOOP = get("md.shield_loop");
    public static final SoundEvent MD_MELTDOWNER = get("md.meltdowner");
    public static final SoundEvent MD_MINE_LOOP = get("md.mine_loop");
    public static final SoundEvent MD_MINE_BASIC_STARTUP = get("md.mine_basic_startup");
    public static final SoundEvent MD_MINE_LUCK_STARTUP = get("md.mine_luck_startup");
    public static final SoundEvent MD_MINE_EXPERT_STARTUP = get("md.mine_expert_startup");
    public static final SoundEvent MD_SIMPLE_CHARGE = get("md.simple_charge");
    public static final SoundEvent MD_MD_CHARGE = get("md.md_charge");

    // ==================== VecManip ====================
    public static final SoundEvent VM_BLOOD_RETRO = get("vecmanip.blood_retro");
    public static final SoundEvent VM_DIRECTED_SHOCK = get("vecmanip.directed_shock");
    public static final SoundEvent VM_GROUNDSHOCK = get("vecmanip.groundshock");
    public static final SoundEvent VM_DIRECTED_BLAST = get("vecmanip.directed_blast");
    public static final SoundEvent VM_VEC_ACCEL = get("vecmanip.vec_accel");
    public static final SoundEvent VM_PLASMA_CANNON = get("vecmanip.plasma_cannon");
    public static final SoundEvent VM_PLASMA_CANNON_T = get("vecmanip.plasma_cannon_t");
    public static final SoundEvent VM_STORM_WING = get("vecmanip.storm_wing");
    public static final SoundEvent VM_VEC_DEVIATION = get("vecmanip.vec_deviation");
    public static final SoundEvent VM_VEC_REFLECTION = get("vecmanip.vec_reflection");

    // ==================== Teleporter ====================
    public static final SoundEvent TP_TP = get("tp.tp");
    public static final SoundEvent TP_TP_PRE = get("tp.tp_pre");
    public static final SoundEvent TP_GUTS = get("tp.guts");
    public static final SoundEvent TP_TP_SHIFT = get("tp.tp_shift");
    public static final SoundEvent TP_TP_FLASHING = get("tp.tp_flashing");

    // ==================== Entity ====================
    public static final SoundEvent ENTITY_FLIPCOIN = get("entity.flipcoin");

    // ==================== Ability ====================
    public static final SoundEvent ABILITY_DENY = get("ability.deny");
    public static final SoundEvent ABILITY_PRESET_CONFIRM = get("ability.preset_confirm");
    public static final SoundEvent ABILITY_PRESET_SWITCH = get("ability.preset_switch");

    // ==================== Terminal ====================
    public static final SoundEvent TERMINAL_SELECT = get("terminal.select");
    public static final SoundEvent TERMINAL_CONFIRM = get("terminal.confirm");

    private static SoundEvent get(String name) {
        return SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, name));
    }

    /**
     * 在服务端播放音效给单个玩家。
     */
    public static void playSound(Player player, SoundEvent sound, float volume, float pitch) {
        player.playSound(sound, volume, pitch);
    }

    /**
     * 在服务端播放音效给指定位置周围的所有玩家。
     */
    public static void playSound(Level level, double x, double y, double z,
                                  SoundEvent sound, SoundSource category, float volume, float pitch) {
        level.playSound(null, x, y, z, sound, category, volume, pitch);
    }

    /**
     * 客户端播放UI音效（无位置）。
     */
    public static void playClient(SoundEvent sound, SoundSource category, float volume, float pitch) {
        Minecraft.getInstance().getSoundManager().play(
                new SimpleSoundInstance(sound.getLocation(), category, volume, pitch,
                        RandomSource.create(), false, 1,
                        SoundInstance.Attenuation.NONE, 0, 0, 0, true));
    }

}
