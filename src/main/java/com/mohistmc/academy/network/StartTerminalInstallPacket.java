package com.mohistmc.academy.network;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.client.TerminalInstallProgress;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 服务端→客户端：开始终端安装流程。
 */
public record StartTerminalInstallPacket() implements CustomPacketPayload {

    public static final StartTerminalInstallPacket INSTANCE = new StartTerminalInstallPacket();

    public static final Type<StartTerminalInstallPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "start_terminal_install"));

    public static final StreamCodec<ByteBuf, StartTerminalInstallPacket> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(StartTerminalInstallPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().isClientSide()) {
                TerminalInstallProgress.start();
            }
        });
    }
}
