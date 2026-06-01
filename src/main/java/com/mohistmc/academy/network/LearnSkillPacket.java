package com.mohistmc.academy.network;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.Skill;
import com.mohistmc.academy.skill.SkillRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record LearnSkillPacket(String skillId) implements CustomPacketPayload {

    public static final Type<LearnSkillPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "learn_skill"));

    public static final StreamCodec<ByteBuf, LearnSkillPacket> STREAM_CODEC =
            ByteBufCodecs.STRING_UTF8.map(LearnSkillPacket::new, LearnSkillPacket::skillId);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(LearnSkillPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            PlayerAbilityData data = player.getData(AcademyAttachments.PLAYER_ABILITY);

            Skill skill = SkillRegistry.getSkill(packet.skillId());
            if (skill == null) {
                player.sendSystemMessage(Component.literal("§c未知技能: " + packet.skillId()));
                return;
            }

            if (data.hasLearnedSkill(skill.getId())) {
                player.sendSystemMessage(Component.translatable("§c已学习: ").append(Component.translatable(skill.getTranslationKey())));
                return;
            }

            if (!data.canLearnSkill(skill)) {
                player.sendSystemMessage(Component.translatable("§c前置条件未满足: ").append(Component.translatable(skill.getTranslationKey())));
                return;
            }

            data.learnSkill(skill.getId());

            if (skill.getId().equals("brain_course")) {
                data.addMaxCp(1000);
            } else if (skill.getId().equals("brain_course_advanced")) {
                data.addMaxCp(1000);
                data.addMaxOverload(100);
            } else if (skill.getId().equals("mind_course")) {
                data.addCpRegenRate(0.1f);
            }

            int newLevel = data.computeEffectiveLevel();
            if (newLevel > data.getPlayerLevel()) {
                data.setPlayerLevel(newLevel);
                player.sendSystemMessage(Component.literal("§a等级提升! 当前等级 " + newLevel));
            }

            data.syncTo(player);
            player.sendSystemMessage(Component.translatable("§a已学习: ").append(Component.translatable(skill.getTranslationKey())));
        });
    }

    public static void syncToClient(ServerPlayer player) {
        PlayerAbilityData data = player.getData(AcademyAttachments.PLAYER_ABILITY);
        PacketDistributor.sendToPlayer(player, new SyncAbilityDataPacket(data.toSyncTag()));
    }
}
