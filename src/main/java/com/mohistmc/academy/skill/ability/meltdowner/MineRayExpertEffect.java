package com.mohistmc.academy.skill.ability.meltdowner;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import net.minecraft.core.BlockPos;
import com.mohistmc.academy.client.effect.EffectHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import com.mohistmc.academy.client.sound.AcademySounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/**
 * 矿物射线(专家) —— 更高级的矿物射线，可同时破坏多个矿物
 */
public class MineRayExpertEffect implements SkillEffect {

    private static final double RANGE = 20.0;
    private static final int MAX_BLOCKS = 5;

    @Override
    public String getId() {
        return "mine_ray_expert";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        double range = lerpf(15.0f, 20.0f, exp);
        int maxBlocks = (int) lerpf(2, MAX_BLOCKS, exp);

        ServerLevel level = player.serverLevel();
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();

        int destroyed = 0;
        for (double d = 1.0; d <= range && destroyed < maxBlocks; d += 0.5) {
            Vec3 checkPos = eyePos.add(lookVec.scale(d));
            BlockPos bp = BlockPos.containing(checkPos.x, checkPos.y, checkPos.z);
            BlockState state = level.getBlockState(bp);

            if (isOre(state)) {
                EffectHelper.meltdownBurst(level, checkPos.x, checkPos.y, checkPos.z, 2, 0.2);
                level.destroyBlock(bp, true, player);
                destroyed++;
            }
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                AcademySounds.MD_RAY_SMALL, SoundSource.PLAYERS, 0.5f, 1.5f);

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }

    private boolean isOre(BlockState state) {
        return state.is(BlockTags.COAL_ORES)
                || state.is(BlockTags.IRON_ORES)
                || state.is(BlockTags.COPPER_ORES)
                || state.is(BlockTags.GOLD_ORES)
                || state.is(BlockTags.REDSTONE_ORES)
                || state.is(BlockTags.LAPIS_ORES)
                || state.is(BlockTags.DIAMOND_ORES)
                || state.is(BlockTags.EMERALD_ORES);
    }
}
