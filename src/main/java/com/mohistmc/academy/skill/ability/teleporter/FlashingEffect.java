package com.mohistmc.academy.skill.ability.teleporter;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import com.mohistmc.academy.client.sound.AcademySounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/**
 * 闪烁 —— 短距离快速传送，获得短暂抗性
 */
public class FlashingEffect implements SkillEffect {

    @Override
    public String getId() {
        return "flashing";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        double range = lerpf(3.0f, 8.0f, exp);

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
                AcademySounds.TP_TP_FLASHING, SoundSource.PLAYERS, 1.0f, 1.0f);

        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 10, 4));

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
