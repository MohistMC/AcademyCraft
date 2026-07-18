package com.mohistmc.academy.client.block.gui;

import com.mohistmc.academy.world.menu.NodeStandardMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class NodeStandardGui extends BaseNodeGui<NodeStandardMenu> {
    public NodeStandardGui(NodeStandardMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }
}
