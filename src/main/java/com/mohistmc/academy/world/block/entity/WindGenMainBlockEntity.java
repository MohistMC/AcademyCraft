package com.mohistmc.academy.world.block.entity;

import com.mohistmc.academy.capability.IFEnergyStorage;
import com.mohistmc.academy.world.AcademyBlockEntities;
import com.mohistmc.academy.world.AcademyBlocks;
import com.mohistmc.academy.world.AcademyItems;
import com.mohistmc.academy.world.block.WindGenBase;
import com.mohistmc.academy.world.block.WindGenBaseSubBlock;
import com.mohistmc.academy.world.block.WindGenMain;
import com.mohistmc.academy.world.block.WindGenPillar;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WindGenMainBlockEntity extends AcademyContainerBlockEntity implements IFEnergyStorage {
    private static final int MAX_STORAGE = 1000;

    public WindGenMainBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(AcademyBlockEntities.WINDGEN_MAIN.get(), p_155229_, p_155230_);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    public void tick(WindGenMain block, Level level, BlockPos pos, Direction facing) {
        switch (facing) {
            case EAST -> checkFan(block, level, pos.east(1), facing);
            case WEST -> checkFan(block, level, pos.west(1), facing);
            case NORTH -> checkFan(block, level, pos.north(1), facing);
            case SOUTH -> checkFan(block, level, pos.south(1), facing);
        }
    }

    private void checkFan(WindGenMain block, Level level, BlockPos east, Direction facing) {
        BlockState state = level.getBlockState(east);
        if (!getItems().isEmpty() && getItems().get(0).is(AcademyItems.WINDGEN_FAN.get())) {
            if (!state.is(Blocks.AIR) && state.is(AcademyBlocks.WINDGEN_FAN.get())) {
                block.setValid(true);
                return;
            } else if (state.is(Blocks.AIR)) {
                level.setBlock(east, AcademyBlocks.WINDGEN_FAN.get()
                        .defaultBlockState()
                        .setValue(WindGenPillar.FACING, facing), 19);
                block.setValid(true);
                return;
            }
        } else {
            if (!state.is(Blocks.AIR) && state.is(AcademyBlocks.WINDGEN_FAN.get())) {
                level.destroyBlock(east, false);
            }
        }
        block.setValid(false);
    }

    public void destroy(Level world, BlockPos pos) {
        destroyFan(world, pos.east(1));
        destroyFan(world, pos.west(1));
        destroyFan(world, pos.south(1));
        destroyFan(world, pos.north(1));
    }

    private void destroyFan(Level world, BlockPos east) {
        BlockState state = world.getBlockState(east);
        if (state.is(AcademyBlocks.WINDGEN_FAN.get())) {
            world.destroyBlock(east, false);
        }
    }

    @Override
    public int getEnergyStored() {
        WindGenBaseBlockEntity base = findBase();
        return base != null ? base.getEnergyStored() : 0;
    }

    @Override
    public int getMaxEnergyStored() {
        WindGenBaseBlockEntity base = findBase();
        return base != null ? base.getMaxEnergyStored() : MAX_STORAGE;
    }

    @Override
    public void setEnergy(int energy) {
        WindGenBaseBlockEntity base = findBase();
        if (base != null) {
            base.setEnergy(energy);
        }
    }

    private WindGenBaseBlockEntity findBase() {
        if (level == null) return null;
        BlockPos pos = getBlockPos();
        for (int i = 1; i < 200; i++) {
            BlockPos below = pos.below(i);
            BlockEntity be = level.getBlockEntity(below);
            if (be instanceof WindGenBaseBlockEntity) {
                return (WindGenBaseBlockEntity) be;
            }
            Block block = level.getBlockState(below).getBlock();
            if (!(block instanceof WindGenPillar) && !(block instanceof WindGenBase) && !(block instanceof WindGenBaseSubBlock)) {
                break;
            }
        }
        return null;
    }
}
