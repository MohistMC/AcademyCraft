package com.mohistmc.academy.world;

import com.mohistmc.academy.AcademyCraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AcademySounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, AcademyCraft.MODID);

    // ==================== Electromaster ====================
    public static final Holder<SoundEvent> EM_ARC_WEAK = register("em.arc_weak");
    public static final Holder<SoundEvent> EM_ARC_STRONG = register("em.arc_strong");
    public static final Holder<SoundEvent> EM_MINEDETECT = register("em.minedetect");
    public static final Holder<SoundEvent> EM_RAILGUN = register("em.railgun");
    public static final Holder<SoundEvent> EM_MOVE_LOOP = register("em.move_loop");
    public static final Holder<SoundEvent> EM_CHARGE_LOOP = register("em.charge_loop");
    public static final Holder<SoundEvent> EM_INTENSIFY_ACTIVATE = register("em.intensify_activate");
    public static final Holder<SoundEvent> EM_INTENSIFY_LOOP = register("em.intensify_loop");
    public static final Holder<SoundEvent> EM_LF_LOOP = register("em.lf_loop");
    public static final Holder<SoundEvent> EM_MAG_MANIP = register("em.mag_manip");

    // ==================== Meltdowner ====================
    public static final Holder<SoundEvent> MD_BALLSHOOT = register("md.ballshoot");
    public static final Holder<SoundEvent> MD_RAY_SMALL = register("md.ray_small");
    public static final Holder<SoundEvent> MD_SHIELD_STARTUP = register("md.shield_startup");
    public static final Holder<SoundEvent> MD_SHIELD_LOOP = register("md.shield_loop");
    public static final Holder<SoundEvent> MD_MELTDOWNER = register("md.meltdowner");
    public static final Holder<SoundEvent> MD_MINE_LOOP = register("md.mine_loop");
    public static final Holder<SoundEvent> MD_MINE_BASIC_STARTUP = register("md.mine_basic_startup");
    public static final Holder<SoundEvent> MD_MINE_LUCK_STARTUP = register("md.mine_luck_startup");
    public static final Holder<SoundEvent> MD_MINE_EXPERT_STARTUP = register("md.mine_expert_startup");
    public static final Holder<SoundEvent> MD_SIMPLE_CHARGE = register("md.simple_charge");
    public static final Holder<SoundEvent> MD_MD_CHARGE = register("md.md_charge");

    // ==================== VecManip ====================
    public static final Holder<SoundEvent> VM_BLOOD_RETRO = register("vecmanip.blood_retro");
    public static final Holder<SoundEvent> VM_DIRECTED_SHOCK = register("vecmanip.directed_shock");
    public static final Holder<SoundEvent> VM_GROUNDSHOCK = register("vecmanip.groundshock");
    public static final Holder<SoundEvent> VM_DIRECTED_BLAST = register("vecmanip.directed_blast");
    public static final Holder<SoundEvent> VM_VEC_ACCEL = register("vecmanip.vec_accel");
    public static final Holder<SoundEvent> VM_PLASMA_CANNON = register("vecmanip.plasma_cannon");
    public static final Holder<SoundEvent> VM_PLASMA_CANNON_T = register("vecmanip.plasma_cannon_t");
    public static final Holder<SoundEvent> VM_STORM_WING = register("vecmanip.storm_wing");
    public static final Holder<SoundEvent> VM_VEC_DEVIATION = register("vecmanip.vec_deviation");
    public static final Holder<SoundEvent> VM_VEC_REFLECTION = register("vecmanip.vec_reflection");

    // ==================== Teleporter ====================
    public static final Holder<SoundEvent> TP_TP = register("tp.tp");
    public static final Holder<SoundEvent> TP_TP_PRE = register("tp.tp_pre");
    public static final Holder<SoundEvent> TP_GUTS = register("tp.guts");
    public static final Holder<SoundEvent> TP_TP_SHIFT = register("tp.tp_shift");
    public static final Holder<SoundEvent> TP_TP_FLASHING = register("tp.tp_flashing");

    // ==================== Entity ====================
    public static final Holder<SoundEvent> ENTITY_FLIPCOIN = register("entity.flipcoin");

    // ==================== Ability ====================
    public static final Holder<SoundEvent> ABILITY_DENY = register("ability.deny");
    public static final Holder<SoundEvent> ABILITY_PRESET_CONFIRM = register("ability.preset_confirm");
    public static final Holder<SoundEvent> ABILITY_PRESET_SWITCH = register("ability.preset_switch");

    // ==================== Terminal ====================
    public static final Holder<SoundEvent> TERMINAL_SELECT = register("terminal.select");
    public static final Holder<SoundEvent> TERMINAL_CONFIRM = register("terminal.confirm");

    // ==================== Media ====================
    public static final Holder<SoundEvent> MEDIA_RAILGUN = register("media.only_my_railgun");
    public static final Holder<SoundEvent> MEDIA_JUDGELIGHT = register("media.level5_judgelight");
    public static final Holder<SoundEvent> MEDIA_NOISE = register("media.sisters_noise");

    private static Holder<SoundEvent> register(String name) {
        return SOUND_EVENTS.register(name, SoundEvent::createVariableRangeEvent);
    }

    // ==================== Utility Methods ====================

    /**
     * 在服务端播放音效给单个玩家。
     */
    public static void playSound(Player player, Holder<SoundEvent> sound, float volume, float pitch) {
        player.playSound(sound.value(), volume, pitch);
    }

    /**
     * 在服务端播放音效给指定位置周围的所有玩家。
     */
    public static void playSound(Level level, double x, double y, double z,
                                  Holder<SoundEvent> sound, SoundSource category, float volume, float pitch) {
        level.playSound(null, x, y, z, sound.value(), category, volume, pitch);
    }
}
