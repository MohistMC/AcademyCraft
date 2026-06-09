package com.mohistmc.academy.skill.effect;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MagMovementEffect implements SkillEffect {

    @Override
    public String getId() {
        return "mag_movement";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        ServerLevel level = player.serverLevel();
        float proficiency = data.getProficiency(getId());

        // 向前方寻找可磁化方块
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        double range = 20.0 + proficiency * 10.0;

        BlockPos targetPos = null;
        for (double d = 1.0; d <= range; d += 0.5) {
            Vec3 checkPos = eyePos.add(lookVec.scale(d));
            BlockPos pos = BlockPos.containing(checkPos.x, checkPos.y, checkPos.z);
            BlockState state = level.getBlockState(pos);
            if (isMagnetic(state)) {
                targetPos = pos;
                break;
            }
        }

        if (targetPos != null) {
            Vec3 targetVec = Vec3.atCenterOf(targetPos);
            Vec3 playerPos = player.position();
            Vec3 dir = targetVec.subtract(playerPos).normalize();
            double dist = playerPos.distanceTo(targetVec);

            // 计算速度
            double speed = Math.min(dist * 0.3, 3.0);
            Vec3 motion = dir.scale(speed);
            player.setDeltaMovement(motion.x, motion.y + 0.2, motion.z);
            player.hurtMarked = true;

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.IRON_GOLEM_REPAIR, SoundSource.PLAYERS, 0.5f, 1.0f);
        }
    }

    private boolean isMagnetic(BlockState state) {
        return state.is(Blocks.IRON_BLOCK) || state.is(Blocks.IRON_ORE) || state.is(Blocks.DEEPSLATE_IRON_ORE)
                || state.is(Blocks.RAW_IRON_BLOCK) || state.is(Blocks.ANVIL) || state.is(Blocks.HOPPER)
                || state.is(Blocks.PISTON) || state.is(Blocks.STICKY_PISTON)
                || state.is(Blocks.HEAVY_CORE) || state.is(Blocks.NETHERITE_BLOCK)
                || state.is(Blocks.GOLD_BLOCK) || state.is(Blocks.GOLD_ORE)
                || state.is(Blocks.DEEPSLATE_GOLD_ORE) || state.is(Blocks.RAW_GOLD_BLOCK);
    }
}
