package com.mohistmc.academy.skill.ability.electromaster;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import com.mohistmc.academy.world.AcademyEntities;
import com.mohistmc.academy.world.entity.OreHighlightEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;

public class MineDetectEffect implements SkillEffect {

    private static final int TIME = 100;

    @Override
    public String getId() {
        return "mine_detect";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float proficiency = data.getProficiency(getId());
        int range = (int) (15 + proficiency * 15);
        boolean advanced = proficiency > 0.5f;

        ServerLevel level = player.serverLevel();
        BlockPos playerPos = player.blockPosition();

        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                for (int dz = -range; dz <= range; dz++) {
                    BlockPos pos = playerPos.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(pos);
                    if (isOre(state)) {
                        int harvestLevel = advanced ? getHarvestLevel(state) : 0;
                        OreHighlightEntity entity = new OreHighlightEntity(
                                AcademyEntities.ORE_HIGHLIGHT.get(), level);
                        // 实体放在方块中心
                        entity.setPos(pos.getX(), pos.getY(), pos.getZ());
                        entity.setData(harvestLevel, range);
                        level.addFreshEntity(entity);
                    }
                }
            }
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

    private int getHarvestLevel(BlockState state) {
        if (state.is(BlockTags.INCORRECT_FOR_DIAMOND_TOOL)) return 4;
        if (state.is(BlockTags.INCORRECT_FOR_IRON_TOOL)) return 3;
        if (state.is(BlockTags.INCORRECT_FOR_STONE_TOOL)) return 2;
        if (state.is(BlockTags.INCORRECT_FOR_WOODEN_TOOL)) return 1;
        return 0;
    }
}
