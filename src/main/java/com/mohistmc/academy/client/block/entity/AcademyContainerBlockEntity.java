package com.mohistmc.academy.client.block.entity;

import net.minecraft.core.BlockPos;
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
    public void load(CompoundTag tag) {
        super.load(tag);
        setItems(deserializeContentItems(tag));
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        serializeContentItems(tag);

    }

    public NonNullList<ItemStack> deserializeContentItems(CompoundTag tag) {
        NonNullList<ItemStack> items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        CompoundTag contentItems = tag.getCompound("contentItems");
        for (int i = 0; i < getContainerSize(); i++) {
            if (contentItems.contains(String.valueOf(i))) {
                items.set(i, ItemStack.of(contentItems.getCompound(String.valueOf(i))));
            }
        }
        return items;
    }

    public void serializeContentItems(CompoundTag tag) {
        CompoundTag contentItems = new CompoundTag();
        if (items.isEmpty()) {
            items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
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
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        serializeContentItems(tag);
        return tag;
    }
}
