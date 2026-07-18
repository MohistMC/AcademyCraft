package com.mohistmc.academy.network;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.energy.impl.WirelessSystem;
import com.mohistmc.academy.world.block.entity.MatrixBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 客户端→服务端：初始化矩阵网络。
 *
 * @author Mgazul
 */
public record InitMatrixPacket(BlockPos pos, String ssid, String password) implements CustomPacketPayload {

    public static final Type<InitMatrixPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "init_matrix"));

    public static final StreamCodec<ByteBuf, InitMatrixPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, InitMatrixPacket::pos,
                    ByteBufCodecs.STRING_UTF8, InitMatrixPacket::ssid,
                    ByteBufCodecs.STRING_UTF8, InitMatrixPacket::password,
                    InitMatrixPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(InitMatrixPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                ServerLevel level = player.serverLevel();
                BlockEntity be = level.getBlockEntity(packet.pos());
                if (be instanceof MatrixBlockEntity matrix) {
                    // 检查所有者权限
                    if (!matrix.isOwner(player)) {
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§c只有矩阵所有者才能初始化！"));
                        return;
                    }
                    if (matrix.isInitialized()) {
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§c该矩阵已经初始化过了！"));
                        return;
                    }

                    matrix.setSSID(packet.ssid());
                    matrix.setPassword(packet.password());
                    matrix.applyCoreLevel(0); // 默认基础核心
                    matrix.setInitialized(true);

                    // 创建无线网络
                    boolean created = WirelessSystem.createNetwork(level, matrix, packet.ssid(), packet.password());
                    if (created) {
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§a矩阵初始化成功！网络: " + packet.ssid()));
                    } else {
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§e矩阵标记为已初始化，但网络创建可能存在冲突"));
                    }
                }
            }
        });
    }
}
