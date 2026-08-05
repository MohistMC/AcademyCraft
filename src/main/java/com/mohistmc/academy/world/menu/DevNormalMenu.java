package com.mohistmc.academy.world.menu;

import com.mohistmc.academy.world.AcademyMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class DevNormalMenu extends AcademyMenu {
    public DevNormalMenu(int windowId, Inventory inv, FriendlyByteBuf data) {
        super(AcademyMenus.DEV_NORMAL_MENU.get(), windowId, inv, data, false);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}