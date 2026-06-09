package com.mohistmc.academy.skill.ability.vecmanip;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import net.minecraft.core.particles.ParticleTypes;
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
 * 等离子炮 —— 发射高能等离子束
 */
public class PlasmaCannonEffect implements SkillEffect {

    @Override
    public String getId() {
        return "plasma_cannon";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        float damage = lerpf(30.0f, 50.0f, exp);
        double range = lerpf(30.0f, 45.0f, exp);
        float beamWidth = lerpf(1.5f, 3.0f, exp);

        ServerLevel level = player.serverLevel();
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();

        for (double d = 1.0; d <= range; d += 0.5) {
            Vec3 checkPos = eyePos.add(lookVec.scale(d));
            level.sendParticles(ParticleTypes.DRAGON_BREATH,
                    checkPos.x, checkPos.y, checkPos.z,
                    1, beamWidth / 2, beamWidth / 2, beamWidth / 2, 0.01);

            AABB area = new AABB(
                    checkPos.x - beamWidth, checkPos.y - beamWidth, checkPos.z - beamWidth,
                    checkPos.x + beamWidth, checkPos.y + beamWidth, checkPos.z + beamWidth
            );
            for (Entity e : level.getEntities(player, area, Entity::isAlive)) {
                if (e instanceof LivingEntity living && e != player) {
                    living.hurt(player.damageSources().playerAttack(player), damage);
                    living.setRemainingFireTicks(60);
                }
            }
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENDER_DRAGON_SHOOT, SoundSource.PLAYERS, 1.0f, 0.5f);

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }
}
