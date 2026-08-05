package com.mohistmc.academy.skill.ability.aerohand;

import com.mohistmc.academy.client.effect.EffectHelper;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
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
 * 风暴核心 —— 大范围风暴伤害
 */
public class StormCoreEffect implements SkillEffect {

    @Override
    public String getId() {
        return "storm_core";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        float damage = lerpf(15.0f, 30.0f, exp);
        float radius = lerpf(5.0f, 8.0f, exp);

        ServerLevel level = player.serverLevel();
        Vec3 playerPos = player.position();

        for (int i = 0; i < 50; i++) {
            double angle = Math.random() * Math.PI * 2;
            double dist = Math.random() * radius;
            double x = playerPos.x + Math.cos(angle) * dist;
            double z = playerPos.z + Math.sin(angle) * dist;
            EffectHelper.windBurst(level, x, playerPos.y + 0.5 + Math.random() * 2, z, 1, 0.1);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 1.0f, 1.0f);

        AABB area = new AABB(
                player.getX() - radius, player.getY() - 2, player.getZ() - radius,
                player.getX() + radius, player.getY() + 4, player.getZ() + radius
        );
        for (Entity e : level.getEntities(player, area, Entity::isAlive)) {
            if (e instanceof LivingEntity living && e != player) {
                float distancedDamage = damage * (float) (1.0 - Math.min(e.distanceToSqr(playerPos.x, playerPos.y, playerPos.z) / (radius * radius), 1.0));
                living.hurt(player.damageSources().playerAttack(player), Math.max(distancedDamage, 1));
            }
        }

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }
}
