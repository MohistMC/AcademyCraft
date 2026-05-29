package com.mohistmc.academy.world.block.entity;

import com.mohistmc.academy.world.AcademyBlockEntities;
import com.mohistmc.academy.world.AcademyItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class WindGenBaseBlockEntity extends AcademyContainerBlockEntity {
    private boolean validBlock = false;
    private boolean validMiddle = false;

    public WindGenBaseBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(AcademyBlockEntities.WINDGEN_BASE.get(), p_155229_, p_155230_);
    }


    public void tick(boolean validBlock, boolean validMiddle) {
        this.validBlock = validBlock;
        this.validMiddle = validMiddle;
        if (!validBlock) return;
        getItems().forEach((item) -> {
            if (item.is(AcademyItems.ENERGY_UNIT.get())) {
                /*
                Optional<?> optional = getCapability(AcademyCapability.IF_CAPABILITY).resolve();
                if (optional.isPresent()) {
                    Object cap = optional.get();
                    if (cap instanceof IIFCapability ifPower) {
                        item.setDamageValue(item.getDamageValue() - ifPower.getIF());
                        // item.applyComponents(ifPower.serializeNBT(item.collectComponents())); TODO
                    }
                }
                 */
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
