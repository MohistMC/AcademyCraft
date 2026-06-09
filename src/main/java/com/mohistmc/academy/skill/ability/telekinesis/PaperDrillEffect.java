package com.mohistmc.academy.skill.ability.telekinesis;

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
 * 纸张钻头 —— 用念力驱动纸张高速旋转形成钻头，造成穿透伤害
 */
public class PaperDrillEffect implements SkillEffect {

    @Override
    public String getId() {
        return "paper_drill";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        float damage = lerpf(18.0f, 30.0f, exp);
        double range = lerpf(15.0f, 25.0f, exp);
        float drillWidth = lerpf(1.0f, 2.0f, exp);

        ServerLevel level = player.serverLevel();
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();

        for (double d = 1.0; d <= range; d += 0.5) {
            Vec3 checkPos = eyePos.add(lookVec.scale(d));
            level.sendParticles(ParticleTypes.SWEEP_ATTACK,
                    checkPos.x, checkPos.y, checkPos.z,
                    2, drillWidth / 2, drillWidth / 2, drillWidth / 2, 0.01);
            level.sendParticles(ParticleTypes.CRIT,
                    checkPos.x, checkPos.y, checkPos.z,
                    1, 0.1, 0.1, 0.1, 0.01);

            AABB area = new AABB(
                    checkPos.x - drillWidth, checkPos.y - drillWidth, checkPos.z - drillWidth,
                    checkPos.x + drillWidth, checkPos.y + drillWidth, checkPos.z + drillWidth
            );
            for (Entity e : level.getEntities(player, area, Entity::isAlive)) {
                if (e instanceof LivingEntity living && e != player) {
                    living.hurt(player.damageSources().playerAttack(player), damage);
                }
            }
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0f, 0.5f);

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }
}
