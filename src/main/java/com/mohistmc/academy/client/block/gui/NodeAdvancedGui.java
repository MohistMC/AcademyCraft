package com.mohistmc.academy.client.block.gui;

import com.mohistmc.academy.world.menu.NodeAdvancedMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class NodeAdvancedGui extends BaseNodeGui<NodeAdvancedMenu> {
    public NodeAdvancedGui(NodeAdvancedMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }
}
