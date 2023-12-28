package com.mohistmc.academy.client.block.entity;

import com.mohistmc.academy.capability.IFCapabilityImpl;
import com.mohistmc.academy.capability.IIFCapability;
import com.mohistmc.academy.world.AcademyBlockEntities;
import com.mohistmc.academy.world.AcademyCapability;
import com.mohistmc.academy.world.AcademyItems;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
                Optional<?> optional = getCapability(AcademyCapability.IF_CAPABILITY).resolve();
                if (optional.isPresent()) {
                    Object cap = optional.get();
                    if (cap instanceof IIFCapability ifPower) {
                        item.setDamageValue(item.getDamageValue() - ifPower.getIF());
                        item.setTag(ifPower.serializeNBT(item.getTag()));
                    }
                }
            }
        });
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == AcademyCapability.IF_CAPABILITY) {
            return LazyOptional.of(() ->
                    new IFCapabilityImpl(1)
            ).cast();
        }
        return LazyOptional.empty();
    }

    public boolean isValidMiddle() {
        return validMiddle;
    }

    public boolean isValidMain() {
        return validBlock;
    }
}
