package com.mohistmc.academy.world.block;

import com.mohistmc.academy.client.block.entity.WindGenBaseBlockEntity;
import com.mohistmc.academy.client.block.gui.WindBaseGui;
import com.mohistmc.academy.world.AcademyBlocks;
import com.mohistmc.academy.world.AcademyItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WindGenBase extends BaseEntityBlock {

    private static final BooleanProperty ENABLE = BooleanProperty.create("enable");
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private boolean validBlock = false;


    public WindGenBase() {
        super(Properties.of(Material.STONE)
                .sound(SoundType.STONE)
                .noOcclusion()
                .strength(4.0f)
                .requiresCorrectToolForDrops()
        );
        this.registerDefaultState(this.getStateDefinition().any().setValue(ENABLE, false).setValue(FACING, Direction.NORTH));

    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
        p_49915_.add(ENABLE, FACING);
        super.createBlockStateDefinition(p_49915_);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_49820_) {
        return this.defaultBlockState().setValue(FACING, p_49820_.getHorizontalDirection().getOpposite());
    }


    @Override
    public List<ItemStack> getDrops(BlockState p_60537_, LootContext.Builder p_60538_) {
        return new ArrayList<>() {{
            add(new ItemStack(AcademyItems.IMAG_FUSOR.get()));
        }};
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState p_60569_, boolean p_60570_) {
        Block subBlock = AcademyBlocks.WIND_GEN_BASE_SUB.get();
        level.setBlock(pos.above(1), subBlock.defaultBlockState(), 19);
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        level.destroyBlock(pos.above(1), false);
    }

    @Override
    public InteractionResult use(BlockState p_60503_, Level level, BlockPos pos, Player player, InteractionHand p_60507_, BlockHitResult p_60508_) {

        // TODO: 打开GUI
        if (this.validBlock) {
            if (level.isClientSide()) {
                Minecraft.getInstance().setScreen(new WindBaseGui(Component.empty()));
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;

    }


    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighbor, boolean p_60514_) {
        //Block block = level.getBlockState(pos).getBlock();
        if (block instanceof WindGenBaseSubBlock && level.getBlockState(neighbor).getBlock() instanceof AirBlock) {
            level.destroyBlock(pos, false);
        }
        super.neighborChanged(state, level, pos, block, neighbor, p_60514_);
    }


    @Override
    public BlockState rotate(BlockState p_48722_, Rotation p_48723_) {
        return p_48722_.setValue(FACING, p_48723_.rotate(p_48722_.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState p_48719_, Mirror p_48720_) {
        return p_48719_.rotate(p_48720_.getRotation(p_48719_.getValue(FACING)));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new WindGenBaseBlockEntity(p_153215_, p_153216_);
    }

    @Override
    public RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.MODEL;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource randomSource) {

        if (level.getBlockState(pos.above(2)).getBlock() instanceof WindGenPillar
                && level.getBlockState(pos.above(3)).getBlock() instanceof WindGenPillar
                && level.getBlockState(pos.above(4)).getBlock() instanceof WindGenPillar
                && level.getBlockState(pos.above(5)).getBlock() instanceof WindGenPillar
                && level.getBlockState(pos.above(6)).getBlock() instanceof WindGenMain) {
            // 满足条件
            this.validBlock = true;
        } else {
            this.validBlock = false;
        }

    }
}
