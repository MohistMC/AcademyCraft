package com.mohistmc.academy.skill.ability.meltdowner;

import com.mohistmc.academy.client.effect.EffectHelper;
import com.mohistmc.academy.client.effect.MeltdownBeamEntity;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import com.mohistmc.academy.world.AcademyEntities;
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
 * 原子崩坏 —— 向前方发射高能射线，穿透多个目标
 */
public class MeltdownerEffect implements SkillEffect {

    private static final double RANGE = 25.0;
    private static final double BEAM_RADIUS = 1.5;

    @Override
    public String getId() {
        return "meltdowner";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        float damage = lerpf(15.0f, 25.0f, exp);
        double range = lerpf(20.0f, 30.0f, exp);

        ServerLevel level = player.serverLevel();
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();

        // 生成熔毁光束实体（客户端渲染绿色射线）
        MeltdownBeamEntity beam = new MeltdownBeamEntity(AcademyEntities.MELTDOWN_BEAM.get(), level);
        beam.setPos(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());
        beam.setBeam(eyePos, lookVec, range);
        level.addFreshEntity(beam);

        for (double d = 1.0; d <= range; d += 0.5) {
            Vec3 checkPos = eyePos.add(lookVec.scale(d));

            AABB area = new AABB(
                    checkPos.x - BEAM_RADIUS, checkPos.y - BEAM_RADIUS, checkPos.z - BEAM_RADIUS,
                    checkPos.x + BEAM_RADIUS, checkPos.y + BEAM_RADIUS, checkPos.z + BEAM_RADIUS
            );
            for (Entity e : level.getEntities(player, area, Entity::isAlive)) {
                if (e instanceof LivingEntity living && e != player) {
                    living.hurt(player.damageSources().playerAttack(player), damage);
                }
            }
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                AcademySounds.MD_MELTDOWNER, SoundSource.PLAYERS, 1.0f, 0.5f);

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }
}
