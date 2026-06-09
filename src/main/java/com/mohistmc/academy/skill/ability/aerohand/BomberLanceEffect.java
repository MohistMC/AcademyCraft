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
 * 轰炸长矛 —— 向前方发射高速风压长矛
 */
public class BomberLanceEffect implements SkillEffect {

    @Override
    public String getId() {
        return "bomber_lance";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        float damage = lerpf(15.0f, 25.0f, exp);
        double range = lerpf(20.0f, 30.0f, exp);
        float lanceWidth = lerpf(1.0f, 2.0f, exp);

        ServerLevel level = player.serverLevel();
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();

        for (double d = 1.0; d <= range; d += 0.5) {
            Vec3 checkPos = eyePos.add(lookVec.scale(d));
            level.sendParticles(ParticleTypes.SONIC_BOOM,
                    checkPos.x, checkPos.y, checkPos.z,
                    1, lanceWidth / 2, lanceWidth / 2, lanceWidth / 2, 0.01);

            AABB area = new AABB(
                    checkPos.x - lanceWidth, checkPos.y - lanceWidth, checkPos.z - lanceWidth,
                    checkPos.x + lanceWidth, checkPos.y + lanceWidth, checkPos.z + lanceWidth
            );
            for (Entity e : level.getEntities(player, area, Entity::isAlive)) {
                if (e instanceof LivingEntity living && e != player) {
                    living.hurt(player.damageSources().playerAttack(player), damage);
                }
            }
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.0f, 1.0f);

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }
}
