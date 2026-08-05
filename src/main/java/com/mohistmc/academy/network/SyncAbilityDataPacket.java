package com.mohistmc.academy.network;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.PlayerAbilityData;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;


/**
 * 服务端→客户端：同步玩家能力数据。
 */
public record SyncAbilityDataPacket(CompoundTag data) implements CustomPacketPayload {

    public static final Type<SyncAbilityDataPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "sync_ability"));

    public static final StreamCodec<ByteBuf, SyncAbilityDataPacket> STREAM_CODEC =
            ByteBufCodecs.COMPOUND_TAG.map(SyncAbilityDataPacket::new, SyncAbilityDataPacket::data);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncAbilityDataPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            PlayerAbilityData abilityData = PlayerAbilityData.fromSyncTag(packet.data());
            player.setData(AcademyAttachments.PLAYER_ABILITY, abilityData);
        });
    }
}
