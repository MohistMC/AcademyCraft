package com.mohistmc.academy.network;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.ChargingSkillEffect;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.Skill;
import com.mohistmc.academy.skill.SkillChargingManager;
import com.mohistmc.academy.skill.SkillEffect;
import com.mohistmc.academy.skill.SkillPreset;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SkillKeyDownPacket(int slotIndex) implements CustomPacketPayload {

    public static final Type<SkillKeyDownPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "skill_key_down"));

    public static final StreamCodec<ByteBuf, SkillKeyDownPacket> STREAM_CODEC =
            ByteBufCodecs.INT.map(SkillKeyDownPacket::new, SkillKeyDownPacket::slotIndex);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SkillKeyDownPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            PlayerAbilityData data = player.getData(AcademyAttachments.PLAYER_ABILITY);

            if (!data.isAbilityActive()) return;

            if (packet.slotIndex() < 0 || packet.slotIndex() >= SkillPreset.SLOT_COUNT) return;

            Skill skill = data.getSlotSkill(data.getCurrentPresetIndex(), packet.slotIndex());
            if (skill == null) return;

            if (!data.hasLearnedSkill(skill.getId())) return;

            if (!data.canUseSkill(skill)) return;

            SkillEffect effect = skill.getEffect();
            if (!(effect instanceof ChargingSkillEffect chargingEffect)) return;

            if (SkillChargingManager.isCharging(player.getUUID())) return;

            SkillChargingManager.startCharging(player.getUUID(), packet.slotIndex());
            chargingEffect.onChargingStart(player, data);
            data.syncTo(player);
        });
    }
}
