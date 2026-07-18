package com.mohistmc.academy.network;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.capability.AcademyNode;
import com.mohistmc.academy.energy.api.block.IWirelessGenerator;
import com.mohistmc.academy.energy.api.block.IWirelessNode;
import com.mohistmc.academy.energy.api.block.IWirelessReceiver;
import com.mohistmc.academy.energy.impl.WiWorldData;
import com.mohistmc.academy.energy.impl.WirelessNet;
import io.netty.buffer.ByteBuf;
import java.util.Collection;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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
 *
 * @author Mgazul
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
                if (data == null) return;

                // 搜索附近节点 (范围64格)
                Collection<WirelessNet> nets = data.rangeSearch(
                        packet.machinePos().getX(),
                        packet.machinePos().getY(),
                        packet.machinePos().getZ(),
                        64, 32
                );

                // 构建节点列表数据
                CompoundTag response = new CompoundTag();
                ListTag nodeList = new ListTag();
                int index = 0;

                for (WirelessNet net : nets) {
                    CompoundTag nodeTag = new CompoundTag();
                    nodeTag.putString("name", net.getSSID());
                    nodeTag.putBoolean("needAuth", !net.getPassword().isEmpty());
                    nodeTag.putBoolean("isMatrix", true);
                    nodeTag.putInt("index", index++);
                    nodeList.add(nodeTag);
                }

                response.put("nodes", nodeList);
                PacketDistributor.sendToPlayer(player, new NodeListSyncPacket(response));
            }
        });
    }
}
