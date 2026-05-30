package com.mohistmc.academy.skill;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.world.item.BaseFactor;
import com.mohistmc.academy.world.item.FactorElectromaster;
import com.mohistmc.academy.world.item.FactorMeltdowner;
import com.mohistmc.academy.world.item.FactorTeleporter;
import com.mohistmc.academy.world.item.FactorVecmanip;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * @author Mgazul
 * @date 2026/5/30 20:30
 */
@EventBusSubscriber(modid = AcademyCraft.MODID)
public class SkillEventHandler {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerAbilityData data = player.getData(AcademyAttachments.PLAYER_ABILITY);
            data.tick();
            player.setData(AcademyAttachments.PLAYER_ABILITY, data);
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;

        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof BaseFactor) {
            AbilityCategory category = getCategoryFromFactor(stack.getItem());
            if (category != null) {
                PlayerAbilityData data = player.getData(AcademyAttachments.PLAYER_ABILITY);
                if (data.hasAbility()) {
                    player.sendSystemMessage(Component.literal("你已经有能力了!"));
                    return;
                }
                data.setCurrentAbility(category);
                data.setPlayerLevel(1);
                player.setData(AcademyAttachments.PLAYER_ABILITY, data);
                player.sendSystemMessage(Component.literal("后天能力: " + category.getId()));

                if (!player.isCreative()) {
                    stack.shrink(1);
                }
            }
        }
    }

    private static AbilityCategory getCategoryFromFactor(net.minecraft.world.item.Item item) {
        if (item instanceof FactorElectromaster) return AbilityCategory.ELECTROMASTER;
        if (item instanceof FactorMeltdowner) return AbilityCategory.MELTDOWNER;
        if (item instanceof FactorTeleporter) return AbilityCategory.TELEPORTER;
        if (item instanceof FactorVecmanip) return AbilityCategory.VECMANIP;
        return null;
    }
}
