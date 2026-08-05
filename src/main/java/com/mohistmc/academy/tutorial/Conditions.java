package com.mohistmc.academy.tutorial;

import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.PlayerAbilityData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

/**
 * 教程条件工厂 —— 曾经获得(拾取/合成/烧炼持久化记录)或当前背包持有。
 */
public final class Conditions {

    private Conditions() {}

    private static final Condition ALWAYS_TRUE = player -> true;

    public static Condition alwaysTrue() {
        return ALWAYS_TRUE;
    }

    public static Condition itemObtained(Item item) {
        final String itemId = BuiltInRegistries.ITEM.getKey(item).toString();
        return player -> {
            PlayerAbilityData data = player.getData(AcademyAttachments.PLAYER_ABILITY);
            if (data.hasObtained(itemId)) return true;
            return hasItem(player, item);
        };
    }

    public static Condition itemObtained(Block block) {
        return itemObtained(block.asItem());
    }

    private static boolean hasItem(Player player, Item item) {
        for (ItemStack stack : player.getInventory().items) {
            if (!stack.isEmpty() && stack.getItem() == item) return true;
        }
        return false;
    }
}
