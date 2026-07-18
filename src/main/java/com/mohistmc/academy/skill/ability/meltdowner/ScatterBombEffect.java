package com.mohistmc.academy.skill.ability.meltdowner;

import com.mohistmc.academy.client.effect.EffectHelper;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import com.mohistmc.academy.world.AcademySounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/**
 * 散射弹 —— 向周围散射多个小型能量弹
 */
public class ScatterBombEffect implements SkillEffect {

    @Override
    public String getId() {
        return "scatter_bomb";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        float damage = lerpf(3.0f, 6.0f, exp);
        int count = (int) lerpf(3, 6, exp);
        float radius = lerpf(3.0f, 5.0f, exp);

        ServerLevel level = player.serverLevel();
        Vec3 playerPos = player.position();

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                AcademySounds.MD_BALLSHOOT, SoundSource.PLAYERS, 1.0f, 2.0f);

        for (int i = 0; i < count; i++) {
            double angle = (Math.PI * 2 * i) / count;
            Vec3 offset = new Vec3(Math.cos(angle) * radius, 0, Math.sin(angle) * radius);
            Vec3 targetPos = playerPos.add(offset);

            EffectHelper.glowBurst(level, targetPos.x, targetPos.y + 1, targetPos.z, 2, 0.3f, 0x88FFCC44, 12, 0.3);

            AABB area = new AABB(
                    targetPos.x - 2, targetPos.y - 2, targetPos.z - 2,
                    targetPos.x + 2, targetPos.y + 2, targetPos.z + 2
            );
            for (Entity e : level.getEntities(player, area, Entity::isAlive)) {
                if (e instanceof LivingEntity living) {
                    living.hurt(player.damageSources().playerAttack(player), damage);
                }
            }
        }

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }

    @Override
    public int getCooldownTicks(float proficiency) {
        return (int) lerpf(30, 15, proficiency);
    }
}
