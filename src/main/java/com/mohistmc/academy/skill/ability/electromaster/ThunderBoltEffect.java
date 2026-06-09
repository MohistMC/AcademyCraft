package com.mohistmc.academy.skill.ability.electromaster;

import com.mohistmc.academy.client.effect.ElectroArcEntity;
import com.mohistmc.academy.world.AcademySounds;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import com.mohistmc.academy.world.AcademyEntities;
import com.mohistmc.academy.client.effect.EffectHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/**
 * 雷击之枪 —— 对视线方向发射闪电，命中目标+AOE伤害
 * <p>
 * 参考旧代码 ThunderBolt.scala：
 * - 射线追踪找目标（RANGE=20）
 * - 主目标伤害 + 落点周围 AOE（AOE_RANGE=8）
 * - 熟练度 > 0.2 时有概率附加减速效果
 *
 * @author Mgazul
 */
public class ThunderBoltEffect implements SkillEffect {

    private static final double RANGE = 20.0;
    private static final double AOE_RANGE = 8.0;

    @Override
    public String getId() {
        return "thunder_bolt";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        ServerLevel level = player.serverLevel();
        float proficiency = data.getProficiency(getId());

        float damage = lerpf(10, 25, proficiency);
        float aoeDamage = lerpf(6, 15, proficiency);

        // 射线追踪找目标
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 endPos = eyePos.add(lookVec.scale(RANGE));

        // 先检查实体命中
        EntityHitResult entityHit = rayTraceEntities(player, eyePos, endPos);
        // 检查方块命中
        BlockHitResult blockHit = (BlockHitResult) player.pick(RANGE, 0, false);

        Vec3 impactPoint;
        final Entity targetEntity;
        boolean hitEntity = false;

        if (entityHit != null) {
            double entityDist = eyePos.distanceTo(entityHit.getLocation());
            double blockDist = eyePos.distanceTo(blockHit.getLocation());
            if (entityDist < blockDist) {
                // 命中实体
                targetEntity = entityHit.getEntity();
                impactPoint = targetEntity.getEyePosition();
                hitEntity = true;
            } else {
                targetEntity = null;
                impactPoint = blockHit.getLocation();
            }
        } else {
            targetEntity = null;
            impactPoint = blockHit.getLocation();
        }

        // 主目标伤害
        if (hitEntity && targetEntity != null && targetEntity.isAlive()) {
            targetEntity.hurt(player.damageSources().lightningBolt(), damage);

            // 减速效果（熟练度 > 0.2 且 80% 概率）
            if (proficiency > 0.2 && Math.random() < 0.8 && targetEntity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 3));
            }

            // 粒子效果
            EffectHelper.arcSpark(level, targetEntity.getX(), targetEntity.getY() + targetEntity.getBbHeight() / 2, targetEntity.getZ(), 20, 0.5);
        }

        // AOE 伤害（落点周围）
        List<Entity> aoes = level.getEntities(player,
                new AABB(impactPoint.x - AOE_RANGE, impactPoint.y - AOE_RANGE, impactPoint.z - AOE_RANGE,
                        impactPoint.x + AOE_RANGE, impactPoint.y + AOE_RANGE, impactPoint.z + AOE_RANGE),
                e -> e.isAlive() && e != player && (targetEntity == null || e != targetEntity));

        for (Entity e : aoes) {
            if (e instanceof LivingEntity living) {
                double dist = impactPoint.distanceTo(e.position());
                if (dist <= AOE_RANGE) {
                    living.hurt(player.damageSources().lightningBolt(), aoeDamage);

                    if (proficiency > 0.2 && Math.random() < 0.8) {
                        living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 3));
                    }

                    EffectHelper.arcSpark(level, e.getX(), e.getY() + e.getBbHeight() / 2, e.getZ(), 10, 0.3);
                }
            }
        }

        // 音效
        AcademySounds.playSound(level, impactPoint.x, impactPoint.y, impactPoint.z,
                AcademySounds.EM_ARC_STRONG, SoundSource.PLAYERS, 0.6f, 1.0f);

        // 生成客户端电弧特效（对应旧代码 EntityArc with strongArc pattern）
        for (int i = 0; i < 2; i++) {
            ElectroArcEntity mainArc = new ElectroArcEntity(AcademyEntities.ELECTRO_ARC.get(), level);
            mainArc.setPos(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());
            mainArc.setYRot(player.getYRot());
            mainArc.setXRot(player.getXRot());
            mainArc.setBeam(RANGE).setLife(20).setArcCount(3);
            level.addFreshEntity(mainArc);
        }

        // AOE电弧（对应旧代码 aoeArc）
        for (Entity e : aoes) {
            ElectroArcEntity aoeArc = new ElectroArcEntity(AcademyEntities.ELECTRO_ARC.get(), level);
            aoeArc.setPos(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());
            aoeArc.setYRot(player.getYRot());
            aoeArc.setXRot(player.getXRot());
            aoeArc.setBeam(player.getEyePosition().distanceTo(e.getEyePosition())).setLife(20).setArcCount(2);
            level.addFreshEntity(aoeArc);
        }

        // 熟练度
        boolean effective = hitEntity || !aoes.isEmpty();
        data.addProficiency(getId(), effective ? 0.005f : 0.003f);
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
}
