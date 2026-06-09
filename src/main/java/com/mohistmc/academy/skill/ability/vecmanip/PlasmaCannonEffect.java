package com.mohistmc.academy.skill.ability.vecmanip;

import com.mohistmc.academy.skill.ChargingSkillEffect;
import com.mohistmc.academy.skill.PlayerAbilityData;
import net.minecraft.core.BlockPos;
import com.mohistmc.academy.client.effect.EffectHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import com.mohistmc.academy.client.sound.AcademySounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import static com.mohistmc.academy.utils.MathUtils.lerpf;

/**
 * 等离子炮 —— 蓄力后发射高能等离子球，在目标位置产生大爆炸
 * <p>
 * 参考旧代码 PlasmaCannon.scala：
 * - 蓄力时间：lerpf(60, 30, exp) ticks
 * - 爆炸伤害：lerpf(80, 150, exp)，爆炸半径：lerpf(12, 15, exp)
 * - 冷却：lerpf(1000, 600, exp) ticks
 *
 * @author Mgazul
 */
public class PlasmaCannonEffect implements ChargingSkillEffect {

    @Override
    public String getId() {
        return "plasma_cannon";
    }

    @Override
    public int getMinChargeTicks() {
        return (int) lerpf(60, 30, 0f); // 最低蓄力 = 低熟练度时的蓄力值
    }

    @Override
    public int getMaxChargeTicks() {
        return (int) lerpf(60, 30, 0f); // 固定蓄力时间（由 proficiency 决定）
    }

    @Override
    public void onChargingStart(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        float overload = lerpf(500, 400, exp);
        if (!data.isDevMode()) {
            data.addOverload(overload);
        }
    }

    @Override
    public boolean onChargingTick(ServerPlayer player, PlayerAbilityData data, int ticks) {
        float exp = data.getProficiency(getId());
        int chargeTime = (int) lerpf(60, 30, exp);
        float cp = lerpf(18, 25, exp);

        if (!data.isDevMode()) {
            if (data.getCurrentCp() < cp) return false;
            data.setCurrentCp(data.getCurrentCp() - cp);
        }

        if (ticks == chargeTime) {
            // 蓄力完成提示音
            ServerLevel level = player.serverLevel();
            AcademySounds.playSound(level, player.getX(), player.getY(), player.getZ(),
                    AcademySounds.VM_PLASMA_CANNON_T, SoundSource.PLAYERS, 0.5f, 2.0f);
        }

        return ticks <= chargeTime;
    }

    @Override
    public void onChargingRelease(ServerPlayer player, PlayerAbilityData data, int ticks) {
        float exp = data.getProficiency(getId());
        int chargeTime = (int) lerpf(60, 30, exp);

        if (ticks < chargeTime) {
            return; // 蓄力不足
        }

        data.addProficiency(getId(), 0.008f);

        ServerLevel level = player.serverLevel();

        // 目标位置：玩家视线方向100格
        Vec3 lookDir = player.getLookAngle();
        Vec3 destination = player.getEyePosition().add(lookDir.scale(100));

        // 检查方块碰撞
        var blockHit = player.pick(100, 0, false);
        if (blockHit != null) {
            destination = blockHit.getLocation();
        }

        // 爆炸
        float damage = lerpf(80, 150, exp);
        float radius = lerpf(12, 15, exp);

        AABB area = new AABB(
                destination.x - radius, destination.y - radius, destination.z - radius,
                destination.x + radius, destination.y + radius, destination.z + radius
        );

        for (Entity e : level.getEntities(player, area, Entity::isAlive)) {
            if (e instanceof LivingEntity living) {
                living.hurt(player.damageSources().explosion(player, player), damage);
                living.hurtMarked = true;
                living.invulnerableTime = 0;
            }
        }

        // 爆炸视觉效果和粒子
        EffectHelper.glowBurst(level, destination.x, destination.y, destination.z, 10, 0.6f, 0xAAFF8822, 15, radius / 2);
        EffectHelper.glowBurst(level, destination.x, destination.y, destination.z, 5, 0.5f, 0xFFFFFFFF, 8, 0.1f);

        AcademySounds.playSound(level, destination.x, destination.y, destination.z,
                AcademySounds.VM_PLASMA_CANNON, SoundSource.PLAYERS, 1.5f, 0.5f);
    }

    @Override
    public void onChargingAbort(ServerPlayer player, PlayerAbilityData data) {
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
    }
}
