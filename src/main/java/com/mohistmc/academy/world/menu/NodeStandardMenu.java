package com.mohistmc.academy.world.menu;

import com.mohistmc.academy.world.AcademyMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class NodeStandardMenu extends BaseNodeMenu {
    public NodeStandardMenu(int windowId, Inventory inv, FriendlyByteBuf data) {
        super(AcademyMenus.NODE_STANDARD_MENU.get(), windowId, inv, data, true);
    }
}
