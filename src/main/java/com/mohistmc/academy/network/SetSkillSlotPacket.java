package com.mohistmc.academy.network;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillPreset;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * @author Mgazul
 * @date 2026/5/30 22:31
 */
public record SetSkillSlotPacket(int presetIndex, int slotIndex, String skillId) implements CustomPacketPayload {

    public static final Type<SetSkillSlotPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "set_skill_slot"));

    public static final StreamCodec<ByteBuf, SetSkillSlotPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, SetSkillSlotPacket::presetIndex,
                    ByteBufCodecs.INT, SetSkillSlotPacket::slotIndex,
                    ByteBufCodecs.STRING_UTF8, SetSkillSlotPacket::skillId,
                    SetSkillSlotPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SetSkillSlotPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            PlayerAbilityData data = player.getData(AcademyAttachments.PLAYER_ABILITY);

            if (packet.presetIndex() < 0 || packet.presetIndex() >= PlayerAbilityData.PRESET_COUNT) return;
            if (packet.slotIndex() < 0 || packet.slotIndex() >= SkillPreset.SLOT_COUNT) return;

            String skillId = packet.skillId();
            if (skillId.isEmpty()) {
                data.clearSlot(packet.presetIndex(), packet.slotIndex());
            } else {
                if (!data.hasLearnedSkill(skillId)) return;
                data.setSlot(packet.presetIndex(), packet.slotIndex(), skillId);
            }

            data.syncTo(player);
        });
    }
}
