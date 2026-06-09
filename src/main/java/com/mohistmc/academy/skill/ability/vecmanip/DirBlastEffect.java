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
 * 定向爆破 —— 向前方发射高能冲击波
 */
public class DirBlastEffect implements SkillEffect {

    @Override
    public String getId() {
        return "dir_blast";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        float damage = lerpf(12.0f, 25.0f, exp);
        double range = lerpf(10.0f, 18.0f, exp);
        float blastRadius = lerpf(1.5f, 3.0f, exp);

        ServerLevel level = player.serverLevel();
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();

        for (double d = 1.0; d <= range; d += 0.5) {
            Vec3 checkPos = eyePos.add(lookVec.scale(d));
            level.sendParticles(ParticleTypes.EXPLOSION,
                    checkPos.x, checkPos.y, checkPos.z,
                    1, blastRadius / 2, blastRadius / 2, blastRadius / 2, 0.01);

            AABB area = new AABB(
                    checkPos.x - blastRadius, checkPos.y - blastRadius, checkPos.z - blastRadius,
                    checkPos.x + blastRadius, checkPos.y + blastRadius, checkPos.z + blastRadius
            );
            for (Entity e : level.getEntities(player, area, Entity::isAlive)) {
                if (e instanceof LivingEntity living && e != player) {
                    living.hurt(player.damageSources().playerAttack(player), damage);
                    Vec3 knock = lookVec.scale(lerpf(1.0f, 2.0f, exp));
                    living.setDeltaMovement(living.getDeltaMovement().add(knock));
                    living.hurtMarked = true;
                }
            }
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.0f, 0.5f);

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }
}
