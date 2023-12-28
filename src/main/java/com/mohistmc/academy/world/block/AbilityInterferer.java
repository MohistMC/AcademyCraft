package com.mohistmc.academy.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class AbilityInterferer extends Block {

    private static final IntegerProperty STATUS = IntegerProperty.create("status", 0, 1);

    public AbilityInterferer() {
        super(Properties.of()
                .sound(SoundType.STONE)
                .noOcclusion()
                .strength(3.0f)
                .requiresCorrectToolForDrops());
        this.registerDefaultState(this.getStateDefinition().any().setValue(STATUS, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
        p_49915_.add(STATUS);
        super.createBlockStateDefinition(p_49915_);
    }


    @Override
    public InteractionResult use(BlockState p_60503_, Level p_60504_, BlockPos p_60505_, Player player, InteractionHand p_60507_, BlockHitResult p_60508_) {

        // TODO: 打开GUI
        return InteractionResult.CONSUME;

    }
}
