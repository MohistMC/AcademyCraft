package com.mohistmc.academy.world.menu;

import com.mohistmc.academy.world.AcademyItems;
import com.mohistmc.academy.world.AcademyMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ImagFusorMenu extends AcademyMenu {

    public ImagFusorMenu(int windowId, Inventory inv, FriendlyByteBuf data) {
        super(AcademyMenus.IMAG_FUSOR_MENU.get(), windowId, inv, data, true);

        addAcademySlot(new Slot(container, 0, 16, 0) {
            @Override
            public boolean mayPlace(ItemStack item) {
                return item.is(AcademyItems.MATTER_UNIT_PHASE_LIQUID.get());
            }
        });

        addAcademySlot(new Slot(container, 1, 148, 0) {
            @Override
            public boolean mayPlace(ItemStack item) {
                return false;
            }
        });

        addAcademySlot(new Slot(container, 2, 16, 40) {
            @Override
            public boolean mayPlace(ItemStack item) {
                return item.is(AcademyItems.CRYSTAL_LOW.get()) || item.is(AcademyItems.CRYSTAL_NORMAL.get());
            }
        });

        addAcademySlot(new Slot(container, 3, 148, 40) {
            @Override
            public boolean mayPlace(ItemStack item) {
                return false;
            }
        });
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 4) {
                if (!this.moveItemStackTo(itemstack1, 4, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(itemstack1, 0, 4, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }
}
