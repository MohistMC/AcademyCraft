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
 * 地面冲击 —— 对周围地面敌人造成伤害并击飞
 */
public class GroundShockEffect implements SkillEffect {

    @Override
    public String getId() {
        return "ground_shock";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        float damage = lerpf(8.0f, 15.0f, exp);
        float radius = lerpf(3.0f, 6.0f, exp);
        float knockup = lerpf(0.8f, 1.5f, exp);

        ServerLevel level = player.serverLevel();
        Vec3 playerPos = player.position();

        // 地面冲击粒子效果
        for (double angle = 0; angle < Math.PI * 2; angle += 0.3) {
            for (double r = 1.0; r <= radius; r += 0.5) {
                double x = playerPos.x + Math.cos(angle) * r;
                double z = playerPos.z + Math.sin(angle) * r;
                level.sendParticles(ParticleTypes.EXPLOSION,
                        x, playerPos.y + 0.1, z,
                        1, 0.1, 0, 0.1, 0.01);
            }
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.0f, 0.5f);

        AABB area = new AABB(
                player.getX() - radius, player.getY() - 1, player.getZ() - radius,
                player.getX() + radius, player.getY() + 2, player.getZ() + radius
        );
        for (Entity e : level.getEntities(player, area, Entity::isAlive)) {
            if (e instanceof LivingEntity living && e != player) {
                living.hurt(player.damageSources().playerAttack(player), damage);
                Vec3 knock = new Vec3(e.getX() - player.getX(), 0, e.getZ() - player.getZ()).normalize().scale(0.5);
                knock = knock.add(0, knockup, 0);
                living.setDeltaMovement(living.getDeltaMovement().add(knock));
                living.hurtMarked = true;
            }
        }

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }
}
