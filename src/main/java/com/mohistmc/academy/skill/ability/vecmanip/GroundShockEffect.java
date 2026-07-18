package com.mohistmc.academy.skill.ability.vecmanip;

import com.mohistmc.academy.client.effect.EffectHelper;
import com.mohistmc.academy.skill.ChargingSkillEffect;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.world.AcademySounds;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/**
 * 地面冲击 —— 向地面释放冲击波，破坏地形并伤害敌人
 * <p>
 * 参考旧代码 Groundshock.scala：
 * - 必须站在地面上才能释放
 * - 消耗能量预算破坏/转化前方扇形区域方块
 * - 石头变圆石，草变泥土等
 * - 熟练度=1时破坏大范围弱方块
 *
 * @author Mgazul
 */
public class GroundShockEffect implements ChargingSkillEffect {

    private static final int MIN_TICKS = 5;

    @Override
    public String getId() {
        return "ground_shock";
    }

    @Override
    public int getMinChargeTicks() {
        return MIN_TICKS;
    }

    @Override
    public int getMaxChargeTicks() {
        return MIN_TICKS;
    }

    @Override
    public void onChargingStart(ServerPlayer player, PlayerAbilityData data) {
    }

    @Override
    public boolean onChargingTick(ServerPlayer player, PlayerAbilityData data, int ticks) {
        return true;
    }

    @Override
    public void onChargingRelease(ServerPlayer player, PlayerAbilityData data, int ticks) {
        if (ticks < MIN_TICKS) return;

        float exp = data.getProficiency(getId());

        if (!player.onGround()) return;

        float cp = lerpf(80, 150, exp);
        float overload = lerpf(15, 10, exp);

        if (!data.isDevMode()) {
            if (data.getCurrentCp() < cp) return;
            data.setCurrentCp(data.getCurrentCp() - cp);
            data.addOverload(overload);
        }

        ServerLevel level = player.serverLevel();
        Vec3 lookDir = player.getLookAngle();
        Vec3 planeLook = new Vec3(lookDir.x, 0, lookDir.z).normalize();

        double energy = lerpf(60, 120, exp);
        float damage = lerpf(4, 6, exp);
        int maxIter = (int) lerpf(10, 25, exp);
        float groundBreakProb = 0.3f;

        int px = (int) Math.floor(player.getX());
        int py = (int) Math.floor(player.getY()) - 1;
        int pz = (int) Math.floor(player.getZ());

        Set<BlockPos> visitedBlocks = new HashSet<>();
        Set<Entity> visitedEntities = new HashSet<>();

        int dx = planeLook.x > 0 ? 1 : (planeLook.x < 0 ? -1 : 0);
        int dz = planeLook.z > 0 ? 1 : (planeLook.z < 0 ? -1 : 0);

        int iter = maxIter;
        int x = px, z = pz;

        while (energy > 0 && iter > 0) {
            iter--;
            x += dx;
            z += dz;

            for (int yOff = -1; yOff <= 1; yOff++) {
                BlockPos pos = new BlockPos(x, py + yOff, z);
                if (visitedBlocks.contains(pos)) continue;
                visitedBlocks.add(pos);

                BlockState state = level.getBlockState(pos);

                if (state.isAir()) continue;

                // 方块转化
                if (state.is(Blocks.STONE)) {
                    level.setBlockAndUpdate(pos, Blocks.COBBLESTONE.defaultBlockState());
                    energy -= 0.4;
                } else if (state.is(Blocks.GRASS_BLOCK)) {
                    level.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
                    energy -= 0.2;
                } else if (state.is(Blocks.FARMLAND)) {
                    energy -= 0.1;
                } else {
                    energy -= 0.5;
                }

                // 概率破坏方块
                if (level.random.nextDouble() < groundBreakProb) {
                    BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(level, pos, state, player);
                    NeoForge.EVENT_BUS.post(breakEvent);
                    if (!breakEvent.isCanceled()) {
                        level.destroyBlock(pos, false);
                    }
                }

                // 伤害实体
                AABB aabb = new AABB(pos.getX() - 0.2, pos.getY() - 0.2, pos.getZ() - 0.2,
                        pos.getX() + 1.2, pos.getY() + 2.2, pos.getZ() + 1.2);
                for (Entity e : level.getEntities(player, aabb, Entity::isAlive)) {
                    if (visitedEntities.contains(e)) continue;
                    visitedEntities.add(e);
                    energy -= 1;
                    if (e instanceof LivingEntity living) {
                        living.hurt(player.damageSources().playerAttack(player), damage);
                        living.setDeltaMovement(living.getDeltaMovement().add(0, 0.6 + level.random.nextFloat() * 0.3, 0));
                        living.hurtMarked = true;
                    }
                }

                // 粒子
                EffectHelper.windBurst(level, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 3, 0.3);
            }
        }

        // 熟练度=1 时破坏大范围弱方块
        if (exp >= 1.0f) {
            for (int ox = -5; ox <= 5; ox++) {
                for (int oy = -1; oy <= 1; oy++) {
                    for (int oz = -5; oz <= 5; oz++) {
                        BlockPos pos = new BlockPos(px + ox, py + oy, pz + oz);
                        BlockState state = level.getBlockState(pos);
                        float hardness = state.getDestroySpeed(level, pos);
                        if (hardness >= 0 && hardness <= 0.6f && !state.isAir()) {
                            BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(level, pos, state, player);
                            NeoForge.EVENT_BUS.post(breakEvent);
                            if (!breakEvent.isCanceled()) {
                                level.destroyBlock(pos, true);
                            }
                        }
                    }
                }
            }
        }

        data.addProficiency(getId(), 0.001f);
        AcademySounds.playSound(level, player.getX(), player.getY(), player.getZ(),
                AcademySounds.VM_GROUNDSHOCK, SoundSource.PLAYERS, 2.0f, 0.8f);
    }

    @Override
    public void onChargingAbort(ServerPlayer player, PlayerAbilityData data) {
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
    }

    @Override
    public int getCooldownTicks(float proficiency) {
        return (int) lerpf(50, 25, proficiency);
    }
}
