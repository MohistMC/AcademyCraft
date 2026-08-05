package com.mohistmc.academy.skill.ability.vecmanip;

import com.mohistmc.academy.skill.ChargingSkillEffect;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.world.AcademySounds;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/** 定向冲击 —— 蓄力后向前猛击，击退目标并造成伤害 */
public class DirShockEffect implements ChargingSkillEffect {

    private static final int MIN_TICKS = 6;
    private static final int MAX_TICKS = 50;
    private static final int MAX_TOLERANT_TICKS = 200;
    private static final double RANGE = 3.0;

    @Override
    public String getId() {
        return "dir_shock";
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
        // 不预先消耗，释放时才消耗
    }

    @Override
    public boolean onChargingTick(ServerPlayer player, PlayerAbilityData data, int ticks) {
        return ticks <= MAX_TOLERANT_TICKS;
    }

    @Override
    public void onChargingRelease(ServerPlayer player, PlayerAbilityData data, int ticks) {
        if (ticks < MIN_TICKS || ticks > MAX_TICKS) {
            return;
        }

        float exp = data.getProficiency(getId());
        float damage = lerpf(7, 15, exp);
        float cp = lerpf(50, 100, exp);
        float overload = lerpf(18, 12, exp);

        if (!data.isDevMode()) {
            if (data.getCurrentCp() < cp) return;
            data.setCurrentCp(data.getCurrentCp() - cp);
            data.addOverload(overload);
        }

        ServerLevel level = player.serverLevel();
        EntityHitResult hit = rayTraceEntities(player, RANGE);

        if (hit != null && hit.getEntity() instanceof LivingEntity target) {
            target.hurt(player.damageSources().playerAttack(player), damage);
            knockback(player, target, exp);
            data.addProficiency(getId(), 0.0035f);

            AcademySounds.playSound(level, target.getX(), target.getY(), target.getZ(),
                    AcademySounds.VM_DIRECTED_SHOCK, SoundSource.PLAYERS, 0.5f, 1.0f);
        } else {
            data.addProficiency(getId(), 0.0010f);
            AcademySounds.playSound(level, player.getX(), player.getY(), player.getZ(),
                    AcademySounds.VM_DIRECTED_SHOCK, SoundSource.PLAYERS, 0.5f, 1.0f);
        }
    }

    @Override
    public void onChargingAbort(ServerPlayer player, PlayerAbilityData data) {
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
    }

    private void knockback(ServerPlayer player, Entity target, float exp) {
        if (exp < 0.25f) return;

        Vec3 delta = target.getEyePosition().subtract(player.getEyePosition()).normalize();
        delta = new Vec3(delta.x, delta.y - 0.6, delta.z).normalize();

        target.setPos(target.getX(), target.getY() + 0.1, target.getZ());
        target.setDeltaMovement(delta.x * -0.7, delta.y * -0.7, delta.z * -0.7);
        target.hurtMarked = true;
    }

    private EntityHitResult rayTraceEntities(ServerPlayer player, double range) {
        Vec3 start = player.getEyePosition();
        Vec3 end = start.add(player.getLookAngle().scale(range));
        AABB area = player.getBoundingBox().inflate(range);
        List<Entity> entities = player.level().getEntities(player, area,
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

    @Override
    public int getCooldownTicks(float proficiency) {
        return (int) lerpf(30, 15, proficiency);
    }
}
