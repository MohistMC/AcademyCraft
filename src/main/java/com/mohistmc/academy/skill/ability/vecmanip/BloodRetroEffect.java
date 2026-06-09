package com.mohistmc.academy.skill.ability.vecmanip;

import com.mohistmc.academy.skill.ChargingSkillEffect;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.client.effect.EffectHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import com.mohistmc.academy.world.AcademySounds;
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
 * 血液回流 —— 操纵目标血液，造成高额伤害
 * <p>
 * 参考旧代码 BloodRetrograde.scala：
 * - 近战范围2格，自动锁定目标
 * - 蓄力时减慢玩家移速，30tick后自动释放
 * - 高额伤害：lerpf(30, 60, exp)
 *
 * @author Mgazul
 */
public class BloodRetroEffect implements ChargingSkillEffect {

    private static final int AUTO_RELEASE_TICKS = 30;
    private static final double RANGE = 2.0;

    @Override
    public String getId() {
        return "blood_retro";
    }

    @Override
    public int getMinChargeTicks() {
        return 0;
    }

    @Override
    public int getMaxChargeTicks() {
        return AUTO_RELEASE_TICKS;
    }

    @Override
    public void onChargingStart(ServerPlayer player, PlayerAbilityData data) {
        // 减慢移速（模拟旧代码的 walkSpeed 降低）
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, AUTO_RELEASE_TICKS + 10, 2,
                false, false, true));
    }

    @Override
    public boolean onChargingTick(ServerPlayer player, PlayerAbilityData data, int ticks) {
        return ticks <= AUTO_RELEASE_TICKS;
    }

    @Override
    public void onChargingRelease(ServerPlayer player, PlayerAbilityData data, int ticks) {
        float exp = data.getProficiency(getId());
        float damage = lerpf(30, 60, exp);
        float cp = lerpf(280, 350, exp);
        float overload = lerpf(55, 40, exp);

        if (!data.isDevMode()) {
            if (data.getCurrentCp() < cp) return;
            data.setCurrentCp(data.getCurrentCp() - cp);
            data.addOverload(overload);
        }

        ServerLevel level = player.serverLevel();
        EntityHitResult hit = rayTraceEntity(player, RANGE);

        if (hit != null && hit.getEntity() instanceof LivingEntity target) {
            target.hurt(player.damageSources().playerAttack(player), damage);

            // 血液粒子效果
            for (int i = 0; i < 8; i++) {
                double ox = (level.random.nextDouble() - 0.5) * target.getBbWidth();
                double oy = level.random.nextDouble() * target.getBbHeight();
                double oz = (level.random.nextDouble() - 0.5) * target.getBbWidth();
                EffectHelper.bloodSplash(level, target.getX() + ox, target.getY() + oy, target.getZ() + oz, 5, 0.3f);
            }

            data.addProficiency(getId(), 0.002f);

            AcademySounds.playSound(level, target.getX(), target.getY(), target.getZ(),
                    AcademySounds.VM_BLOOD_RETRO, SoundSource.PLAYERS, 1.0f, 0.8f);
        }
    }

    @Override
    public void onChargingAbort(ServerPlayer player, PlayerAbilityData data) {
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
    }

    private EntityHitResult rayTraceEntity(ServerPlayer player, double range) {
        Vec3 start = player.getEyePosition();
        Vec3 end = start.add(player.getLookAngle().scale(range));
        List<Entity> entities = player.level().getEntities(player,
                player.getBoundingBox().inflate(range),
                e -> e.isAlive() && e.isPickable());

        Entity closest = null;
        double closestDist = Double.MAX_VALUE;
        Vec3 closestHit = null;

        for (Entity e : entities) {
            var result = e.getBoundingBox().inflate(0.3).clip(start, end);
            if (result.isPresent()) {
                double dist = start.distanceTo(result.get());
                if (dist < closestDist) {
                    closestDist = dist;
                    closest = e;
                    closestHit = result.get();
                }
            }
        }
        return closest != null ? new EntityHitResult(closest, closestHit) : null;
    }
}
