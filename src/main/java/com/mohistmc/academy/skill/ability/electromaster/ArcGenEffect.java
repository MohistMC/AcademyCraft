package com.mohistmc.academy.skill.ability.electromaster;

import com.mohistmc.academy.client.effect.ElectroArcEntity;
import com.mohistmc.academy.world.AcademySounds;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import com.mohistmc.academy.world.AcademyEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/**
 * 电弧激发 —— 向前方发射一道电弧，命中实体造成伤害，命中水面有概率电出熟鱼，命中方块有概率点火
 * <p>
 * 参考旧代码 ArcGen.java：
 * - 射程 6~15（随熟练度）
 * - 伤害 5~9（随熟练度）
 * - 命中水方块且熟练度>0.5 有10%概率生成熟鱼
 * - 命中普通方块有概率在方块上方点火（0%~60%）
 * - 熟练度>=1.0 可以眩晕敌人
 *
 * @author Mgazul
 */
public class ArcGenEffect implements SkillEffect {

    @Override
    public String getId() {
        return "arc_gen";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());

        // 消耗 CP 和 Overload（与旧代码一致）
        float cp = lerpf(30, 70, exp);
        float overload = lerpf(18, 11, exp);

        if (!data.isDevMode()) {
            if (data.getCurrentCp() < cp) return;
            data.setCurrentCp(data.getCurrentCp() - cp);
            data.addOverload(overload);
        }

        float range = lerpf(6, 15, exp);
        float damage = lerpf(5, 9, exp);
        float igniteProb = lerpf(0, 0.6f, exp);
        double fishProb = exp > 0.5f ? 0.1 : 0;
        boolean canStun = exp >= 1.0f;

        ServerLevel level = player.serverLevel();
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();

        // 射线追踪：实体 + 方块（包括水）
        TraceResult result = trace(player, eyePos, lookVec, range);

        // 播放电弧音效
        AcademySounds.playSound(level, player.getX(), player.getY(), player.getZ(),
                AcademySounds.EM_ARC_WEAK, SoundSource.PLAYERS, 0.5f, 1.0f);

        // 生成客户端电弧特效（对应旧代码 EntityArc with weakArc pattern）
        ElectroArcEntity arc = new ElectroArcEntity(AcademyEntities.ELECTRO_ARC.get(), level);
        arc.setPos(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());
        arc.setYRot(player.getYRot());
        arc.setXRot(player.getXRot());
        arc.setBeam(range).setLife(10).setArcCount(2)
                .setWiggle(0.7f, 0.1f, 0.4f);
        level.addFreshEntity(arc);

        float expIncr = 0f;

        if (result != null) {
            if (result.isEntity && result.entity != null) {
                // === 命中实体 ===
                Entity target = result.entity;
                target.hurt(player.damageSources().playerAttack(player), damage);

                // 眩晕效果（熟练度满时）
                if (canStun && target instanceof LivingEntity living) {
                    // 通过强伤害造成类似眩晕的效果
                    living.hurtMarked = true;
                    living.setDeltaMovement(living.getDeltaMovement().add(0, 0.3, 0));
                }

                expIncr = getExpIncr(exp, true);
            } else if (result.blockPos != null) {
                // === 命中方块 ===
                BlockState state = level.getBlockState(result.blockPos);

                if (state.is(Blocks.WATER)) {
                    // 水中电鱼
                    if (level.random.nextDouble() < fishProb) {
                        Vec3 pos = result.hitPos != null ? result.hitPos : Vec3.atCenterOf(result.blockPos);
                        ItemEntity fish = new ItemEntity(level,
                                pos.x, pos.y, pos.z,
                                new ItemStack(Items.COOKED_COD));
                        level.addFreshEntity(fish);
                    }
                } else {
                    // 普通方块：概率点火
                    if (level.random.nextDouble() < igniteProb) {
                        BlockPos firePos = result.blockPos.above();
                        if (level.isEmptyBlock(firePos)) {
                            level.setBlockAndUpdate(firePos, Blocks.FIRE.defaultBlockState());
                        }
                    }
                }

                expIncr = getExpIncr(exp, false);
            }

            if (expIncr > 0 && !data.isDevMode()) {
                data.addProficiency(getId(), expIncr);
            }
        }

        // 冷却时间（由 SkillRegistry 的 Builder 处理基础冷却，这里不强制设置）
    }

    /**
     * 射线追踪，检测实体和方块（包括水）。
     * 与旧代码 Raytrace.traceLiving 一致：水流也算作"击中"
     */
    private TraceResult trace(ServerPlayer player, Vec3 start, Vec3 dir, double range) {
        ServerLevel level = player.serverLevel();

        // 1. 检测实体命中
        EntityHitResult entityHit = rayTraceEntities(player, start, dir, range);

        // 2. 手动步进检测方块（包括水）
        double step = 0.3;
        BlockPos waterHit = null;
        Vec3 waterHitPos = null;
        BlockPos solidHit = null;
        Vec3 solidHitPos = null;

        for (double d = 0; d <= range; d += step) {
            Vec3 pos = start.add(dir.scale(d));
            BlockPos bp = BlockPos.containing(pos.x, pos.y, pos.z);
            BlockState state = level.getBlockState(bp);

            if ((state.is(Blocks.WATER) || state.is(Blocks.BUBBLE_COLUMN)) && waterHit == null) {
                waterHit = bp;
                waterHitPos = pos;
            }

            if (state.isSolid() && solidHit == null) {
                solidHit = bp;
                solidHitPos = pos;
                break; // 遇到固体方块立即停止
            }
        }

        // 3. 决定返回哪个结果——最近者优先
        // 实体
        double entityDist = entityHit != null ? start.distanceTo(entityHit.getLocation()) : Double.MAX_VALUE;
        double solidDist = solidHit != null ? start.distanceTo(solidHitPos) : Double.MAX_VALUE;
        double waterDist = waterHit != null ? start.distanceTo(waterHitPos) : Double.MAX_VALUE;

        // 找最近的命中
        TraceResult best = null;
        double bestDist = Double.MAX_VALUE;

        if (entityDist < bestDist) {
            best = new TraceResult();
            best.isEntity = true;
            best.entity = entityHit.getEntity();
            bestDist = entityDist;
        }

        if (solidDist < bestDist) {
            best = new TraceResult();
            best.isEntity = false;
            best.blockPos = solidHit;
            best.hitPos = solidHitPos;
            bestDist = solidDist;
        }

        if (waterDist < bestDist) {
            best = new TraceResult();
            best.isEntity = false;
            best.blockPos = waterHit;
            best.hitPos = waterHitPos;
        }

        return best;
    }

    private EntityHitResult rayTraceEntities(ServerPlayer player, Vec3 start, Vec3 dir, double range) {
        Vec3 end = start.add(dir.scale(range));
        AABB searchArea = player.getBoundingBox().inflate(range);
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

    private float getExpIncr(float exp, boolean effectiveHit) {
        if (effectiveHit) {
            return lerpf(0.0048f, 0.0072f, exp);
        } else {
            return lerpf(0.0018f, 0.0027f, exp);
        }
    }

    @Override
    public int getCooldownTicks(float proficiency) {
        return (int) lerpf(15, 5, proficiency);
    }

    // ==================== 内部类 ====================

    private static class TraceResult {
        boolean isEntity;
        Entity entity;
        BlockPos blockPos;
        Vec3 hitPos;
    }
}
