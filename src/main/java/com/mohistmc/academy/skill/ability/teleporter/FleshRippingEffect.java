package com.mohistmc.academy.skill.ability.teleporter;

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
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/**
 * 撕裂肉体 —— 对视线方向目标实体造成空间撕裂伤害
 */
public class FleshRippingEffect implements SkillEffect {

    private static final double RANGE = 25.0;

    @Override
    public String getId() {
        return "flesh_ripping";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        float damage = lerpf(15.0f, 30.0f, exp);

        ServerLevel level = player.serverLevel();
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 endPos = eyePos.add(lookVec.scale(RANGE));

        EntityHitResult hit = rayTraceEntities(player, eyePos, endPos);
        if (hit == null) {
            return;
        }

        Entity target = hit.getEntity();
        if (!(target instanceof LivingEntity living)) {
            return;
        }

        level.sendParticles(ParticleTypes.DAMAGE_INDICATOR,
                target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
                10, 0.5, 0.5, 0.5, 0.1);

        level.playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.ENDERMAN_SCREAM, SoundSource.PLAYERS, 1.0f, 0.5f);

        living.hurt(player.damageSources().playerAttack(player), damage);

        if (exp > 0.5f && level.random.nextFloat() < 0.6f) {
            living.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 1));
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
