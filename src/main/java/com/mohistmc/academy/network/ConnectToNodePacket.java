package com.mohistmc.academy.network;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.energy.api.block.IWirelessGenerator;
import com.mohistmc.academy.energy.api.block.IWirelessNode;
import com.mohistmc.academy.energy.api.block.IWirelessReceiver;
import com.mohistmc.academy.energy.impl.WiWorldData;
import com.mohistmc.academy.energy.impl.WirelessSystem;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 客户端→服务端：连接机器到指定节点。
 *
 * @author Mgazul
 */
public record ConnectToNodePacket(BlockPos machinePos, BlockPos nodePos, Optional<String> password) implements CustomPacketPayload {

    public static final Type<ConnectToNodePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "connect_to_node"));

    public static final StreamCodec<ByteBuf, ConnectToNodePacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, ConnectToNodePacket::machinePos,
                    BlockPos.STREAM_CODEC, ConnectToNodePacket::nodePos,
                    ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), ConnectToNodePacket::password,
                    ConnectToNodePacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ConnectToNodePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                ServerLevel level = player.serverLevel();

                // 获取节点
                BlockEntity nodeBe = level.getBlockEntity(packet.nodePos());
                if (!(nodeBe instanceof com.mohistmc.academy.energy.api.block.IWirelessNode iNode)) {
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§c未找到无线节点"));
                    return;
                }

                // 获取机器
                BlockEntity machineBe = level.getBlockEntity(packet.machinePos());
                if (machineBe == null) {
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§c未找到机器"));
                    return;
                }

                String pass = packet.password().orElse("");
                boolean needAuth = pass.isEmpty() && !iNode.getPassword().isEmpty();

                if (machineBe instanceof IWirelessGenerator gen) {
                    boolean ok = WirelessSystem.linkGenerator(level, iNode, gen, needAuth, pass);
                    if (ok) {
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§a发电机已连接到节点"));
                    } else {
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§c连接失败，请检查密码和距离"));
                    }
                } else if (machineBe instanceof IWirelessReceiver rec) {
                    boolean ok = WirelessSystem.linkReceiver(level, iNode, rec, needAuth, pass);
                    if (ok) {
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§a机器已连接到节点"));
                    } else {
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§c连接失败，请检查密码和距离"));
                    }
                } else {
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§c该机器不支持无线连接"));
                }
            }
        });
    }
}
