package com.mohistmc.academy.world.menu;

import com.mohistmc.academy.client.block.entity.AcademyContainerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class AcademyMenu extends AbstractContainerMenu {

    public final Inventory inv;
    public final AcademyMenuContainer container = new AcademyMenuContainer(this);
    public BlockPos pos;

    public AcademyMenu(MenuType<?> menuType, int windowId, Inventory inv, FriendlyByteBuf data, boolean hasInventory) {
        super(menuType, windowId);
        this.inv = inv;
        if (data != null)
            this.pos = data.readBlockPos();
        if (hasInventory) {
            // 背包
            for (int k = 0; k < 3; ++k) {
                for (int i1 = 0; i1 < 9; ++i1) {
                    this.addSlot(new Slot(inv, i1 + k * 9 + 9, 8 + i1 * 18, 94 + k * 18));
                }
            }

            // 快捷栏
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(inv, l, 8 + l * 18, 152));
            }
        }
        container.reloadItems();
    }

    public Slot addAcademySlot(Slot slot) {
        addSlot(slot);
        container.addSlot(slot);
        return slot;
    }


    @Override
    public boolean stillValid(Player p_38874_) {
        return container.stillValid(p_38874_);
    }

    @Override
    public void slotsChanged(Container p_38868_) {
        AcademyContainerBlockEntity blockEntity = container.getBlockEntity(this);
        if (blockEntity != null) {
            blockEntity.setItems(container.items);
        }
        super.slotsChanged(p_38868_);
    }

    public static class AcademyMenuContainer implements Container, StackedContentsCompatible {

        private final AcademyMenu menu;
        private NonNullList<ItemStack> items = NonNullList.withSize(0, ItemStack.EMPTY);

        public AcademyMenuContainer(AcademyMenu menu) {
            this.menu = menu;
        }

        @Override
        public int getContainerSize() {
            return items.size();
        }

        @Override
        public boolean isEmpty() {
            return items.isEmpty();
        }

        @Override
        public ItemStack getItem(int p_18941_) {
            reloadItems();
            return items.size() <= p_18941_ ? ItemStack.EMPTY : items.get(p_18941_);
        }

        @Override
        public ItemStack removeItem(int p_18942_, int p_18943_) {
            ItemStack stack = getItem(p_18942_);
            // System.out.println("移除物品: " + p_18942_);
            items.set(p_18942_, ItemStack.EMPTY);
            saveItems();
            return stack;
        }

        public void saveItems() {
            AcademyContainerBlockEntity blockEntity = getBlockEntity(this.menu);
            if (blockEntity != null) {
                blockEntity.setItems(items);
            }
        }


        public void reloadItems() {
            AcademyContainerBlockEntity blockEntity = getBlockEntity(this.menu);
            if (blockEntity != null) {
                items = blockEntity.getItems();
            }
        }

        public AcademyContainerBlockEntity getBlockEntity(AcademyMenu menu) {
            if (menu != null && menu.pos != null) {
                BlockEntity entity = menu.inv.player.level.getBlockEntity(menu.pos);
                if (entity instanceof AcademyContainerBlockEntity blockEntity && !blockEntity.isRemoved()) {
                    return blockEntity;
                }
            }
            return null;
        }

        @Override
        public ItemStack removeItemNoUpdate(int p_18951_) {
            return removeItem(p_18951_, 1);
        }

        @Override
        public void setItem(int p_18944_, ItemStack p_18945_) {
            if (p_18945_ == ItemStack.EMPTY) return;
            if (items.size() > p_18944_) {
                items.set(p_18944_, p_18945_);
                saveItems();
            }
        }

        @Override
        public void setChanged() {
            AcademyContainerBlockEntity blockEntity = getBlockEntity(this.menu);
            if (blockEntity != null) {
                blockEntity.setChanged();
            }
        }

        @Override
        public boolean stillValid(Player p_18946_) {
            return getBlockEntity(this.menu) != null;
        }

        @Override
        public void clearContent() {
            items.clear();
            saveItems();
        }

        @Override
        public void fillStackedContents(StackedContents p_40281_) {
            for (ItemStack item : items) {
                p_40281_.accountSimpleStack(item);
            }
        }

        public void addSlot(Slot slot) {
            items = NonNullList.withSize(items.size() + 1, ItemStack.EMPTY);
        }
    }


}
