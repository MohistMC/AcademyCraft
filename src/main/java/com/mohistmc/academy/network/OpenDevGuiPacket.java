package com.mohistmc.academy.network;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.client.gui.SkillTreeGui;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * @author Mgazul
 * @date 2026/5/31 00:46
 */
public record OpenDevGuiPacket() implements CustomPacketPayload {

    public static final OpenDevGuiPacket INSTANCE = new OpenDevGuiPacket();

    public static final Type<OpenDevGuiPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "open_dev_gui"));

    public static final StreamCodec<ByteBuf, OpenDevGuiPacket> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenDevGuiPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().isClientSide()) {
                Minecraft.getInstance().setScreen(new SkillTreeGui());
            }
        });
    }
}
