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

    public WindGenMainBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(AcademyBlockEntities.WINDGEN_MAIN.get(), p_155229_, p_155230_);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    public void tick(WindGenMain block, Level level, BlockPos pos, Direction facing) {
        boolean hasBase = findBase() != null;

        BlockPos fanPos = switch (facing) {
            case EAST -> pos.east(1);
            case WEST -> pos.west(1);
            case NORTH -> pos.north(1);
            case SOUTH -> pos.south(1);
            default -> pos;
        };

        BlockEntity fanEntity = level.getBlockEntity(fanPos);
        if (fanEntity instanceof WindGenFanBlockEntity fan) {
            boolean hasFanBlock = level.getBlockState(fanPos).is(AcademyBlocks.WINDGEN_FAN.get());
            fan.isRunning = hasFanBlock && hasBase;
            fan.setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(fanPos, fan.getBlockState(), fan.getBlockState(), Block.UPDATE_ALL);
            }
        }

        if (!level.isClientSide()) {
            if (!hasBase) {
                block.setValid(false);
                return;
            }

            switch (facing) {
                case EAST -> checkFan(block, level, pos.east(1), facing);
                case WEST -> checkFan(block, level, pos.west(1), facing);
                case NORTH -> checkFan(block, level, pos.north(1), facing);
                case SOUTH -> checkFan(block, level, pos.south(1), facing);
            }
        }
    }

    private void checkFan(WindGenMain block, Level level, BlockPos fanPos, Direction facing) {
        BlockState state = level.getBlockState(fanPos);
        if (!getItems().isEmpty() && getItems().get(0).is(AcademyItems.WINDGEN_FAN.get())) {
            if (!state.is(Blocks.AIR) && state.is(AcademyBlocks.WINDGEN_FAN.get())) {
                block.setValid(true);
                return;
            } else if (state.is(Blocks.AIR)) {
                if (checkFanSpace(level, fanPos, facing)) {
                    level.setBlock(fanPos, AcademyBlocks.WINDGEN_FAN.get()
                            .defaultBlockState()
                            .setValue(WindGenPillar.FACING, facing), 19);
                    block.setValid(true);
                } else {
                    block.setValid(false);
                }
                return;
            }
        } else {
            if (!state.is(Blocks.AIR) && state.is(AcademyBlocks.WINDGEN_FAN.get())) {
                level.destroyBlock(fanPos, false);
            }
        }
        block.setValid(false);
    }

    private boolean checkFanSpace(Level level, BlockPos pos, Direction facing) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        if (facing == Direction.EAST || facing == Direction.WEST) {
            for (int dy = -3; dy <= 3; dy++) {
                for (int dz = -3; dz <= 3; dz++) {
                    BlockPos checkPos = new BlockPos(x, y + dy, z + dz);
                    BlockState state = level.getBlockState(checkPos);
                    if (!state.isAir() && !state.is(AcademyBlocks.WINDGEN_FAN.get())) {
                        return false;
                    }
                }
            }
        } else {
            for (int dy = -3; dy <= 3; dy++) {
                for (int dx = -3; dx <= 3; dx++) {
                    BlockPos checkPos = new BlockPos(x + dx, y + dy, z);
                    BlockState state = level.getBlockState(checkPos);
                    if (!state.isAir() && !state.is(AcademyBlocks.WINDGEN_FAN.get())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void destroy(Level world, BlockPos pos, Direction facing) {
        switch (facing) {
            case EAST -> destroyFan(world, pos.east(1));
            case WEST -> destroyFan(world, pos.west(1));
            case NORTH -> destroyFan(world, pos.north(1));
            case SOUTH -> destroyFan(world, pos.south(1));
        }
    }

    private void destroyFan(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.is(AcademyBlocks.WINDGEN_FAN.get())) {
            world.destroyBlock(pos, false);
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
        return base != null ? base.getMaxEnergyStored() : 0;
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
