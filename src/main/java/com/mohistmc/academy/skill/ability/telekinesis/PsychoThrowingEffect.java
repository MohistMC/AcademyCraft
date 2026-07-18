package com.mohistmc.academy.skill.ability.telekinesis;

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
 * 念力投掷 —— 用念力抓取附近实体并将其抛向空中
 */
public class PsychoThrowingEffect implements SkillEffect {

    @Override
    public String getId() {
        return "psycho_throwing";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        float damage = lerpf(6.0f, 12.0f, exp);
        float radius = lerpf(4.0f, 7.0f, exp);
        float throwStrength = lerpf(1.5f, 3.0f, exp);

        ServerLevel level = player.serverLevel();
        Vec3 playerPos = player.position();

        EffectHelper.glowBurst(level, player.getX(), player.getY() + player.getBbHeight() / 2, player.getZ(), 20, 0.15f, 0xAAFFFFFF, 10, 1.0);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENDER_DRAGON_FLAP, SoundSource.PLAYERS, 0.8f, 1.2f);

        AABB area = new AABB(
                player.getX() - radius, player.getY() - 1, player.getZ() - radius,
                player.getX() + radius, player.getY() + 3, player.getZ() + radius
        );

        for (Entity e : level.getEntities(player, area, Entity::isAlive)) {
            if (e instanceof LivingEntity living && e != player) {
                Vec3 throwVec = new Vec3(
                        e.getX() - player.getX(),
                        1.0,
                        e.getZ() - player.getZ()
                ).normalize().scale(throwStrength);
                living.setDeltaMovement(throwVec.x, throwVec.y + 0.5, throwVec.z);
                living.hurtMarked = true;
                living.hurt(player.damageSources().playerAttack(player), damage);
            }
        }

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }
}
