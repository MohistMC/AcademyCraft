package com.mohistmc.academy.client.block.entity;

import com.mohistmc.academy.world.AcademyBlockEntities;
import com.mohistmc.academy.world.AcademyBlocks;
import com.mohistmc.academy.world.AcademyItems;
import com.mohistmc.academy.world.block.WindGenMain;
import com.mohistmc.academy.world.block.WindGenPillar;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;

public class WindGenMainBlockEntity extends AcademyContainerBlockEntity {
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
                // 这一方向有风扇
                block.setValid(true);
                return;
            } else if (state.is(Blocks.AIR)) {
                // 这一方向是空的
                level.setBlock(east, AcademyBlocks.WINDGEN_FAN.get()
                        .defaultBlockState()
                        .setValue(WindGenPillar.FACING, facing), 19);
                block.setValid(true);
                return;
            }
        } else {
            if (!state.is(Blocks.AIR) && state.is(AcademyBlocks.WINDGEN_FAN.get())) {
                // 这一方向有风扇
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
}
