package com.mohistmc.academy.network;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.ChargingSkillEffect;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.Skill;
import com.mohistmc.academy.skill.SkillChargingManager;
import com.mohistmc.academy.skill.SkillEffect;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SkillKeyUpPacket(int slotIndex) implements CustomPacketPayload {

    public static final Type<SkillKeyUpPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "skill_key_up"));

    public static final StreamCodec<ByteBuf, SkillKeyUpPacket> STREAM_CODEC =
            ByteBufCodecs.INT.map(SkillKeyUpPacket::new, SkillKeyUpPacket::slotIndex);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SkillKeyUpPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            PlayerAbilityData data = player.getData(AcademyAttachments.PLAYER_ABILITY);

            SkillChargingManager.ChargingState state = SkillChargingManager.getState(player.getUUID());
            if (state == null || state.releasing) return;

            state.releasing = true; // 标记正在释放，防止 onPlayerTick 重复进入

            Skill skill = data.getSlotSkill(data.getCurrentPresetIndex(), state.slotIndex);
            if (skill == null) {
                SkillChargingManager.stopCharging(player.getUUID());
                return;
            }

            SkillEffect effect = skill.getEffect();
            if (!(effect instanceof ChargingSkillEffect chargingEffect)) {
                SkillChargingManager.stopCharging(player.getUUID());
                return;
            }

            SkillChargingManager.stopCharging(player.getUUID());

            if (state.ticks >= chargingEffect.getMinChargeTicks()) {
                chargingEffect.onChargingRelease(player, data, state.ticks);
                data.addProficiency(skill.getId(), 0.002f);
            } else {
                chargingEffect.onChargingAbort(player, data);
            }
            data.syncTo(player);
        });
    }
}
