package com.mohistmc.academy.client.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
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
    public void loadAdditional(CompoundTag p_331149_, HolderLookup.Provider p_333170_) {
        super.loadAdditional(p_331149_, p_333170_);
        setItems(deserializeContentItems(p_331149_, p_333170_));
    }

    @Override
    public void saveAdditional(CompoundTag p_187471_, HolderLookup.Provider p_327783_) {
        super.saveAdditional(p_187471_, p_327783_);
        serializeContentItems(p_187471_, p_327783_);

    }

    public NonNullList<ItemStack> deserializeContentItems(CompoundTag tag, HolderLookup.Provider provider) {
        NonNullList<ItemStack> items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        CompoundTag contentItems = tag.getCompound("contentItems");
        for (int i = 0; i < getContainerSize(); i++) {
            if (contentItems.contains(String.valueOf(i))) {
                items.set(i, ItemStack.parseOptional(provider, contentItems.getCompound(String.valueOf(i))));
            }
        }
        return items;
    }

    public void serializeContentItems(CompoundTag tag, HolderLookup.Provider provider) {
        CompoundTag contentItems = new CompoundTag();
        if (items.isEmpty()) {
            items =NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        }
        for (int i = 0; i < getContainerSize(); i++) {
            contentItems.put(String.valueOf(i), items.get(i).serializeNBT());
        }
        tag.put("contentItems", contentItems);
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

    public abstract int getContainerSize();

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider p_329179_) {
        CompoundTag tag = new CompoundTag();
        serializeContentItems(tag);
        return tag;
    }


}
