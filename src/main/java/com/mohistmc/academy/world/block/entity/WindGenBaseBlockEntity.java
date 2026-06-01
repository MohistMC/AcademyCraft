package com.mohistmc.academy.world.block.entity;

import com.mohistmc.academy.capability.EnergyItemHelper;
import com.mohistmc.academy.world.AcademyBlockEntities;
import com.mohistmc.academy.world.AcademyItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class WindGenBaseBlockEntity extends AcademyContainerBlockEntity {
    private boolean validBlock = false;
    private boolean validMiddle = false;

    // 风力发电基础速率
    private static final int GENERATION_RATE = 1;

    public WindGenBaseBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(AcademyBlockEntities.WINDGEN_BASE.get(), p_155229_, p_155230_);
    }

    public void tick(boolean validBlock, boolean validMiddle) {
        this.validBlock = validBlock;
        this.validMiddle = validMiddle;
        if (!validBlock) return;

        getItems().forEach((item) -> {
            if (item.is(AcademyItems.ENERGY_UNIT.get())) {
                if (EnergyItemHelper.isEnergyItem(item)) {
                    EnergyItemHelper.receiveEnergy(item, GENERATION_RATE, false);
                }
            }
        });
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    public boolean isValidMiddle() {
        return validMiddle;
    }

    public boolean isValidMain() {
        return validBlock;
    }
}
