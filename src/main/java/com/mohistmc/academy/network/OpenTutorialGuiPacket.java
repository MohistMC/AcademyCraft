package com.mohistmc.academy.network;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.client.gui.TutorialAppGui;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 服务端→客户端：打开教程界面。
 */
public record OpenTutorialGuiPacket() implements CustomPacketPayload {

    public static final OpenTutorialGuiPacket INSTANCE = new OpenTutorialGuiPacket();

    public static final Type<OpenTutorialGuiPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "open_tutorial_gui"));

    public static final StreamCodec<ByteBuf, OpenTutorialGuiPacket> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenTutorialGuiPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().isClientSide()) {
                Minecraft.getInstance().setScreen(new TutorialAppGui());
            }
        });
    }
}