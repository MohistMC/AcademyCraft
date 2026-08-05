package com.mohistmc.academy.world.menu;

import com.mohistmc.academy.capability.EnergyItemHelper;
import com.mohistmc.academy.world.AcademyMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SolarGenMenu extends AcademyMenu {
    public SolarGenMenu(int windowId, Inventory inv, FriendlyByteBuf data) {
        super(AcademyMenus.SOLAR_GEN_MENU.get(), windowId, inv, data, true);
        addAcademySlot(new Slot(container, 0, 44, 81) {
            @Override
            public boolean mayPlace(ItemStack item) {
                return EnergyItemHelper.isEnergyItem(item);
            }
        });
    }
}
