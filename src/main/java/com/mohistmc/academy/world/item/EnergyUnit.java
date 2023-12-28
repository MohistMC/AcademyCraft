package com.mohistmc.academy.world.item;

import java.util.List;
import net.minecraft.network.chat.Component;
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
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, level, components, tooltipFlag);
        components.add(Component.translatable((getMaxDamage(itemStack) - getDamage(itemStack)) + "/" + getMaxDamage(itemStack) + " IF"));
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        if (damage > getMaxDamage(stack)) {
            damage = getMaxDamage(stack);
        }
        super.setDamage(stack, damage);
    }
}