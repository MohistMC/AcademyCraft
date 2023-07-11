package com.mohistmc.academy.world.menu;

import com.mohistmc.academy.world.AcademyMenus;
import com.mohistmc.academy.world.container.WindGenBaseContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ChestBlock;

public class WindBaseMenu extends AbstractContainerMenu {
    public final Inventory inv;
    public final WindGenBaseContainer container = new WindGenBaseContainer(this);
    public final BlockPos pos;

    public WindBaseMenu(int windowId, Inventory inv, FriendlyByteBuf data) {
        super(AcademyMenus.WIND_BASE.get(), windowId);
        this.inv = inv;
        this.pos = data.readBlockPos();
        container.startOpen(inv.player);
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

        // 输出栏
        this.addSlot(new Slot(this.container, 0, 44, 70));
    }


    @Override
    public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(p_38942_);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (p_38942_ < 1) {
                if (!this.moveItemStackTo(itemstack1, 1, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
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

    @Override
    public boolean stillValid(Player p_38874_) {
        return this.container.stillValid(p_38874_);
    }

    @Override
    public void removed(Player p_39251_) {
        super.removed(p_39251_);
        this.container.stopOpen(p_39251_);
    }


}
