package com.mohistmc.academy.world.menu;

import com.mohistmc.academy.world.AcademyMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class NodeAdvancedMenu extends BaseNodeMenu {
    public NodeAdvancedMenu(int windowId, Inventory inv, FriendlyByteBuf data) {
        super(AcademyMenus.NODE_ADVANCED_MENU.get(), windowId, inv, data, true);
    }
}
