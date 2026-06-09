package com.mohistmc.academy.skill.ability.teleporter;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/**
 * 威胁传送 —— 向前方短距离传送，对落点周围实体造成伤害
 */
public class ThreateningTeleportEffect implements SkillEffect {

    @Override
    public String getId() {
        return "threatening_teleport";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        double range = lerpf(5.0f, 10.0f, exp);
        float damage = lerpf(3.0f, 8.0f, exp);

        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();

        BlockPos target = findSafePos(player, eyePos, lookVec, range);
        if (target == null) return;

        ServerLevel level = player.serverLevel();

        level.sendParticles(ParticleTypes.PORTAL,
                player.getX(), player.getY() + player.getBbHeight() / 2, player.getZ(),
                20, 0.5, 0.5, 0.5, 0.1);

        player.teleportTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5);

        level.sendParticles(ParticleTypes.PORTAL,
                player.getX(), player.getY() + player.getBbHeight() / 2, player.getZ(),
                20, 0.5, 0.5, 0.5, 0.1);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);

        AABB area = new AABB(
                player.getX() - 3, player.getY() - 3, player.getZ() - 3,
                player.getX() + 3, player.getY() + 3, player.getZ() + 3
        );
        for (Entity e : level.getEntities(player, area, Entity::isAlive)) {
            if (e instanceof LivingEntity living && e != player) {
                living.hurt(player.damageSources().playerAttack(player), damage);
            }
        }

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }

    private BlockPos findSafePos(ServerPlayer player, Vec3 start, Vec3 dir, double maxRange) {
        ServerLevel level = player.serverLevel();
        for (double d = 0.5; d <= maxRange; d += 0.5) {
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
