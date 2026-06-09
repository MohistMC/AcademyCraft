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
 * 血液回流 —— 吸取周围敌人生命值
 */
public class BloodRetroEffect implements SkillEffect {

    @Override
    public String getId() {
        return "blood_retro";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        float damage = lerpf(8.0f, 15.0f, exp);
        float radius = lerpf(4.0f, 7.0f, exp);
        float healRatio = lerpf(0.3f, 0.5f, exp);

        ServerLevel level = player.serverLevel();

        level.sendParticles(ParticleTypes.DAMAGE_INDICATOR,
                player.getX(), player.getY() + player.getBbHeight() / 2, player.getZ(),
                20, radius / 2, 0.5, radius / 2, 0.1);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENDERMAN_HURT, SoundSource.PLAYERS, 1.0f, 0.5f);

        AABB area = new AABB(
                player.getX() - radius, player.getY() - 1, player.getZ() - radius,
                player.getX() + radius, player.getY() + 2, player.getZ() + radius
        );

        float totalHealed = 0;
        for (Entity e : level.getEntities(player, area, Entity::isAlive)) {
            if (e instanceof LivingEntity living && e != player) {
                living.hurt(player.damageSources().playerAttack(player), damage);
                totalHealed += damage * healRatio;
                // 吸血粒子
                level.sendParticles(ParticleTypes.HEART,
                        e.getX(), e.getY() + e.getBbHeight() / 2, e.getZ(),
                        1, 0.1, 0.1, 0.1, 0.01);
            }
        }

        // 治疗玩家
        if (totalHealed > 0) {
            player.heal(totalHealed);
        }

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }
}
