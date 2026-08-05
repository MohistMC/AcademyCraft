package com.mohistmc.academy.skill.ability.electromaster;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import com.mohistmc.academy.world.AcademyEntities;
import com.mohistmc.academy.world.AcademySounds;
import com.mohistmc.academy.world.entity.OreHighlightEntity;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.state.BlockState;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/** 矿物探测 —— 致盲玩家，高亮显示周围矿物位置 */
public class MineDetectEffect implements SkillEffect {

    private static final int BLIND_TIME = 100;
    private static final int MAX_ORES = 100; // 限制最大矿石实体数量

    @Override
    public String getId() {
        return "mine_detect";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float proficiency = data.getProficiency(getId());
        int range = (int) lerpf(15, 30, proficiency);
        boolean advanced = proficiency > 0.5f && data.getPlayerLevel() >= 4;

        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, BLIND_TIME, 0));

        ServerLevel level = player.serverLevel();
        AcademySounds.playSound(level, player.getX(), player.getY(), player.getZ(),
                AcademySounds.EM_MINEDETECT, SoundSource.PLAYERS, 0.5f, 1.0f);

        BlockPos playerPos = player.blockPosition();
        List<OreHighlightEntity> spawnedOres = new ArrayList<>();

        for (int dx = -range; dx <= range && spawnedOres.size() < MAX_ORES; dx++) {
            for (int dy = -range; dy <= range && spawnedOres.size() < MAX_ORES; dy++) {
                for (int dz = -range; dz <= range && spawnedOres.size() < MAX_ORES; dz++) {
                    BlockPos pos = playerPos.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(pos);
                    if (isOre(state)) {
                        int harvestLevel = advanced ? getHarvestLevel(state) : 0;
                        OreHighlightEntity entity = new OreHighlightEntity(
                                AcademyEntities.ORE_HIGHLIGHT.get(), level);
                        entity.setPos(pos.getX(), pos.getY(), pos.getZ());
                        entity.setData(harvestLevel, range);
                        level.addFreshEntity(entity);
                        spawnedOres.add(entity);
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

    /**
     * 获取矿石的挖掘等级索引（用于颜色区分）
     */
    private int getHarvestLevel(BlockState state) {
        if (state.is(BlockTags.INCORRECT_FOR_DIAMOND_TOOL)) return 4;
        if (state.is(BlockTags.INCORRECT_FOR_IRON_TOOL)) return 3;
        if (state.is(BlockTags.INCORRECT_FOR_STONE_TOOL)) return 2;
        if (state.is(BlockTags.INCORRECT_FOR_WOODEN_TOOL)) return 1;
        return 0;
    }
}
