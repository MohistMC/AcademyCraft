package com.mohistmc.academy.skill.ability.vecmanip;

import com.mohistmc.academy.skill.ChargingSkillEffect;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.world.AcademySounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/**
 * 矢量加速 —— 蓄力后向前方高速冲刺
 * <p>
 * 参考旧代码 VecAccel.scala：
 * - 最大速度 2.5，蓄力最多20tick
 * - 速度曲线: sin(lerp(0.4, 1, clamp(0, 1, tick/20))) * 2.5
 * - 熟练度>0.5 可在空中使用
 *
 * @author Mgazul
 */
public class VecAccelEffect implements ChargingSkillEffect {

    private static final int MAX_CHARGE = 20;
    private static final double MAX_VELOCITY = 2.5;

    @Override
    public String getId() {
        return "vec_accel";
    }

    @Override
    public int getMinChargeTicks() {
        return 0;
    }

    @Override
    public int getMaxChargeTicks() {
        return MAX_CHARGE;
    }

    @Override
    public void onChargingStart(ServerPlayer player, PlayerAbilityData data) {
        // 不预先消耗
    }

    @Override
    public boolean onChargingTick(ServerPlayer player, PlayerAbilityData data, int ticks) {
        return ticks <= MAX_CHARGE;
    }

    @Override
    public void onChargingRelease(ServerPlayer player, PlayerAbilityData data, int ticks) {
        float exp = data.getProficiency(getId());

        // 检查是否在地面（熟练度<=0.5时要求地面）
        boolean ignoreGround = exp > 0.5f;
        if (!ignoreGround && !player.onGround()) {
            return;
        }

        float cp = lerpf(120, 80, exp);
        float overload = lerpf(30, 15, exp);

        if (!data.isDevMode()) {
            if (data.getCurrentCp() < cp) return;
            data.setCurrentCp(data.getCurrentCp() - cp);
            data.addOverload(overload);
        }

        // 计算速度
        int effectiveTicks = Math.min(ticks, MAX_CHARGE);
        double prog = Math.max(0, Math.min(1, effectiveTicks / (double) MAX_CHARGE));
        double speed = Math.sin(lerpf(0.4f, 1.0f, (float) prog)) * MAX_VELOCITY;

        // 方向：玩家视角方向，略微向下
        Vec3 dir = player.getLookAngle().scale(speed);
        dir = new Vec3(dir.x, dir.y - 0.2 * speed, dir.z);

        player.setDeltaMovement(dir);
        player.hurtMarked = true;
        player.fallDistance = 0;
        if (player.getVehicle() != null) {
            player.stopRiding();
        }

        data.addProficiency(getId(), 0.002f);

        ServerLevel level = player.serverLevel();
        AcademySounds.playSound(level, player.getX(), player.getY(), player.getZ(),
                AcademySounds.VM_VEC_ACCEL, SoundSource.PLAYERS, 0.35f, 1.0f);
    }

    @Override
    public void onChargingAbort(ServerPlayer player, PlayerAbilityData data) {
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
    }

    @Override
    public int getCooldownTicks(float proficiency) {
        return (int) lerpf(20, 10, proficiency);
    }
}
