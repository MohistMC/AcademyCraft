package com.mohistmc.academy.skill.ability.meltdowner;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import com.mohistmc.academy.client.effect.EffectHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import com.mohistmc.academy.world.AcademySounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/**
 * 电子弹 —— 向前方发射能量弹，命中后爆炸
 */
public class ElectronBombEffect implements SkillEffect {

    private static final double RANGE = 15.0;

    @Override
    public String getId() {
        return "electron_bomb";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        float damage = lerpf(5.0f, 10.0f, exp);
        float radius = lerpf(2.0f, 3.5f, exp);

        ServerLevel level = player.serverLevel();
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 endPos = eyePos.add(lookVec.scale(RANGE));

        // 射线追踪找实体
        EntityHitResult hit = rayTraceEntities(player, eyePos, endPos);
        Vec3 impactPos;
        if (hit != null) {
            impactPos = hit.getEntity().position();
        } else {
            impactPos = eyePos.add(lookVec.scale(RANGE));
        }

        // 爆炸效果
        EffectHelper.glowBurst(level, impactPos.x, impactPos.y, impactPos.z, (int) (radius * 2), 0.3f, 0x88FFCC44, 12, radius / 2);

        level.playSound(null, impactPos.x, impactPos.y, impactPos.z,
                AcademySounds.MD_BALLSHOOT, SoundSource.PLAYERS, 1.0f, 1.5f);

        // AOE伤害
        AABB area = new AABB(
                impactPos.x - radius, impactPos.y - radius, impactPos.z - radius,
                impactPos.x + radius, impactPos.y + radius, impactPos.z + radius
        );
        for (Entity e : level.getEntities(player, area, Entity::isAlive)) {
            if (e instanceof LivingEntity living) {
                float distancedDamage = damage * (float) (1.0 - Math.min(e.distanceToSqr(impactPos.x, impactPos.y, impactPos.z) / (radius * radius), 1.0));
                living.hurt(player.damageSources().playerAttack(player), Math.max(distancedDamage, 1));
            }
        }

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }

    private EntityHitResult rayTraceEntities(ServerPlayer player, Vec3 start, Vec3 end) {
        AABB searchArea = player.getBoundingBox().expandTowards(end.subtract(start)).inflate(1.0);
        List<Entity> entities = player.level().getEntities(player, searchArea,
                e -> e != player && e.isAlive() && e.isPickable());

        Entity closest = null;
        double closestDist = Double.MAX_VALUE;
        Vec3 closestHit = null;

        for (Entity entity : entities) {
            AABB box = entity.getBoundingBox().inflate(0.3);
            var result = box.clip(start, end);
            if (result.isPresent()) {
                double dist = start.distanceTo(result.get());
                if (dist < closestDist) {
                    closestDist = dist;
                    closest = entity;
                    closestHit = result.get();
                }
            }
        }

        if (closest != null) {
            return new EntityHitResult(closest, closestHit);
        }
        return null;
    }
}
