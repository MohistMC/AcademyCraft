package com.mohistmc.academy.world.item;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class EnergyUnit extends AcademyItem {

    public static int maxEnergy = 10000;

    public EnergyUnit() {
        super(new Properties().durability(maxEnergy));
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, Item.TooltipContext p_333372_, List<Component> p_41423_, TooltipFlag p_41424_) {
        super.appendHoverText(p_41421_, p_333372_, p_41423_, p_41424_);
        p_41423_.add(Component.translatable((p_41421_.getMaxDamage() - p_41421_.getDamageValue()) + "/" + p_41421_.getMaxDamage() + " IF"));
    }
}