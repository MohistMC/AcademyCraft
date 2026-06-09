package com.mohistmc.academy.skill.ability.electromaster;

import com.mohistmc.academy.skill.ChargingSkillEffect;
import com.mohistmc.academy.skill.PlayerAbilityData;
import java.util.List;
import com.mohistmc.academy.client.effect.EffectHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class ThunderClapEffect implements ChargingSkillEffect {

    private static final int MIN_TICKS = 40;
    private static final int MAX_TICKS = 60;

    @Override
    public String getId() {
        return "thunder_clap";
    }

    @Override
    public int getMinChargeTicks() {
        return MIN_TICKS;
    }

    @Override
    public int getMaxChargeTicks() {
        return MAX_TICKS;
    }

    @Override
    public void onChargingStart(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        float overload = lerpf(390, 252, exp);
        if (!data.isDevMode()) {
            data.addOverload(overload);
        }

        ServerLevel level = player.serverLevel();
        EffectHelper.arcSpark(level, player.getX(), player.getY() + 1, player.getZ(), 20, 0.5);
    }

    @Override
    public boolean onChargingTick(ServerPlayer player, PlayerAbilityData data, int ticks) {
        float exp = data.getProficiency(getId());
        float consumption = lerpf(18, 25, exp);

        if (!data.isDevMode()) {
            if (data.getCurrentCp() < consumption) {
                return false;
            }
            data.setCurrentCp(data.getCurrentCp() - consumption);
        }
        return true;
    }

    @Override
    public void onChargingRelease(ServerPlayer player, PlayerAbilityData data, int ticks) {
        ServerLevel level = player.serverLevel();
        float exp = data.getProficiency(getId());

        Vec3 targetPos = getTargetPos(player);

        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
        if (lightning != null) {
            lightning.moveTo(targetPos.x, targetPos.y, targetPos.z);
            lightning.setVisualOnly(false);
            level.addFreshEntity(lightning);
        }

        float damage = getDamage(exp, ticks);
        float range = getRange(exp);

        List<Entity> entities = level.getEntities(player,
                new AABB(targetPos.x - range, targetPos.y - range, targetPos.z - range,
                        targetPos.x + range, targetPos.y + range, targetPos.z + range),
                Entity::isAlive);

        for (Entity e : entities) {
            if (e instanceof LivingEntity target && e != player) {
                double dist = targetPos.distanceTo(e.position());
                if (dist <= range) {
                    target.hurt(player.damageSources().lightningBolt(), damage);
                }
            }
        }

        level.playSound(null, targetPos.x, targetPos.y, targetPos.z,
                SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 1.0f, 1.0f);

        data.addProficiency(getId(), 0.003f);
    }

    @Override
    public void onChargingAbort(ServerPlayer player, PlayerAbilityData data) {
        // 蓄力取消，不执行效果
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        // 蓄力技能通过 Charging 接口执行，此处留空
    }

    private Vec3 getTargetPos(ServerPlayer player) {
        BlockHitResult hit = (BlockHitResult) player.pick(40.0, 0, false);
        if (hit != null) {
            return Vec3.atCenterOf(hit.getBlockPos());
        }
        Vec3 eye = player.getEyePosition(0);
        Vec3 look = player.getLookAngle().scale(40.0);
        return eye.add(look);
    }

    private float lerpf(float a, float b, float x) {
        return a + (b - a) * x;
    }

    private float getDamage(float exp, int ticks) {
        return lerpf(36, 72, exp) * lerpf(1.0f, 1.2f, (ticks - 40.0f) / 60.0f);
    }

    private float getRange(float exp) {
        return lerpf(15, 30, exp);
    }
}
