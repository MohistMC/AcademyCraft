package com.mohistmc.academy.skill.ability.aerohand;

import com.mohistmc.academy.client.effect.EffectHelper;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/**
 * 空气分离器 —— 在目标区域制造真空，将敌人拉向中心并造成窒息伤害
 */
public class AeroSeparatorEffect implements SkillEffect {

    @Override
    public String getId() {
        return "aero_separator";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        float damage = lerpf(20.0f, 35.0f, exp);
        float radius = lerpf(5.0f, 8.0f, exp);
        float pullStrength = lerpf(1.0f, 2.5f, exp);

        ServerLevel level = player.serverLevel();
        Vec3 targetPos = player.getEyePosition().add(player.getLookAngle().scale(8.0));

        for (int i = 0; i < 60; i++) {
            double angle = Math.random() * Math.PI * 2;
            double dist = Math.random() * radius;
            double x = targetPos.x + Math.cos(angle) * dist;
            double z = targetPos.z + Math.sin(angle) * dist;
            EffectHelper.glowBurst(level, x, targetPos.y + Math.random() * 3, z, 1, 0.15f, 0xAAFFFFFF, 10, 0.1);
        }

        level.playSound(null, targetPos.x, targetPos.y, targetPos.z,
                SoundEvents.BREEZE_INHALE, SoundSource.PLAYERS, 1.5f, 0.5f);

        AABB area = new AABB(
                targetPos.x - radius, targetPos.y - 3, targetPos.z - radius,
                targetPos.x + radius, targetPos.y + 3, targetPos.z + radius
        );

        for (Entity e : level.getEntities(player, area, Entity::isAlive)) {
            if (e instanceof LivingEntity living && e != player) {
                Vec3 pull = new Vec3(
                        targetPos.x - e.getX(),
                        targetPos.y - e.getY(),
                        targetPos.z - e.getZ()
                ).normalize().scale(pullStrength);
                living.setDeltaMovement(living.getDeltaMovement().add(pull));
                living.hurtMarked = true;

                living.hurt(player.damageSources().playerAttack(player), damage);
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 2));
                living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 1));
            }
        }

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }
}
