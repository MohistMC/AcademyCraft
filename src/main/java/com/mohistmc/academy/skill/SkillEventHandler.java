package com.mohistmc.academy.skill;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.network.LearnSkillPacket;
import com.mohistmc.academy.world.item.BaseFactor;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

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
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            LearnSkillPacket.syncToClient(sp);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            LearnSkillPacket.syncToClient(sp);
        }
    }
}
