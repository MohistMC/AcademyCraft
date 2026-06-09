package com.mohistmc.academy.network;

import com.mohistmc.academy.AcademyCraft;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncChargingStatePacket(int ticks, int maxTicks, String skillId) implements CustomPacketPayload {

    public static final Type<SyncChargingStatePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "sync_charging"));

    public static final StreamCodec<ByteBuf, SyncChargingStatePacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, SyncChargingStatePacket::ticks,
                    ByteBufCodecs.INT, SyncChargingStatePacket::maxTicks,
                    ByteBufCodecs.STRING_UTF8, SyncChargingStatePacket::skillId,
                    SyncChargingStatePacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncChargingStatePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            com.mohistmc.academy.client.ChargingHudOverlay.setChargingState(packet.ticks, packet.maxTicks, packet.skillId);
        });
    }
}
