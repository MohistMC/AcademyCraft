package com.mohistmc.academy.network;

import com.mohistmc.academy.AcademyCraft;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 服务端→客户端：同步可用节点列表。
 */
public record NodeListSyncPacket(CompoundTag data) implements CustomPacketPayload {

    public static final Type<NodeListSyncPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "node_list_sync"));

    public static final StreamCodec<ByteBuf, NodeListSyncPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.COMPOUND_TAG, NodeListSyncPacket::data,
                    NodeListSyncPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(NodeListSyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            // 客户端处理：在 AcademyBaseUI 中接收并缓存
            if (context.player().level().isClientSide()) {
                com.mohistmc.academy.client.gui.AcademyBaseUI.receiveNodeList(packet.data());
            }
        });
    }
}
