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
        // 输入槽：PhaseLiquid 单元
        addAcademySlot(new Slot(container, 0, 44, 20) {
            @Override
            public boolean mayPlace(ItemStack item) {
                return item.is(AcademyItems.MATTER_UNIT_PHASE_LIQUID.get());
            }
        });
        // 输出槽：空单元
        addAcademySlot(new Slot(container, 1, 44, 70) {
            @Override
            public boolean mayPlace(ItemStack item) {
                return false;
            }
        });
    }
}
