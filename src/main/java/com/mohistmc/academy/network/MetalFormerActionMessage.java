package com.mohistmc.academy.network;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.world.block.entity.MetalFomerBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 客户端→服务端：切换金属成型机工作模式。
 */
public record MetalFormerActionMessage(BlockPos pos, int delta) implements CustomPacketPayload {

    public static final Type<MetalFormerActionMessage> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "metal_former_action"));

    public static final StreamCodec<ByteBuf, MetalFormerActionMessage> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, MetalFormerActionMessage::pos,
                    ByteBufCodecs.VAR_INT, MetalFormerActionMessage::delta,
                    MetalFormerActionMessage::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void send(BlockPos pos, int delta) {
        PacketDistributor.sendToServer(new MetalFormerActionMessage(pos, delta));
    }

    public static void handle(MetalFormerActionMessage packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                // 距离校验，防止玩家隔墙/远程操控
                if (player.distanceToSqr(packet.pos().getX() + 0.5, packet.pos().getY() + 0.5,
                        packet.pos().getZ() + 0.5) > 64.0) return;
                BlockEntity be = player.level().getBlockEntity(packet.pos());
                if (be instanceof MetalFomerBlockEntity former) {
                    former.cycleMode(Integer.signum(packet.delta()));
                }
            }
        });
    }
}
