package com.mohistmc.academy.network;


import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.client.gui.SkillTreeGui;
import com.mohistmc.academy.world.block.DevMachineType;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * @author Mgazul
 * @date 2026/5/31 00:46
 */
public record OpenDevGuiPacket(int typeOrdinal) implements CustomPacketPayload {

    public static final OpenDevGuiPacket PORTABLE = new OpenDevGuiPacket(0);
    public static final OpenDevGuiPacket NORMAL = new OpenDevGuiPacket(1);
    public static final OpenDevGuiPacket ADVANCED = new OpenDevGuiPacket(2);

    public static final Type<OpenDevGuiPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "open_dev_gui"));

    public static final StreamCodec<ByteBuf, OpenDevGuiPacket> STREAM_CODEC =
            ByteBufCodecs.INT.map(OpenDevGuiPacket::new, OpenDevGuiPacket::typeOrdinal);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenDevGuiPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().isClientSide()) {
                DevMachineType devType = DevMachineType.fromOrdinal(packet.typeOrdinal());
                Minecraft.getInstance().setScreen(new SkillTreeGui(false, false, devType));
            }
        });
    }
}