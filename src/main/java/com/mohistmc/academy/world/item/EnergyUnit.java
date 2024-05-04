package com.mohistmc.academy.world.item;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class EnergyUnit extends AcademyItem {
    public EnergyUnit() {
        super(new Properties()
                .durability(10000)
        );
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, Item.TooltipContext p_333372_, List<Component> p_41423_, TooltipFlag p_41424_) {
        super.appendHoverText(p_41421_, p_333372_, p_41423_, p_41424_);
        p_41423_.add(Component.translatable((p_41421_.getMaxDamage() - p_41421_.getDamageValue()) + "/" + p_41421_.getDamageValue() + " IF"));
    }
}