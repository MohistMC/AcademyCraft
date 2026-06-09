package com.mohistmc.academy.skill.ability.aerohand;

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
 * 空气刃 —— 向前方发射空气刀刃
 */
public class AirBladeEffect implements SkillEffect {

    @Override
    public String getId() {
        return "air_blade";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        float damage = lerpf(10.0f, 18.0f, exp);
        double range = lerpf(12.0f, 20.0f, exp);
        float bladeWidth = lerpf(1.0f, 2.0f, exp);

        ServerLevel level = player.serverLevel();
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();

        for (double d = 1.0; d <= range; d += 0.5) {
            Vec3 checkPos = eyePos.add(lookVec.scale(d));
            level.sendParticles(ParticleTypes.SWEEP_ATTACK,
                    checkPos.x, checkPos.y, checkPos.z,
                    1, bladeWidth / 2, bladeWidth / 2, bladeWidth / 2, 0.01);

            AABB area = new AABB(
                    checkPos.x - bladeWidth, checkPos.y - bladeWidth, checkPos.z - bladeWidth,
                    checkPos.x + bladeWidth, checkPos.y + bladeWidth, checkPos.z + bladeWidth
            );
            for (Entity e : level.getEntities(player, area, Entity::isAlive)) {
                if (e instanceof LivingEntity living && e != player) {
                    living.hurt(player.damageSources().playerAttack(player), damage);
                }
            }
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0f, 1.5f);

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }
}
