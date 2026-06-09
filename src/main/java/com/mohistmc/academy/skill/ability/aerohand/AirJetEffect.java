package com.mohistmc.academy.skill.ability.aerohand;

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
 * 空气喷射 —— 强力空气冲击，击退敌人
 */
public class AirJetEffect implements SkillEffect {

    @Override
    public String getId() {
        return "air_jet";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        float damage = lerpf(5.0f, 10.0f, exp);
        double range = lerpf(10.0f, 18.0f, exp);
        float knockback = lerpf(1.5f, 3.0f, exp);
        float radius = lerpf(1.5f, 2.5f, exp);

        ServerLevel level = player.serverLevel();
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();

        for (double d = 1.0; d <= range; d += 0.5) {
            Vec3 checkPos = eyePos.add(lookVec.scale(d));
            EffectHelper.windBurst(level, checkPos.x, checkPos.y, checkPos.z, 1, 0.1);

            AABB area = new AABB(
                    checkPos.x - radius, checkPos.y - radius, checkPos.z - radius,
                    checkPos.x + radius, checkPos.y + radius, checkPos.z + radius
            );
            for (Entity e : level.getEntities(player, area, Entity::isAlive)) {
                if (e instanceof LivingEntity living && e != player) {
                    living.hurt(player.damageSources().playerAttack(player), damage);
                    Vec3 knock = lookVec.scale(knockback);
                    living.setDeltaMovement(living.getDeltaMovement().add(knock));
                    living.hurtMarked = true;
                }
            }
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0f, 1.0f);

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }
}
