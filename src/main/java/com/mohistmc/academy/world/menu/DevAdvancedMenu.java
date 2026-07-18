package com.mohistmc.academy.world.menu;

import com.mohistmc.academy.world.AcademyItems;
import com.mohistmc.academy.world.AcademyMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * 高级能力开发机菜单 —— 包含高压磁增幅线圈和能力诱导因子槽位。
 *
 * @author Mgazul
 */
public class DevAdvancedMenu extends AcademyMenu {

    public DevAdvancedMenu(int windowId, Inventory inv, FriendlyByteBuf data) {
        super(AcademyMenus.DEV_ADVANCED_MENU.get(), windowId, inv, data, true);

        // 线圈槽 (位置0)
        addAcademySlot(new Slot(container, 0, 44, 20) {
            @Override
            public boolean mayPlace(ItemStack item) {
                return item.is(AcademyItems.MAGNETIC_COIL.get());
            }
        });

        // 因子槽 (位置1)
        addAcademySlot(new Slot(container, 1, 44, 70) {
            @Override
            public boolean mayPlace(ItemStack item) {
                return item.getItem() instanceof com.mohistmc.academy.world.item.BaseFactor;
            }
        });
    }
}
