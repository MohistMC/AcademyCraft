package com.mohistmc.academy.client.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AcademyContainerBlockEntity extends BlockEntity {
    private NonNullList<ItemStack> items = NonNullList.withSize(0, ItemStack.EMPTY);

    public AcademyContainerBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    @Override
    public void load(CompoundTag p_155245_) {
        super.load(p_155245_);
        setItems(NonNullList.withSize(getContainerSize(), ItemStack.EMPTY));
        ContainerHelper.loadAllItems(p_155245_, getItems());
    }

    @Override
    public void saveAdditional(CompoundTag p_187489_) {
        super.saveAdditional(p_187489_);
        ContainerHelper.saveAllItems(p_187489_, getItems());
    }

    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    public void setItems(NonNullList<ItemStack> items) {
        this.items = items;
        setChanged();
        if (level != null)
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    public void saveItems() {
        this.saveWithoutMetadata();
    }

    public abstract int getContainerSize();
}
