package com.mohistmc.academy.config;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * AcademyCraft 配置系统(基于 NeoForge ModConfigSpec)
 */
@EventBusSubscriber(modid = "academy", bus = EventBusSubscriber.Bus.MOD)
public final class ACConfig {

    private ACConfig() {}

    // ==================== 服务端配置 ====================

    public static final class Server {
        public static final ModConfigSpec SPEC;
        public static final ModConfigSpec.DoubleValue ENERGY_MULTIPLIER;
        public static final ModConfigSpec.DoubleValue SKILL_DAMAGE_MULTIPLIER;
        public static final ModConfigSpec.DoubleValue SKILL_RANGE_MULTIPLIER;
        public static final ModConfigSpec.BooleanValue PVP_ENABLED;

        static {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

            builder.push("energy");
            ENERGY_MULTIPLIER = builder
                    .comment("Global energy production multiplier. 1.0 = default.")
                    .defineInRange("energyMultiplier", 1.0, 0.1, 10.0);
            builder.pop();

            builder.push("skill");
            SKILL_DAMAGE_MULTIPLIER = builder
                    .comment("Global skill damage multiplier. 1.0 = default.")
                    .defineInRange("skillDamageMultiplier", 1.0, 0.1, 10.0);
            SKILL_RANGE_MULTIPLIER = builder
                    .comment("Global skill range multiplier. 1.0 = default.")
                    .defineInRange("skillRangeMultiplier", 1.0, 0.5, 5.0);
            PVP_ENABLED = builder
                    .comment("Whether skills can damage other players.")
                    .define("pvpEnabled", true);
            builder.pop();

            SPEC = builder.build();
        }

        public static double energyMul() { return ENERGY_MULTIPLIER.get(); }
        public static double damageMul() { return SKILL_DAMAGE_MULTIPLIER.get(); }
        public static double rangeMul() { return SKILL_RANGE_MULTIPLIER.get(); }
        public static boolean pvpEnabled() { return PVP_ENABLED.get(); }
    }

    // ==================== 客户端配置 ====================

    public static final class Client {
        public static final ModConfigSpec SPEC;
        public static final ModConfigSpec.BooleanValue SHOW_HUD;
        public static final ModConfigSpec.BooleanValue SHOW_CP_BAR;
        public static final ModConfigSpec.IntValue CP_BAR_X;
        public static final ModConfigSpec.IntValue CP_BAR_Y;
        public static final ModConfigSpec.BooleanValue SHOW_CHARGING_HUD;
        public static final ModConfigSpec.BooleanValue SHOW_KEY_HINTS;
        public static final ModConfigSpec.BooleanValue ENABLE_SKILL_SOUNDS;

        static {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

            builder.push("hud");
            SHOW_HUD = builder
                    .comment("Show main HUD overlay.")
                    .define("showHud", true);
            SHOW_CP_BAR = builder
                    .comment("Show CP bar overlay.")
                    .define("showCpBar", true);
            CP_BAR_X = builder
                    .comment("CP bar X position (from screen center).")
                    .defineInRange("cpBarX", 0, -1000, 1000);
            CP_BAR_Y = builder
                    .comment("CP bar Y position (from screen bottom).")
                    .defineInRange("cpBarY", 30, 0, 500);
            SHOW_CHARGING_HUD = builder
                    .comment("Show charging progress HUD.")
                    .define("showChargingHud", true);
            SHOW_KEY_HINTS = builder
                    .comment("Show key binding hints.")
                    .define("showKeyHints", true);
            builder.pop();

            builder.push("audio");
            ENABLE_SKILL_SOUNDS = builder
                    .comment("Enable skill sound effects.")
                    .define("enableSkillSounds", true);
            builder.pop();

            SPEC = builder.build();
        }

        public static boolean showHud() { return SHOW_HUD.get(); }
        public static boolean showCpBar() { return SHOW_CP_BAR.get(); }
        public static boolean showChargingHud() { return SHOW_CHARGING_HUD.get(); }
        public static boolean showKeyHints() { return SHOW_KEY_HINTS.get(); }
        public static boolean enableSkillSounds() { return ENABLE_SKILL_SOUNDS.get(); }
    }

    // ==================== 事件处理 ====================

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading event) {
    }

    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading event) {
    }
}
