package com.mohistmc.academy.client.block.entity;

import com.mohistmc.academy.world.AcademyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CatEngineBlockEntity extends BlockEntity {
    public int time;
    public float rot;
    public float oRot;
    public float tRot;
    public float rH = 0;

    public CatEngineBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(AcademyBlockEntities.CAT_ENGINE.get(), p_155229_, p_155230_);
    }

    public static void tickAnim(Level level, BlockPos blockPos, BlockState blockState, CatEngineBlockEntity e) {
        e.oRot = e.rot;
        Player player = level.getNearestPlayer(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 3, false);
        if (player != null) {
            double d0 = player.getX() - ((double)blockPos.getX() + 0.5D);
            double d1 = player.getZ() - ((double)blockPos.getZ() + 0.5D);
            e.tRot = (float) Mth.atan2(d1, d0);
        } else {
            e.tRot += 0.02F;
        }
        while(e.rot >= (float)Math.PI) {
            e.rot -= ((float)Math.PI * 2F);
        }

        while(e.rot < -(float)Math.PI) {
            e.rot += ((float)Math.PI * 2F);
        }

        while(e.tRot >= (float)Math.PI) {
            e.tRot -= ((float)Math.PI * 2F);
        }

        while(e.tRot < -(float)Math.PI) {
            e.tRot += ((float)Math.PI * 2F);
        }

        float f2;
        for(f2 = e.tRot - e.rot; f2 >= (float)Math.PI; f2 -= ((float)Math.PI * 2F)) {
        }

        while(f2 < -(float)Math.PI) {
            f2 += ((float)Math.PI * 2F);
        }

        e.rot += f2 * 0.4F;
        ++e.time;
    }
}
