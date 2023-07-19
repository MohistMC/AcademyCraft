package com.mohistmc.academy.world.menu;

import com.mohistmc.academy.world.AcademyItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public abstract class BaseNodeMenu extends AcademyMenu {
    public BaseNodeMenu(MenuType<?> menuType, int windowId, Inventory inv, FriendlyByteBuf data, boolean hasInventory) {
        super(menuType, windowId, inv, data, hasInventory);
        // IN
        addAcademySlot(new Slot(container, 0, 44, 0) {
            @Override
            public boolean mayPlace(ItemStack item) {
                return item.is(AcademyItems.ENERGY_UNIT.get());
            }
        });

        //OUT
        addAcademySlot(new Slot(container, 1, 44, 70) {
            @Override
            public boolean mayPlace(ItemStack item) {
                return item.is(AcademyItems.ENERGY_UNIT.get());
            }
        });
    }
}
