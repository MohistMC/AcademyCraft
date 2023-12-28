package com.mohistmc.academy.world.menu;

import com.mohistmc.academy.world.AcademyItems;
import com.mohistmc.academy.world.AcademyMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class WindGenMainMenu extends AcademyMenu {
    public WindGenMainMenu(int windowId, Inventory inv, FriendlyByteBuf data) {
        super(AcademyMenus.WIND_MAIN_MENU.get(), windowId, inv, data, true);

        addAcademySlot(new Slot(container, 0, 80, 0) {
            @Override
            public boolean mayPlace(ItemStack item) {
                return item.is(AcademyItems.WINDGEN_FAN.get());
            }
        });
    }


}
