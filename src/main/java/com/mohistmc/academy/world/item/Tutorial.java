package com.mohistmc.academy.world.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class Tutorial extends Item {
    public Tutorial() {
        super(new Item.Properties());
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        //TODO: 打开GUI
        return InteractionResult.CONSUME;
    }
}
