package com.mohistmc.academy.network;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.world.block.entity.BaseNodeBlockEntity;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
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
public record NodeConfigPacket(BlockPos pos, Optional<String> name, Optional<String> password) implements CustomPacketPayload {

    public static final Type<NodeConfigPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "node_config"));

    public static final StreamCodec<ByteBuf, NodeConfigPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, NodeConfigPacket::pos,
                    ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), NodeConfigPacket::name,
                    ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), NodeConfigPacket::password,
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
                System.out.println("[AcademyDebug] NodeConfigPacket: received pos=" + packet.pos()
                    + " hasName=" + packet.name().isPresent() + " hasPassword=" + packet.password().isPresent());
                if (be instanceof BaseNodeBlockEntity node) {
                    System.out.println("[AcademyDebug] NodeConfigPacket: found node " + node.getNodeName());
                    if (!node.isOwner(player)) {
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cOnly owner can modify node config"));
                        System.out.println("[AcademyDebug] NodeConfigPacket: rejected - not owner player=" + player.getName().getString());
                        return;
                    }
                    packet.name().ifPresent(n -> {
                        if (!n.isEmpty()) {
                            System.out.println("[AcademyDebug] NodeConfigPacket: setName old=" + node.getNodeName() + " new=" + n);
                            node.setNodeName(n);
                        } else {
                            System.out.println("[AcademyDebug] NodeConfigPacket: skip empty name");
                        }
                    });
                    packet.password().ifPresent(p -> {
                        System.out.println("[AcademyDebug] NodeConfigPacket: setPassword old=" + node.getPassword() + " new=" + p);
                        node.setPassword(p);
                    });
                    node.setChanged();
                    System.out.println("[AcademyDebug] NodeConfigPacket: write done");
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aNode config updated"));
                } else {
                    System.out.println("[AcademyDebug] NodeConfigPacket: no node entity found entity=" + be);
                }
            }
        });
    }
}
