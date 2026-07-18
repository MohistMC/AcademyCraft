package com.mohistmc.academy.network;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.world.block.entity.BaseNodeBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 客户端→服务端：更新节点名称和密码。
 *
 * @author Mgazul
 */
public record NodeConfigPacket(BlockPos pos, String name, String password) implements CustomPacketPayload {

    public static final Type<NodeConfigPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "node_config"));

    public static final StreamCodec<ByteBuf, NodeConfigPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, NodeConfigPacket::pos,
                    ByteBufCodecs.STRING_UTF8, NodeConfigPacket::name,
                    ByteBufCodecs.STRING_UTF8, NodeConfigPacket::password,
                    NodeConfigPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(NodeConfigPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                BlockEntity be = player.level().getBlockEntity(packet.pos());
                if (be instanceof BaseNodeBlockEntity node) {
                    if (!node.isOwner(player)) {
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§c只有节点所有者才能修改配置"));
                        return;
                    }
                    if (packet.name() != null && !packet.name().isEmpty()) {
                        node.setNodeName(packet.name());
                    }
                    if (packet.password() != null) {
                        node.setPassword(packet.password());
                    }
                    node.setChanged();
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§a节点配置已更新"));
                }
            }
        });
    }
}
