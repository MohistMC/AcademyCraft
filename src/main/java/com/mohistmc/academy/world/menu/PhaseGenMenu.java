package com.mohistmc.academy.world.menu;

import com.mohistmc.academy.world.AcademyItems;
import com.mohistmc.academy.world.AcademyMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class PhaseGenMenu extends AcademyMenu {
    public PhaseGenMenu(int windowId, Inventory inv, FriendlyByteBuf data) {
        super(AcademyMenus.PHASE_GEN_MENU.get(), windowId, inv, data, true);
        addAcademySlot(new Slot(container, 0, 47, 12) {
            @Override
            public boolean mayPlace(ItemStack item) {
                return item.is(AcademyItems.MATTER_UNIT_PHASE_LIQUID.get());
            }
        });
        addAcademySlot(new Slot(container, 1, 114, 51) {
            @Override
            public boolean mayPlace(ItemStack item) {
                return false;
            }
        });
    }
}
