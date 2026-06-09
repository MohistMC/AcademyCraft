package com.mohistmc.academy.skill.ability.telekinesis;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import com.mohistmc.academy.client.effect.EffectHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/**
 * 巡航炸弹 —— 用念力发射追踪目标的爆炸投射物
 */
public class CruiseBombEffect implements SkillEffect {

    @Override
    public String getId() {
        return "cruise_bomb";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        float damage = lerpf(12.0f, 22.0f, exp);
        double range = lerpf(12.0f, 20.0f, exp);
        float radius = lerpf(2.5f, 4.0f, exp);

        ServerLevel level = player.serverLevel();
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 impactPos = eyePos.add(lookVec.scale(range));

        // 飞行粒子
        for (double d = 1.0; d <= range; d += 0.5) {
            Vec3 pos = eyePos.add(lookVec.scale(d));
            EffectHelper.glowBurst(level, pos.x, pos.y, pos.z, 1, 0.2f, 0xAAFFCC88, 10, 0.1);
        }

        // 爆炸粒子
        EffectHelper.glowBurst(level, impactPos.x, impactPos.y, impactPos.z, (int) (radius * 3), 0.3f, 0x88FFCC44, 12, radius / 2);

        level.playSound(null, impactPos.x, impactPos.y, impactPos.z,
                SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.0f, 0.8f);

        AABB area = new AABB(
                impactPos.x - radius, impactPos.y - radius, impactPos.z - radius,
                impactPos.x + radius, impactPos.y + radius, impactPos.z + radius
        );
        for (Entity e : level.getEntities(player, area, Entity::isAlive)) {
            if (e instanceof LivingEntity living) {
                float distancedDamage = damage * (float) (1.0 - Math.min(e.distanceToSqr(impactPos.x, impactPos.y, impactPos.z) / (radius * radius), 1.0));
                living.hurt(player.damageSources().playerAttack(player), Math.max(distancedDamage, 1));
                Vec3 knock = new Vec3(
                        e.getX() - impactPos.x,
                        0.5,
                        e.getZ() - impactPos.z
                ).normalize().scale(lerpf(1.0f, 2.0f, exp));
                living.setDeltaMovement(living.getDeltaMovement().add(knock));
                living.hurtMarked = true;
            }
        }

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }
}
