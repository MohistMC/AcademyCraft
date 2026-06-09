package com.mohistmc.academy.skill.ability.teleporter;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import net.minecraft.core.BlockPos;
import com.mohistmc.academy.client.effect.EffectHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import com.mohistmc.academy.world.AcademySounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/**
 * 位移传送 —— 向准星方向中距离位移传送
 */
public class ShiftTpEffect implements SkillEffect {

    @Override
    public String getId() {
        return "shift_tp";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        double range = lerpf(8.0f, 15.0f, exp);
        float damage = lerpf(5.0f, 12.0f, exp);

        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();

        BlockPos target = findSafePos(player, eyePos, lookVec, range);
        if (target == null) return;

        ServerLevel level = player.serverLevel();

        double minX = Math.min(player.getX(), target.getX() + 0.5);
        double minY = Math.min(player.getY(), target.getY());
        double minZ = Math.min(player.getZ(), target.getZ() + 0.5);
        double maxX = Math.max(player.getX(), target.getX() + 0.5);
        double maxY = Math.max(player.getY(), target.getY() + 1);
        double maxZ = Math.max(player.getZ(), target.getZ() + 0.5);
        AABB pathArea = new AABB(minX, minY, minZ, maxX, maxY, maxZ).inflate(1.5);
        for (Entity e : level.getEntities(player, pathArea, Entity::isAlive)) {
            if (e instanceof LivingEntity living && e != player) {
                living.hurt(player.damageSources().playerAttack(player), damage);
            }
        }

        EffectHelper.teleportBurst(level, player.getX(), player.getY() + player.getBbHeight() / 2, player.getZ(), 20);

        player.teleportTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5);

        EffectHelper.teleportBurst(level, player.getX(), player.getY() + player.getBbHeight() / 2, player.getZ(), 20);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                AcademySounds.TP_TP_SHIFT, SoundSource.PLAYERS, 1.0f, 1.0f);

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }

    private BlockPos findSafePos(ServerPlayer player, Vec3 start, Vec3 dir, double maxRange) {
        ServerLevel level = player.serverLevel();
        for (double d = maxRange; d >= 0.5; d -= 0.5) {
            Vec3 pos = start.add(dir.scale(d));
            BlockPos bp = BlockPos.containing(pos.x, pos.y, pos.z);
            if (isSafe(level, bp)) {
                return bp;
            }
        }
        return null;
    }

    private boolean isSafe(ServerLevel level, BlockPos pos) {
        return level.isEmptyBlock(pos) && level.isEmptyBlock(pos.above()) && !level.isEmptyBlock(pos.below());
    }
}
