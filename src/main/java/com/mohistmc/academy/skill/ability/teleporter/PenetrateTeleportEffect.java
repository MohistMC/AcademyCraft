package com.mohistmc.academy.skill.ability.teleporter;

import com.mohistmc.academy.client.effect.EffectHelper;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import com.mohistmc.academy.world.AcademySounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/**
 * 穿透传送 —— 穿透墙壁向前方中距离传送
 */
public class PenetrateTeleportEffect implements SkillEffect {

    @Override
    public String getId() {
        return "penetrate_teleport";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        double range = lerpf(10.0f, 20.0f, exp);
        float damage = lerpf(2.0f, 5.0f, exp);

        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();

        Vec3 targetVec = eyePos.add(lookVec.scale(range));
        BlockPos target = findSafePosBelow(player, targetVec);

        ServerLevel level = player.serverLevel();

        // 对路径上的实体造成伤害
        double minX = Math.min(player.getX(), target.getX() + 0.5);
        double minY = Math.min(player.getY(), target.getY());
        double minZ = Math.min(player.getZ(), target.getZ() + 0.5);
        double maxX = Math.max(player.getX(), target.getX() + 0.5);
        double maxY = Math.max(player.getY(), target.getY() + 1);
        double maxZ = Math.max(player.getZ(), target.getZ() + 0.5);
        AABB pathArea = new AABB(minX, minY, minZ, maxX, maxY, maxZ).inflate(1.0);
        for (Entity e : level.getEntities(player, pathArea, Entity::isAlive)) {
            if (e instanceof LivingEntity living && e != player) {
                living.hurt(player.damageSources().playerAttack(player), damage);
            }
        }

        EffectHelper.teleportBurst(level, player.getX(), player.getY() + player.getBbHeight() / 2, player.getZ(), 20);

        player.teleportTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5);

        EffectHelper.teleportBurst(level, player.getX(), player.getY() + player.getBbHeight() / 2, player.getZ(), 20);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                AcademySounds.TP_TP, SoundSource.PLAYERS, 1.0f, 1.0f);

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }

    private BlockPos findSafePosBelow(ServerPlayer player, Vec3 targetVec) {
        ServerLevel level = player.serverLevel();
        BlockPos bp = BlockPos.containing(targetVec.x, targetVec.y, targetVec.z);
        for (int y = bp.getY(); y > level.getMinBuildHeight(); y--) {
            BlockPos check = new BlockPos(bp.getX(), y, bp.getZ());
            if (isSafe(level, check)) {
                return check;
            }
        }
        for (int y = bp.getY(); y < level.getMaxBuildHeight(); y++) {
            BlockPos check = new BlockPos(bp.getX(), y, bp.getZ());
            if (isSafe(level, check)) {
                return check;
            }
        }
        return player.blockPosition();
    }

    private boolean isSafe(ServerLevel level, BlockPos pos) {
        return level.isEmptyBlock(pos) && level.isEmptyBlock(pos.above()) && !level.isEmptyBlock(pos.below());
    }

    @Override
    public int getCooldownTicks(float proficiency) {
        return (int) lerpf(50, 30, proficiency);
    }
}
