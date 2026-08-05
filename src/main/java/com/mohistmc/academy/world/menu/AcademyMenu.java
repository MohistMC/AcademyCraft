package com.mohistmc.academy.world.menu;

import com.mohistmc.academy.world.block.entity.AcademyContainerBlockEntity;
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

    /** 背包栏坐标常量 */
    public static final int INV_X = 8;
    public static final int INV_Y = 105;
    public static final int HOTBAR_Y = 163;

    public final Inventory inv;
    public final AcademyMenuContainer container = new AcademyMenuContainer(this);
    public BlockPos pos;

    /** 翻页时是否禁用槽位交互(机器页=0 激活,无线页=1 禁用) */
    private boolean slotsActive = true;

    public AcademyMenu(MenuType<?> menuType, int windowId, Inventory inv, FriendlyByteBuf data, boolean hasInventory) {
        super(menuType, windowId);
        this.inv = inv;
        if (data != null)
            this.pos = data.readBlockPos();
        if (hasInventory) {
            for (int k = 0; k < 3; ++k) {
                for (int i1 = 0; i1 < 9; ++i1) {
                    this.addSlot(pagedSlot(inv, i1 + k * 9 + 9, INV_X + i1 * 18, INV_Y + k * 18));
                }
            }

            for (int l = 0; l < 9; ++l) {
                this.addSlot(pagedSlot(inv, l, INV_X + l * 18, HOTBAR_Y));
            }
        }
        container.reloadItems();
    }

    /** 返回方块位置(与 Return 框架的 getPos 一致) */
    public BlockPos getPos() {
        return pos;
    }

    public void setSlotsActive(boolean active) {
        this.slotsActive = active;
    }

    public boolean areSlotsActive() {
        return slotsActive;
    }

    private Slot pagedSlot(Inventory inv, int index, int x, int y) {
        return new Slot(inv, index, x, y) {
            @Override
            public boolean isActive() {
                return slotsActive;
            }
        };
    }

    public Slot addAcademySlot(Slot slot) {
        // 包装为响应 slotsActive 的槽位
        Slot paged = new Slot(slot.container, slot.getSlotIndex(), slot.x, slot.y) {
            @Override
            public boolean isActive() {
                return slotsActive;
            }

            @Override
            public boolean mayPlace(ItemStack item) {
                // 手持为空时允许悬停高亮（findSlot 依赖 mayPlace(EMPTY)==true）
                return item.isEmpty() || slot.mayPlace(item);
            }

            @Override
            public boolean mayPickup(net.minecraft.world.entity.player.Player player) {
                return slot.mayPickup(player);
            }
        };
        addSlot(paged);
        container.addSlot(paged);
        return paged;
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

    /**
     * 获取机器槽位数量（玩家背包之前的槽位）
     */
    protected int getMachineSlotCount() {
        return container.getContainerSize();
    }

    @Override
    public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(p_38942_);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            int machineSlots = getMachineSlotCount();
            if (p_38942_ < machineSlots) {
                if (!this.moveItemStackTo(itemstack1, machineSlots, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, machineSlots, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
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
                BlockEntity entity = menu.inv.player.level().getBlockEntity(menu.pos);
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
