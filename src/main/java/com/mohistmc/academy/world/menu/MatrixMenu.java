package com.mohistmc.academy.world.menu;

import com.mohistmc.academy.world.AcademyItems;
import com.mohistmc.academy.world.AcademyMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * 无线虚能矩阵菜单 —— 矩阵核心槽 + 约束金属板槽。
 *
 * @author Mgazul
 */
public class MatrixMenu extends AcademyMenu {

    public MatrixMenu(int windowId, Inventory inv, FriendlyByteBuf data) {
        super(AcademyMenus.MATRIX_MENU.get(), windowId, inv, data, true);

        // 矩阵核心槽 (位置索引0)
        addAcademySlot(new Slot(container, 0, 44, 20) {
            @Override
            public boolean mayPlace(ItemStack item) {
                return item.is(AcademyItems.MAT_CORE_0.get())
                        || item.is(AcademyItems.MAT_CORE_1.get())
                        || item.is(AcademyItems.MAT_CORE_2.get());
            }
        });

        // 约束金属板槽位 (位置索引1-3，需要3块)
        for (int i = 0; i < 3; i++) {
            int slotIndex = i;
            int yPos = 20 + i * 18;
            addAcademySlot(new Slot(container, 1 + i, 116, yPos) {
                @Override
                public boolean mayPlace(ItemStack item) {
                    return item.is(AcademyItems.CONSTRAINT_PLATE.get());
                }
            });
        }
    }
}
