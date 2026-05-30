package com.mohistmc.academy.network;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.Skill;
import com.mohistmc.academy.skill.SkillEffect;
import com.mohistmc.academy.skill.SkillRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * @author Mgazul
 * @date 2026/5/30 21:52
 */
public record UseSkillPacket(String skillId) implements CustomPacketPayload {

    public static final Type<UseSkillPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "use_skill"));

    public static final StreamCodec<ByteBuf, UseSkillPacket> STREAM_CODEC =
            ByteBufCodecs.STRING_UTF8.map(UseSkillPacket::new, UseSkillPacket::skillId);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(UseSkillPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            PlayerAbilityData data = player.getData(AcademyAttachments.PLAYER_ABILITY);

            Skill skill = SkillRegistry.getSkill(packet.skillId());
            if (skill == null) return;

            if (!data.hasLearnedSkill(skill.getId())) {
                player.sendSystemMessage(Component.literal("§c尚未学习: " + skill.getId()));
                return;
            }

            if (!data.canUseSkill(skill)) {
                player.sendSystemMessage(Component.literal("§c计算力不足或过载过高"));
                return;
            }

            SkillEffect effect = skill.getEffect();
            if (effect == null) {
                player.sendSystemMessage(Component.literal("§c该技能暂无效果实现"));
                return;
            }

            data.useSkill(skill);
            effect.execute(player, data);

            player.setData(AcademyAttachments.PLAYER_ABILITY, data);
            LearnSkillPacket.syncToClient(player);
        });
    }
}
