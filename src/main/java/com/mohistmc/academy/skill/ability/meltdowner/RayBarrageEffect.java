package com.mohistmc.academy.skill.ability.meltdowner;

import com.mohistmc.academy.client.effect.EffectHelper;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import com.mohistmc.academy.world.AcademySounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/**
 * 射线弹幕 —— 向准星方向发射大量射线
 */
public class RayBarrageEffect implements SkillEffect {

    private static final double RANGE = 25.0;
    private static final int RAY_COUNT = 5;

    @Override
    public String getId() {
        return "ray_barrage";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        float damage = lerpf(8.0f, 15.0f, exp);
        double range = lerpf(20.0f, 25.0f, exp);
        int rayCount = (int) lerpf(3, RAY_COUNT, exp);

        ServerLevel level = player.serverLevel();
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();

        for (int r = 0; r < rayCount; r++) {
            double offsetX = (level.random.nextDouble() - 0.5) * 0.5;
            double offsetY = (level.random.nextDouble() - 0.5) * 0.5;
            Vec3 rayDir = lookVec.add(offsetX, offsetY, 0).normalize();

            for (double d = 1.0; d <= range; d += 0.5) {
                Vec3 checkPos = eyePos.add(rayDir.scale(d));
                EffectHelper.arcSpark(level, checkPos.x, checkPos.y, checkPos.z, 1, 0.05);

                AABB area = new AABB(
                        checkPos.x - 1, checkPos.y - 1, checkPos.z - 1,
                        checkPos.x + 1, checkPos.y + 1, checkPos.z + 1
                );
                for (Entity e : level.getEntities(player, area, Entity::isAlive)) {
                    if (e instanceof LivingEntity living && e != player) {
                        living.hurt(player.damageSources().playerAttack(player), damage / rayCount);
                    }
                }
            }
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                AcademySounds.MD_BALLSHOOT, SoundSource.PLAYERS, 1.0f, 0.5f);

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }

    @Override
    public int getCooldownTicks(float proficiency) {
        return (int) lerpf(20, 8, proficiency);
    }
}
