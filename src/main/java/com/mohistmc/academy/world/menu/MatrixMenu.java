package com.mohistmc.academy.world.menu;

import com.mohistmc.academy.world.AcademyItems;
import com.mohistmc.academy.world.AcademyMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * 无线虚能矩阵菜单 —— 矩阵核心槽 + 约束金属板槽。
 */
public class MatrixMenu extends AcademyMenu {

    public MatrixMenu(int windowId, Inventory inv, FriendlyByteBuf data) {
        super(AcademyMenus.MATRIX_MENU.get(), windowId, inv, data, true);

        addAcademySlot(new Slot(container, 0, 80, 36) {
            @Override
            public boolean mayPlace(ItemStack item) {
                return item.is(AcademyItems.MAT_CORE_0.get())
                        || item.is(AcademyItems.MAT_CORE_1.get())
                        || item.is(AcademyItems.MAT_CORE_2.get());
            }
        });

        int[][] platePos = {{55, 60}, {106, 60}, {80, 11}};
        for (int i = 0; i < 3; i++) {
            addAcademySlot(new Slot(container, 1 + i, platePos[i][0], platePos[i][1]) {
                @Override
                public boolean mayPlace(ItemStack item) {
                    return item.is(AcademyItems.CONSTRAINT_PLATE.get());
                }
            });
        }
    }
}
