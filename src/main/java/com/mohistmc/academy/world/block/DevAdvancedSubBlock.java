package com.mohistmc.academy.world.block;

import com.mohistmc.academy.client.block.entity.DevAdvancedBlockEntity;
import com.mohistmc.academy.client.block.entity.DevAdvancedSubBlockEntity;
import com.mohistmc.academy.client.block.entity.DevNormalSubBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

public class DevAdvancedSubBlock extends BaseEntityBlock {

    public DevAdvancedSubBlock() {
        super(Properties.of(Material.STONE)
                .sound(SoundType.STONE)
                .noOcclusion()
                .strength(4.0f)
                .requiresCorrectToolForDrops()
        );
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighbor, boolean p_60514_) {
        //Block block = level.getBlockState(pos).getBlock();
        if ((block instanceof DevAdvancedSubBlock || block instanceof DevAdvanced) && level.getBlockState(neighbor).getBlock() instanceof AirBlock) {
            level.destroyBlock(pos, false);
        }
        super.neighborChanged(state, level, pos, block, neighbor, p_60514_);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new DevAdvancedSubBlockEntity(p_153215_, p_153216_);
    }


}
