package com.mohistmc.academy.skill.effect;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ThunderBoltEffect implements SkillEffect {

    @Override
    public String getId() {
        return "thunder_bolt";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        ServerLevel level = player.serverLevel();
        float proficiency = data.getProficiency(getId());
        float damage = 8.0f + proficiency * 8.0f;
        double radius = 5.0 + proficiency * 5.0;

        Vec3 pos = player.position();
        List<Entity> entities = level.getEntities(player,
                new AABB(pos.x - radius, pos.y - radius, pos.z - radius,
                        pos.x + radius, pos.y + radius, pos.z + radius),
                Entity::isAlive);

        for (Entity e : entities) {
            if (e instanceof LivingEntity target && e != player) {
                double dist = pos.distanceTo(e.position());
                if (dist <= radius) {
                    target.hurt(player.damageSources().lightningBolt(), damage);

                    // 粒子效果
                    level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                            e.getX(), e.getY() + e.getBbHeight() / 2, e.getZ(),
                            10, 0.3, 0.3, 0.3, 0.5);
                }
            }
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 0.8f, 1.0f);
    }
}
