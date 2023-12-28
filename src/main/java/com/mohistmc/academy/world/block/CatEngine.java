package com.mohistmc.academy.world.block;

import com.mohistmc.academy.client.block.entity.CatEngineBlockEntity;
import com.mohistmc.academy.world.AcademyBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class CatEngine extends BaseEntityBlock {

    public static final MapCodec<CatEngine> CODEC = simpleCodec(CatEngine::new);
    public CatEngine(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<CatEngine> codec() {
        return CODEC;
    }

    @Override
    public void animateTick(BlockState p_220827_, Level p_220828_, BlockPos p_220829_, RandomSource p_220830_) {

    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new CatEngineBlockEntity(p_153215_, p_153216_);
    }

    @Override
    public InteractionResult use(BlockState p_60503_, Level level, BlockPos pos, Player p_60506_, InteractionHand p_60507_, BlockHitResult p_60508_) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof CatEngineBlockEntity blockEntity) {
            blockEntity.enable = !blockEntity.enable;
            // TODO: 自动链接到周围节点
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        return p_153212_.isClientSide() ? createTickerHelper(p_153214_, AcademyBlockEntities.CAT_ENGINE.get(), CatEngineBlockEntity::tickAnim) : null;
    }

}
