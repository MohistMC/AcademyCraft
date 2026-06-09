package com.mohistmc.academy.skill.ability;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * @author Mgazul
 * @date 2026/5/30 22:01
 */
public class ArcGenEffect implements SkillEffect {

    private static final double RANGE = 10.0;

    @Override
    public String getId() {
        return "arc_gen";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float proficiency = data.getProficiency(getId());
        float damage = 4.0f + proficiency * 4.0f;

        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 endPos = eyePos.add(lookVec.scale(RANGE));

        HitResult hitResult = rayTrace(player, eyePos, endPos);

        ServerLevel level = player.serverLevel();
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.PLAYERS, 0.5f, 1.0f + proficiency * 0.5f);

        spawnArcParticles(level, eyePos, hitResult.getLocation());

        if (hitResult instanceof EntityHitResult entityHit) {
            Entity target = entityHit.getEntity();
            if (target != player && target.isAlive()) {
                DamageSource source = player.damageSources().playerAttack(player);
                target.hurt(source, damage);

                spawnLightningVisual(level, target.blockPosition());
                level.playSound(null, target.getX(), target.getY(), target.getZ(),
                        SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 0.3f, 1.2f);
            }
        } else if (hitResult instanceof BlockHitResult blockHit) {
            spawnLightningVisual(level, blockHit.getBlockPos());
        }
    }

    private HitResult rayTrace(ServerPlayer player, Vec3 start, Vec3 end) {
        EntityHitResult entityHit = rayTraceEntities(player, start, end);
        BlockHitResult blockHit = (BlockHitResult) player.pick(RANGE, 0, false);

        if (entityHit != null) {
            double entityDist = start.distanceTo(entityHit.getLocation());
            double blockDist = start.distanceTo(blockHit.getLocation());
            if (entityDist < blockDist) {
                return entityHit;
            }
        }
        return blockHit;
    }

    private EntityHitResult rayTraceEntities(ServerPlayer player, Vec3 start, Vec3 end) {
        AABB searchArea = player.getBoundingBox().inflate(RANGE);
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

    private void spawnArcParticles(ServerLevel level, Vec3 start, Vec3 end) {
        double dx = end.x - start.x;
        double dy = end.y - start.y;
        double dz = end.z - start.z;
        double dist = start.distanceTo(end);
        int count = (int) (dist * 3);

        for (int i = 0; i < count; i++) {
            double t = (double) i / count;
            double x = start.x + dx * t + (level.random.nextGaussian() * 0.15);
            double y = start.y + dy * t + (level.random.nextGaussian() * 0.15);
            double z = start.z + dz * t + (level.random.nextGaussian() * 0.15);
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, y, z, 1, 0.1, 0.1, 0.1, 0.5);
        }

        level.sendParticles(ParticleTypes.ELECTRIC_SPARK, end.x, end.y, end.z, 10, 0.3, 0.3, 0.3, 0.5);
    }

    private void spawnLightningVisual(ServerLevel level, BlockPos pos) {
        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
        if (lightning != null) {
            lightning.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            lightning.setVisualOnly(true);
            level.addFreshEntity(lightning);
        }
    }
}