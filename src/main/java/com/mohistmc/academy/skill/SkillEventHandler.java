package com.mohistmc.academy.skill;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.config.ACConfig;
import com.mohistmc.academy.network.LearnSkillPacket;
import com.mohistmc.academy.network.SyncChargingStatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = AcademyCraft.MODID)
public class SkillEventHandler {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        PlayerAbilityData data = event.getEntity().getData(AcademyAttachments.PLAYER_ABILITY);
        data.tick(); // 服务端和客户端都递减冷却

        if (event.getEntity() instanceof ServerPlayer player) {
            SkillChargingManager.ChargingState state = SkillChargingManager.getState(player.getUUID());
            if (state != null && !state.releasing) {
                Skill skill = data.getSlotSkill(data.getCurrentPresetIndex(), state.slotIndex);
                if (skill == null) {
                    SkillChargingManager.stopCharging(player.getUUID());
                    PacketDistributor.sendToPlayer(player, new SyncChargingStatePacket(-1, 0, ""));
                } else {
                    SkillEffect effect = skill.getEffect();
                    if (effect instanceof ChargingSkillEffect chargingEffect) {
                        state.ticks++;
                        PacketDistributor.sendToPlayer(player,
                                new SyncChargingStatePacket(state.ticks, chargingEffect.getMaxChargeTicks(), skill.getId()));

                        boolean canContinue = chargingEffect.onChargingTick(player, data, state.ticks);
                        if (!canContinue || state.ticks >= chargingEffect.getMaxChargeTicks()) {
                            state.releasing = true;
                            SkillChargingManager.stopCharging(player.getUUID());
                            if (state.ticks >= chargingEffect.getMinChargeTicks()) {
                                chargingEffect.onChargingRelease(player, data, state.ticks);
                                data.addProficiency(skill.getId(), 0.002f);
                            } else {
                                chargingEffect.onChargingAbort(player, data);
                            }
                            PacketDistributor.sendToPlayer(player, new SyncChargingStatePacket(-1, 0, ""));
                            data.syncTo(player);
                        }
                    } else {
                        SkillChargingManager.stopCharging(player.getUUID());
                        PacketDistributor.sendToPlayer(player, new SyncChargingStatePacket(-1, 0, ""));
                    }
                }
            }

            player.setData(AcademyAttachments.PLAYER_ABILITY, data);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            if (ACConfig.Server.crossServerSync()) {
                CrossServerAbilityStore.load(sp).ifPresent(data ->
                        sp.setData(AcademyAttachments.PLAYER_ABILITY, data));
            }
            LearnSkillPacket.syncToClient(sp);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            if (ACConfig.Server.crossServerSync()) {
                CrossServerAbilityStore.save(sp);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            LearnSkillPacket.syncToClient(sp);
        }
    }
}
