package com.mohistmc.academy.skill.ability.electromaster;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import com.mohistmc.academy.world.AcademySounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/**
 * 电磁牵引 —— 将玩家拉向准星对准的金属方块或实体
 * <p>
 * 参考旧代码 MagMovement.scala：
 * - 射线追踪找金属目标（方块或实体）
 * - 每 tick 向目标加速移动
 * - 目标必须是金属类方块/实体
 *
 * @author Mgazul
 */
public class MagMovementEffect implements SkillEffect {

    private static final double ACCEL = 0.08;
    private static final double VELOCITY = 1.0;

    @Override
    public String getId() {
        return "mag_movement";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        ServerLevel level = player.serverLevel();
        float proficiency = data.getProficiency(getId());
        double range = lerpf(15, 30, proficiency);

        // 射线追踪找目标
        HitResult hit = findTarget(player, range, proficiency);
        if (hit == null) return;

        Vec3 targetPos;
        if (hit instanceof BlockHitResult blockHit) {
            targetPos = Vec3.atCenterOf(blockHit.getBlockPos());
        } else if (hit instanceof EntityHitResult entityHit) {
            targetPos = entityHit.getEntity().getEyePosition();
        } else {
            return;
        }

        Vec3 playerPos = player.position();
        Vec3 dir = targetPos.subtract(playerPos);
        double dist = dir.length();

        if (dist < 1.0) {
            // 到达目标，轻柔落地
            player.setDeltaMovement(Vec3.ZERO);
            player.hurtMarked = true;
            player.fallDistance = 0.0f;
            return;
        }

        dir = dir.normalize();
        Vec3 currentMotion = player.getDeltaMovement();

        // 平滑加速
        double newMx = tryAdjust(currentMotion.x, dir.x * VELOCITY);
        double newMy = tryAdjust(currentMotion.y, dir.y * VELOCITY);
        double newMz = tryAdjust(currentMotion.z, dir.z * VELOCITY);

        player.setDeltaMovement(newMx, newMy, newMz);
        player.hurtMarked = true;
        player.fallDistance = 0.0f;

        AcademySounds.playSound(level, player.getX(), player.getY(), player.getZ(),
                AcademySounds.EM_MOVE_LOOP, SoundSource.PLAYERS, 0.5f, 1.0f);
    }

    /**
     * 尝试将值向目标调整一步（每次最多移动 ACCEL）
     */
    private double tryAdjust(double from, double to) {
        double d = to - from;
        if (Math.abs(d) < ACCEL) return to;
        return d > 0 ? from + ACCEL : from - ACCEL;
    }

    /**
     * 寻找前方可磁化的目标（方块优先）
     */
    private HitResult findTarget(ServerPlayer player, double range, float proficiency) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();

        BlockHitResult blockHit = (BlockHitResult) player.pick(range, 0, false);
        EntityHitResult entityHit = rayTraceEntities(player, eyePos, lookVec, range);

        // 检查方块是否为金属
        if (blockHit != null && blockHit.getType() != HitResult.Type.MISS) {
            BlockState state = player.serverLevel().getBlockState(blockHit.getBlockPos());
            if (isMetalBlock(state, proficiency)) {
                // 如果实体更近，优先选择实体
                if (entityHit != null && entityHit.getEntity() instanceof LivingEntity) {
                    double blockDist = eyePos.distanceTo(blockHit.getLocation());
                    double entityDist = eyePos.distanceTo(entityHit.getLocation());
                    if (entityDist < blockDist) {
                        return entityHit;
                    }
                }
                return blockHit;
            }
        }

        // 没有有效方块目标，检查实体
        if (entityHit != null && entityHit.getEntity() instanceof LivingEntity) {
            return entityHit;
        }

        return null;
    }

    private EntityHitResult rayTraceEntities(ServerPlayer player, Vec3 start, Vec3 lookVec, double range) {
        Vec3 end = start.add(lookVec.scale(range));
        for (Entity entity : player.level().getEntities(player,
                player.getBoundingBox().inflate(range),
                e -> e != player && e.isAlive() && e.isPickable())) {
            var result = entity.getBoundingBox().inflate(0.3).clip(start, end);
            if (result.isPresent()) {
                return new EntityHitResult(entity, result.get());
            }
        }
        return null;
    }

    /**
     * 判断方块是否为可用金属（低熟练度时只能吸附强金属方块）
     */
    private boolean isMetalBlock(BlockState state, float proficiency) {
        if (isStrongMetal(state)) return true;
        // 熟练度 >= 0.6 时可以使用弱金属
        if (proficiency >= 0.6f && isWeakMetal(state)) return true;
        return false;
    }

    private boolean isStrongMetal(BlockState state) {
        return state.is(Blocks.IRON_BLOCK) || state.is(Blocks.RAW_IRON_BLOCK)
                || state.is(Blocks.NETHERITE_BLOCK) || state.is(Blocks.ANVIL)
                || state.is(Blocks.HEAVY_CORE);
    }

    private boolean isWeakMetal(BlockState state) {
        return state.is(Blocks.IRON_ORE) || state.is(Blocks.DEEPSLATE_IRON_ORE)
                || state.is(Blocks.GOLD_BLOCK) || state.is(Blocks.RAW_GOLD_BLOCK)
                || state.is(Blocks.GOLD_ORE) || state.is(Blocks.DEEPSLATE_GOLD_ORE)
                || state.is(Blocks.HOPPER) || state.is(Blocks.PISTON)
                || state.is(Blocks.STICKY_PISTON) || state.is(Blocks.COPPER_BLOCK)
                || state.is(Blocks.COPPER_ORE) || state.is(Blocks.DEEPSLATE_COPPER_ORE);
    }
}
