package com.mohistmc.academy.skill.ability.telekinesis;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/**
 * 念力猛击 —— 用念力将周围敌人猛击到地面，造成伤害和眩晕
 */
public class PsychoSlamEffect implements SkillEffect {

    @Override
    public String getId() {
        return "psycho_slam";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        float damage = lerpf(15.0f, 28.0f, exp);
        float radius = lerpf(4.0f, 7.0f, exp);

        ServerLevel level = player.serverLevel();
        Vec3 playerPos = player.position();

        level.sendParticles(ParticleTypes.CRIT,
                player.getX(), player.getY() + player.getBbHeight() / 2, player.getZ(),
                30, radius / 2, 0.5, radius / 2, 0.1);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENDER_DRAGON_GROWL, SoundSource.PLAYERS, 1.0f, 0.8f);

        AABB area = new AABB(
                player.getX() - radius, player.getY() - 2, player.getZ() - radius,
                player.getX() + radius, player.getY() + 3, player.getZ() + radius
        );

        for (Entity e : level.getEntities(player, area, Entity::isAlive)) {
            if (e instanceof LivingEntity living && e != player) {
                // 将敌人拉向地面
                living.setDeltaMovement(0, -2.0, 0);
                living.hurtMarked = true;
                living.hurt(player.damageSources().playerAttack(player), damage);
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
                living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 1));
            }
        }

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }
}
