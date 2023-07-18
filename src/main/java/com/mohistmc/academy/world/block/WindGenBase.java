package com.mohistmc.academy.world.block;

import com.mohistmc.academy.client.block.entity.AcademyContainerBlockEntity;
import com.mohistmc.academy.client.block.entity.WindGenBaseBlockEntity;
import com.mohistmc.academy.world.AcademyBlocks;
import com.mohistmc.academy.world.AcademyItems;
import com.mohistmc.academy.world.menu.WindGenBaseMenu;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WindGenBase extends BaseEntityBlock {

    private static final BooleanProperty ENABLE = BooleanProperty.create("enable");
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private boolean validBlock = false;
    private int mainHeight = 0;


    public WindGenBase() {
        super(Properties.of(Material.STONE)
                .sound(SoundType.STONE)
                .noOcclusion()
                .randomTicks()
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
            add(new ItemStack(AcademyItems.WINDGEN_BASE.get()));
        }};
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState p_60569_, boolean p_60570_) {
        Block subBlock = AcademyBlocks.WIND_GEN_BASE_SUB.get();
        level.setBlock(pos.above(1), subBlock.defaultBlockState(), 19);


    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        return (level, pos, p_155255_, p_155256_) -> {
            mainHeight = 0;
            for (int i = 2; i < 200; i++) {
                Block block = level.getBlockState(pos.above(i)).getBlock();
                if (block instanceof WindGenPillar) {
                    mainHeight++;
                    continue;
                } else if (block instanceof WindGenMain mainBlock) {
                    if (mainHeight > 4) {
                        this.validBlock = mainBlock.hasFan();
                        break;
                    }
                }
                this.validBlock = false;
                break;
            }

            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof WindGenBaseBlockEntity blockEntity) {
                blockEntity.tick(this.validBlock);
            }
        };
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        level.destroyBlock(pos.above(1), false);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand p_60507_, BlockHitResult p_60508_) {
        // TODO: 打开GUI
        //if (this.validBlock) {
        if (!level.isClientSide()) {
            player.openMenu(getMenuProvider(state, level, pos));
            return InteractionResult.CONSUME;
        }
        //  }
        return InteractionResult.PASS;

    }

    @Override
    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {

        return new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.empty();
            }

            @Override
            public AbstractContainerMenu createMenu(int p_39954_, Inventory inv, Player p_39956_) {
                return new WindGenBaseMenu(p_39954_, inv, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(pos));
            }
        };
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
    public void onRemove(BlockState p_60515_, Level world, BlockPos pos, BlockState p_60518_, boolean p_60519_) {
        if (!p_60515_.is(p_60518_.getBlock())) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof AcademyContainerBlockEntity blockEntity) {
                blockEntity
                        .getItems()
                        .forEach(item -> {
                            world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, item));
                        });
            }
            super.onRemove(p_60515_, world, pos, p_60518_, p_60519_);
        }

    }
}
