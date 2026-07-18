package com.mohistmc.academy.network;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.energy.api.block.IWirelessGenerator;
import com.mohistmc.academy.energy.api.block.IWirelessReceiver;
import com.mohistmc.academy.energy.impl.WiWorldData;
import com.mohistmc.academy.energy.impl.WirelessSystem;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 客户端→服务端：断开机器与节点的连接。
 *
 * @author Mgazul
 */
public record DisconnectFromNodePacket(BlockPos machinePos) implements CustomPacketPayload {

    public static final Type<DisconnectFromNodePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "disconnect_from_node"));

    public static final StreamCodec<ByteBuf, DisconnectFromNodePacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, DisconnectFromNodePacket::machinePos,
                    DisconnectFromNodePacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(DisconnectFromNodePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                ServerLevel level = player.serverLevel();
                BlockEntity be = level.getBlockEntity(packet.machinePos());
                if (be == null) return;

                WiWorldData data = WiWorldData.getNonCreate(level);
                if (data == null) return;

                if (be instanceof IWirelessGenerator || be instanceof IWirelessReceiver) {
                    // 通过 NodeConn 查找并移除
                    var conn = data.getNodeConnection(
                            be instanceof IWirelessGenerator gen ? gen
                                    : (com.mohistmc.academy.energy.api.block.IWirelessUser) be
                    );
                    if (conn != null) {
                        conn.dispose();
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§a已断开连接"));
                    } else {
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§c该机器未连接到任何节点"));
                    }
                }
            }
        });
    }
}
