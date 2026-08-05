package com.mohistmc.academy.network;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.energy.api.block.IWirelessGenerator;
import com.mohistmc.academy.energy.api.block.IWirelessNode;
import com.mohistmc.academy.energy.api.block.IWirelessReceiver;
import com.mohistmc.academy.energy.impl.NodeConn;
import com.mohistmc.academy.energy.impl.WiWorldData;
import io.netty.buffer.ByteBuf;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 客户端→服务端：请求附近的无线节点列表。
 */
public record RequestNodesPacket(BlockPos machinePos) implements CustomPacketPayload {

    public static final Type<RequestNodesPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "request_nodes"));

    public static final StreamCodec<ByteBuf, RequestNodesPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, RequestNodesPacket::machinePos,
                    RequestNodesPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(RequestNodesPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                ServerLevel level = player.serverLevel();
                WiWorldData data = WiWorldData.getNonCreate(level);

                // 扫描附近所有的 IWirelessNode 方块（步长1，范围32格）
                Set<BlockPos> foundNodes = new HashSet<>();
                BlockPos center = packet.machinePos();
                int range = 32;

                for (int dx = -range; dx <= range; dx++) {
                    for (int dz = -range; dz <= range; dz++) {
                        int distXZ = dx * dx + dz * dz;
                        if (distXZ > range * range) continue;
                        int maxDy = (int) Math.sqrt(range * range - distXZ);
                        for (int dy = -maxDy; dy <= maxDy; dy++) {
                            BlockPos bp = center.offset(dx, dy, dz);
                            if (!level.isLoaded(bp)) continue;
                            BlockEntity be = level.getBlockEntity(bp);
                            if (be instanceof IWirelessNode) {
                                foundNodes.add(bp.immutable());
                            }
                        }
                    }
                }

                // 检查当前机器是否已连接到某个节点
                long connectedPos = 0;
                BlockEntity machineBe = level.getBlockEntity(packet.machinePos());
                if (machineBe != null && data != null) {
                    NodeConn existingConn = null;
                    if (machineBe instanceof IWirelessGenerator gen) {
                        existingConn = data.getNodeConnection(gen);
                    } else if (machineBe instanceof IWirelessReceiver rec) {
                        existingConn = data.getNodeConnection(rec);
                    }
                    if (existingConn != null) {
                        com.mohistmc.academy.energy.api.block.IWirelessNode node = existingConn.getNode();
                        if (node instanceof BlockEntity nodeBe) {
                            connectedPos = nodeBe.getBlockPos().asLong();
                        }
                    }
                }

                CompoundTag response = new CompoundTag();
                ListTag nodeList = new ListTag();
                int index = 0;
                int connectedIndex = -1;

                for (BlockPos nodePos : foundNodes) {
                    BlockEntity be = level.getBlockEntity(nodePos);
                    if (!(be instanceof IWirelessNode node)) continue;

                    CompoundTag nodeTag = new CompoundTag();
                    nodeTag.putString("name", node.getNodeName());
                    nodeTag.putBoolean("needAuth", !node.getPassword().isEmpty());
                    nodeTag.putLong("pos", nodePos.asLong());
                    nodeTag.putInt("index", index);

                    // 节点连接信息
                    NodeConn conn = data != null ? data.getNodeConnection(node) : null;
                    if (conn != null) {
                        nodeTag.putInt("load", conn.getLoad());
                        nodeTag.putInt("capacity", conn.getCapacity());
                    } else {
                        nodeTag.putInt("load", 0);
                        nodeTag.putInt("capacity", node.getCapacity());
                    }

                    nodeList.add(nodeTag);

                    if (nodePos.asLong() == connectedPos) {
                        connectedIndex = index;
                    }

                    index++;
                    if (index >= 32) break;
                }

                response.put("nodes", nodeList);
                response.putInt("connectedIndex", connectedIndex);
                PacketDistributor.sendToPlayer(player, new NodeListSyncPacket(response));
            }
        });
    }
}
