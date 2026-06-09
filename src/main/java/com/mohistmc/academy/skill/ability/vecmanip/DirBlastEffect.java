package com.mohistmc.academy.skill.ability.vecmanip;

import com.mohistmc.academy.skill.ChargingSkillEffect;
import com.mohistmc.academy.skill.PlayerAbilityData;
import net.minecraft.core.BlockPos;
import com.mohistmc.academy.client.effect.EffectHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import com.mohistmc.academy.world.AcademySounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.List;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/**
 * 定向爆破 —— 蓄力后向目标位置释放爆破冲击，伤害敌人并破坏方块
 * <p>
 * 参考旧代码 DirectedBlastwave.scala：
 * - 射线追踪4格找目标位置
 * - AOE 半径3格伤害 + 击退
 * - 破坏周围方块，硬度上限随熟练度提升
 *
 * @author Mgazul
 */
public class DirBlastEffect implements ChargingSkillEffect {

    private static final int MIN_TICKS = 6;
    private static final int MAX_TICKS = 50;
    private static final int MAX_TOLERANT_TICKS = 200;
    private static final double RANGE = 4.0;
    private static final double AOE_RANGE = 3.0;

    @Override
    public String getId() {
        return "dir_blast";
    }

    @Override
    public int getMinChargeTicks() {
        return MIN_TICKS;
    }

    @Override
    public int getMaxChargeTicks() {
        return MAX_TOLERANT_TICKS;
    }

    @Override
    public void onChargingStart(ServerPlayer player, PlayerAbilityData data) {
    }

    @Override
    public boolean onChargingTick(ServerPlayer player, PlayerAbilityData data, int ticks) {
        return ticks <= MAX_TOLERANT_TICKS;
    }

    @Override
    public void onChargingRelease(ServerPlayer player, PlayerAbilityData data, int ticks) {
        if (ticks < MIN_TICKS || ticks > MAX_TICKS) return;

        float exp = data.getProficiency(getId());
        float cp = lerpf(160, 200, exp);
        float overload = lerpf(50, 30, exp);

        if (!data.isDevMode()) {
            if (data.getCurrentCp() < cp) return;
            data.setCurrentCp(data.getCurrentCp() - cp);
            data.addOverload(overload);
        }

        ServerLevel level = player.serverLevel();
        float damage = lerpf(10, 25, exp);
        float breakProb = lerpf(0.5f, 0.8f, exp);
        float dropRate = lerpf(0.4f, 0.9f, exp);

        float breakHardness;
        if (exp < 0.25f) breakHardness = 2.9f;
        else if (exp < 0.5f) breakHardness = 25f;
        else breakHardness = 55f;

        // 找目标位置
        Vec3 lookDir = player.getLookAngle();
        Vec3 eyePos = player.getEyePosition();
        Vec3 position = eyePos.add(lookDir.scale(RANGE));

        // 检查实体命中
        var entities = level.getEntities(player,
                player.getBoundingBox().inflate(RANGE),
                e -> e.isAlive() && e.isPickable());
        for (Entity e : entities) {
            var result = e.getBoundingBox().clip(eyePos, eyePos.add(lookDir.scale(RANGE)));
            if (result.isPresent()) {
                position = e.getEyePosition();
                break;
            }
        }

        // 伤害AOE内的实体
        Vec3 finalPos = position;
        AABB aoeArea = new AABB(
                finalPos.x - AOE_RANGE, finalPos.y - AOE_RANGE, finalPos.z - AOE_RANGE,
                finalPos.x + AOE_RANGE, finalPos.y + AOE_RANGE, finalPos.z + AOE_RANGE
        );
        boolean effective = false;

        for (Entity e : level.getEntities(player, aoeArea, Entity::isAlive)) {
            if (e instanceof LivingEntity living && e != player) {
                living.hurt(player.damageSources().playerAttack(player), damage);
                // 击退
                Vec3 delta = player.getEyePosition().subtract(living.getEyePosition()).normalize();
                delta = new Vec3(delta.x, delta.y - 0.4, delta.z).normalize();
                living.setDeltaMovement(delta.x * -1.2, delta.y * -1.2, delta.z * -1.2);
                living.hurtMarked = true;
                effective = true;
            }
        }

        // 破坏周围方块
        int cx = (int) Math.round(finalPos.x);
        int cy = (int) Math.round(finalPos.y);
        int cz = (int) Math.round(finalPos.z);

        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -3; dy <= 3; dy++) {
                for (int dz = -3; dz <= 3; dz++) {
                    if (dx * dx + dy * dy + dz * dz > 6) continue;
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    if (level.random.nextFloat() >= breakProb) continue;

                    BlockPos pos = new BlockPos(cx + dx, cy + dy, cz + dz);
                    BlockState state = level.getBlockState(pos);
                    float hardness = state.getDestroySpeed(level, pos);

                    if (hardness >= 0 && hardness <= breakHardness && !state.isAir()) {
                        BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(level, pos, state, player);
                        NeoForge.EVENT_BUS.post(breakEvent);
                        if (!breakEvent.isCanceled()) {
                            if (level.random.nextFloat() < dropRate) {
                                state.getBlock().playerDestroy(level, player, pos, state, null, ItemStack.EMPTY);
                            }
                            level.destroyBlock(pos, false);
                        }
                    }
                }
            }
        }

        // 粒子
        EffectHelper.glowBurst(level, finalPos.x, finalPos.y, finalPos.z, 20, 0.3f, 0x88FFCC44, 12, AOE_RANGE / 2);

        data.addProficiency(getId(), effective ? 0.0025f : 0.0012f);
        AcademySounds.playSound(level, finalPos.x, finalPos.y, finalPos.z,
                AcademySounds.VM_DIRECTED_BLAST, SoundSource.PLAYERS, 0.5f, 1.0f);
    }

    @Override
    public void onChargingAbort(ServerPlayer player, PlayerAbilityData data) {
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
    }
}
