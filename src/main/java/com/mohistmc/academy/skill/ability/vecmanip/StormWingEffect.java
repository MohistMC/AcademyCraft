package com.mohistmc.academy.skill.ability.vecmanip;

import com.mohistmc.academy.client.effect.EffectHelper;
import com.mohistmc.academy.skill.ChargingSkillEffect;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.world.AcademySounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/**
 * 风暴之翼 —— 获得临时飞行能力，推开周围弱方块和实体
 * <p>
 * 参考旧代码 StormWing.scala：
 * - 蓄力阶段后进入飞行模式
 * - 熟练度<0.15时自动破坏周围弱方块
 * - 熟练度=1时推开周围实体
 * - 持续消耗CP
 *
 * @author Mgazul
 */
public class StormWingEffect implements ChargingSkillEffect {

    @Override
    public String getId() {
        return "storm_wing";
    }

    @Override
    public int getMinChargeTicks() {
        return (int) lerpf(70, 30, 0f);
    }

    @Override
    public int getMaxChargeTicks() {
        return (int) lerpf(70, 30, 0f);
    }

    @Override
    public void onChargingStart(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        player.getAbilities().mayfly = true;
        player.onUpdateAbilities();
    }

    @Override
    public boolean onChargingTick(ServerPlayer player, PlayerAbilityData data, int ticks) {
        float exp = data.getProficiency(getId());
        int chargeTime = (int) lerpf(70, 30, exp);

        if (ticks >= chargeTime) {
            // 进入飞行模式
            performFlightTick(player, data);
        }
        return true; // 持续能力，用户手动取消
    }

    @Override
    public void onChargingRelease(ServerPlayer player, PlayerAbilityData data, int ticks) {
        // StormWing是toggle能力，release时进入飞行模式或取消
        float exp = data.getProficiency(getId());
        int chargeTime = (int) lerpf(70, 30, exp);

        if (ticks < chargeTime) return;

        // 激活飞行
        performActivate(player, data);
    }

    @Override
    public void onChargingAbort(ServerPlayer player, PlayerAbilityData data) {
        // 取消飞行
        player.getAbilities().mayfly = false;
        player.getAbilities().flying = false;
        player.onUpdateAbilities();
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
    }

    private void performActivate(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        ServerLevel level = player.serverLevel();

        player.getAbilities().mayfly = true;
        player.getAbilities().flying = true;
        player.onUpdateAbilities();

        // 熟练度=1 时推开周围实体
        if (exp >= 1.0f) {
            for (Entity e : level.getEntities(player,
                    new AABB(player.getX() - 6, player.getY() - 6, player.getZ() - 6,
                            player.getX() + 6, player.getY() + 6, player.getZ() + 6),
                    ent -> ent != player && ent.isAlive())) {
                Vec3 delta = e.position().subtract(player.position()).normalize()
                        .scale(0.5 + level.random.nextFloat() * 0.5);
                e.setDeltaMovement(delta.x, delta.y, delta.z);
                e.hurtMarked = true;
            }
        }

        EffectHelper.windBurst(level, player.getX(), player.getY(), player.getZ(), 50, 3);
        AcademySounds.playSound(level, player.getX(), player.getY(), player.getZ(),
                AcademySounds.VM_STORM_WING, SoundSource.PLAYERS, 1.0f, 0.5f);
    }

    private void performFlightTick(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        float cp = lerpf(40, 25, exp);
        float overload = lerpf(10, 7, exp);

        if (!data.isDevMode()) {
            if (data.getCurrentCp() < cp) return;
            data.setCurrentCp(data.getCurrentCp() - cp);
            data.addOverload(overload);
        }

        data.addProficiency(getId(), 0.00005f);
        player.fallDistance = 0;

        // 熟练度<0.15 时破坏周围弱方块
        if (exp < 0.15f) {
            ServerLevel level = player.serverLevel();
            for (int i = 0; i < 40; i++) {
                int dx = level.random.nextIntBetweenInclusive(-10, 10);
                int dy = level.random.nextIntBetweenInclusive(-10, 10);
                int dz = level.random.nextIntBetweenInclusive(-10, 10);
                BlockPos pos = player.blockPosition().offset(dx, dy, dz);
                BlockState state = level.getBlockState(pos);
                float hardness = state.getDestroySpeed(level, pos);
                if (hardness >= 0 && hardness <= 0.3f && !state.isAir()) {
                    BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(level, pos, state, player);
                    NeoForge.EVENT_BUS.post(breakEvent);
                    if (!breakEvent.isCanceled()) {
                        level.destroyBlock(pos, false);
                    }
                }
            }
        }
    }

    @Override
    public int getCooldownTicks(float proficiency) {
        return (int) lerpf(40, 20, proficiency);
    }
}
