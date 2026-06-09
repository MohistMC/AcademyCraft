package com.mohistmc.academy.skill;

import com.mohistmc.academy.AcademyCraft;
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
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerAbilityData data = player.getData(AcademyAttachments.PLAYER_ABILITY);
            data.tick();

            SkillChargingManager.ChargingState state = SkillChargingManager.getState(player.getUUID());
            if (state != null && !state.releasing) { // 避免重复进入
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
                            state.releasing = true; // 标记正在释放，防止重复
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
