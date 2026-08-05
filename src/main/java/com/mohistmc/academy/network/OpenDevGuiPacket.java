package com.mohistmc.academy.network;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.client.gui.SkillTreeGui;
import com.mohistmc.academy.world.block.DevMachineType;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 服务端→客户端：打开开发机技能树界面。
 */
public record OpenDevGuiPacket(int typeOrdinal, int energy, int maxEnergy, Optional<BlockPos> mainPos) implements CustomPacketPayload {

    public static final Type<OpenDevGuiPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "open_dev_gui"));

    public static final StreamCodec<ByteBuf, OpenDevGuiPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, OpenDevGuiPacket::typeOrdinal,
                    ByteBufCodecs.INT, OpenDevGuiPacket::energy,
                    ByteBufCodecs.INT, OpenDevGuiPacket::maxEnergy,
                    ByteBufCodecs.optional(BlockPos.STREAM_CODEC), OpenDevGuiPacket::mainPos,
                    OpenDevGuiPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenDevGuiPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().isClientSide()) {
                DevMachineType devType = DevMachineType.fromOrdinal(packet.typeOrdinal());
                BlockPos pos = packet.mainPos().orElse(null);
                Minecraft.getInstance().setScreen(new SkillTreeGui(false, false, devType, packet.energy(), packet.maxEnergy(), pos));
            }
        });
    }
}
