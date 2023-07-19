package com.mohistmc.academy.client.block.entity;

import com.mohistmc.academy.world.AcademyBlockEntities;
import com.sun.jna.platform.win32.WinUser;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WindGenFanBlockEntity extends BlockEntity {
    public float rH = 360;

    public WindGenFanBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(AcademyBlockEntities.WINDGEN_FAN.get(), p_155229_, p_155230_);
    }

}
