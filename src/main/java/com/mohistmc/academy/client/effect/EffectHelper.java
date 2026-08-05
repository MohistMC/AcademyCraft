package com.mohistmc.academy.client.effect;

import com.mohistmc.academy.world.AcademyEntities;
import net.minecraft.world.level.Level;

/**
 * 特效生成辅助工具。
 */
public class EffectHelper {

    // ==================== 通用 ====================

    /** 光环爆发 */
    public static void glowBurst(Level level, double x, double y, double z,
                                  int count, double size, int color, int life, double spread) {
        SpriteEffectEntity.spawnBurst(level, AcademyEntities.SPRITE_EFFECT.get(),
                x, y, z, "glow_circle.png", count, (float) size, color, life, (float) spread, 0);
    }

    /** 烟雾 */
    public static void smokeBurst(Level level, double x, double y, double z,
                                   int count, double spread) {
        SpriteEffectEntity.spawnBurst(level, AcademyEntities.SPRITE_EFFECT.get(),
                x, y, z, "smokes.png", count, 0.3f + level.random.nextFloat() * 0.2f,
                0xAA888888, 15, (float) spread, -0.01f);
    }

    // ==================== Electromaster ====================

    /** 电弧火花爆发 */
    public static void arcSpark(Level level, double x, double y, double z, int count, double spread) {
        SpriteEffectEntity.spawnBurst(level, AcademyEntities.SPRITE_EFFECT.get(),
                x, y, z, "glow_circle.png", count, 0.08f,
                0xCCFFFFAA, 8, (float) spread, 0);
    }

    /** 闪电爆发 (大) */
    public static void lightningBurst(Level level, double x, double y, double z) {
        glowBurst(level, x, y, z, 15, 0.5, 0x88FFFFCC, 12, 0.5);
        glowBurst(level, x, y, z, 5, 0.2, 0xCCFFFFFF, 8, 0.3);
    }

    // ==================== Meltdowner ====================

    /** 熔毁粒子爆发 */
    public static void meltdownBurst(Level level, double x, double y, double z,
                                      int count, double spread) {
        SpriteEffectEntity.spawnBurst(level, AcademyEntities.SPRITE_EFFECT.get(),
                x, y, z, "md_particle.png", count, 0.2f,
                0xCCFF6644, 15, (float) spread, 0.02f);
    }

    /** 射线粒子 */
    public static void raySpark(Level level, double x, double y, double z, int count) {
        SpriteEffectEntity.spawnBurst(level, AcademyEntities.SPRITE_EFFECT.get(),
                x, y, z, "glow_circle.png", count, 0.06f,
                0xCCFF9966, 6, 0.1f, 0);
    }

    // ==================== Teleporter ====================

    /** 传送粒子 */
    public static void teleportBurst(Level level, double x, double y, double z, int count) {
        SpriteEffectEntity.spawnBurst(level, AcademyEntities.SPRITE_EFFECT.get(),
                x, y, z, "tp_particle.png", count, 0.15f,
                0xCCAA66FF, 20, 0.3f, 0.01f);
    }

    // ==================== VecManip ====================

    /** 冲击波环 (使用 WaveEffectEntity 更适合) */
    public static void shockwaveRing(Level level, double x, double y, double z, int rings, double size) {
        WaveEffectEntity wave = new WaveEffectEntity(AcademyEntities.WAVE_EFFECT.get(), level);
        wave.setPos(x, y, z);
        wave.setData(rings, (float) size);
        level.addFreshEntity(wave);
    }

    /** 血液飞溅 */
    public static void bloodSplash(Level level, double x, double y, double z, int count, double spread) {
        for (int i = 0; i < count; i++) {
            int idx = level.random.nextInt(10);
            SpriteEffectEntity e = new SpriteEffectEntity(AcademyEntities.SPRITE_EFFECT.get(), level);
            e.setPos(x, y, z);
            e.setTexture("blood_splash/" + idx + ".png")
                    .setSize(0.3f + level.random.nextFloat() * 0.4f)
                    .setColor(0.9f, 0.1f, 0.1f, 0.9f)
                    .setLife(15 + level.random.nextInt(10))
                    .setMotion(
                            (level.random.nextDouble() - 0.5) * spread,
                            level.random.nextDouble() * spread * 0.3,
                            (level.random.nextDouble() - 0.5) * spread
                    ).setGravity(0.03f).setGlow(false);
            level.addFreshEntity(e);
        }
    }

    // ==================== Aerohand ====================

    /** 气流 */
    public static void windBurst(Level level, double x, double y, double z, int count, double spread) {
        SpriteEffectEntity.spawnBurst(level, AcademyEntities.SPRITE_EFFECT.get(),
                x, y, z, "glow_circle.png", count, 0.1f,
                0x88CCEEFF, 10, (float) spread, -0.005f);
    }

    // ==================== Telekinesis ====================

    /** 念力粒子 */
    public static void psychoBurst(Level level, double x, double y, double z, int count, double spread) {
        SpriteEffectEntity.spawnBurst(level, AcademyEntities.SPRITE_EFFECT.get(),
                x, y, z, "glow_circle.png", count, 0.12f,
                0xCCFF88FF, 12, (float) spread, 0);
    }
}
