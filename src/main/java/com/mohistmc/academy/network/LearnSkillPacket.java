package com.mohistmc.academy.network;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.capability.EnergyItemHelper;
import com.mohistmc.academy.capability.IFEnergyStorage;
import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.Skill;
import com.mohistmc.academy.skill.SkillRegistry;
import com.mohistmc.academy.world.block.DevMachineType;
import com.mohistmc.academy.world.block.entity.DevAdvancedBlockEntity;
import com.mohistmc.academy.world.block.entity.DevNormalBlockEntity;
import com.mohistmc.academy.world.item.DeveloperPortable;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record LearnSkillPacket(String skillId, int typeOrdinal) implements CustomPacketPayload {

    public static final Type<LearnSkillPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "learn_skill"));

    public static final StreamCodec<ByteBuf, LearnSkillPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    LearnSkillPacket::skillId,
                    ByteBufCodecs.INT,
                    LearnSkillPacket::typeOrdinal,
                    LearnSkillPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(LearnSkillPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            PlayerAbilityData data = player.getData(AcademyAttachments.PLAYER_ABILITY);
            DevMachineType devType = DevMachineType.fromOrdinal(packet.typeOrdinal());

            Skill skill = SkillRegistry.getSkill(packet.skillId());
            if (skill == null) {
                player.sendSystemMessage(Component.literal("§c未知技能: " + packet.skillId()));
                return;
            }

            if (data.hasLearnedSkill(skill.getId())) {
                player.sendSystemMessage(Component.literal("§c已学习: ").append(Component.translatable(skill.getTranslationKey())));
                return;
            }

            if (!data.canLearnSkill(skill)) {
                player.sendSystemMessage(Component.literal("§c前置条件未满足: ").append(Component.translatable(skill.getTranslationKey())));
                return;
            }

            // 检查等级限制
            if (skill.getLevel() > devType.maxLevel) {
                player.sendSystemMessage(Component.literal("§c同步率不足，该开发机无法支持 Lv." + skill.getLevel() + " 技能"));
                return;
            }

            // 计算并消耗能量
            int baseCost = getSkillCost(skill);
            int actualCost = devType.applySyncRate(baseCost);

            boolean success = consumeEnergy(player, devType, actualCost);
            if (!success) {
                player.sendSystemMessage(Component.literal("§c能量不足! 需要 " + actualCost + " IF"));
                return;
            }

            data.learnSkill(skill.getId());

            switch (skill.getId()) {
                case "brain_course" -> data.addMaxCp(1000);
                case "brain_course_advanced" -> {
                    data.addMaxCp(1000);
                    data.addMaxOverload(100);
                }
                case "mind_course" -> data.addCpRegenRate(0.1f);
            }

            int newLevel = data.computeEffectiveLevel();
            if (newLevel > data.getPlayerLevel()) {
                data.setPlayerLevel(newLevel);
                player.sendSystemMessage(Component.literal("§a等级提升! 当前等级 " + newLevel));
            }

            data.syncTo(player);
            player.sendSystemMessage(Component.literal("§a已学习: ").append(Component.translatable(skill.getTranslationKey())));
            player.sendSystemMessage(Component.literal("§7消耗能量: " + actualCost + " IF (" + devType.displayName + " 同步率 " + devType.syncRate + "%)"));
        });
    }

    private static boolean consumeEnergy(ServerPlayer player, DevMachineType devType, int cost) {
        if (devType == DevMachineType.PORTABLE) {
            ItemStack mainHand = player.getMainHandItem();
            if (mainHand.getItem() instanceof DeveloperPortable) {
                int energy = EnergyItemHelper.getEnergy(mainHand);
                if (energy < cost) {
                    return false;
                }
                EnergyItemHelper.extractEnergy(mainHand, cost, false);
                return true;
            }
            return false;
        } else {
            IFEnergyStorage devMachine = findNearbyDevMachine(player);
            if (devMachine == null) {
                return false;
            }
            if (devMachine.getEnergyStored() < cost) {
                return false;
            }
            devMachine.extractEnergy(cost, false);
            return true;
        }
    }

    private static IFEnergyStorage findNearbyDevMachine(ServerPlayer player) {
        BlockPos playerPos = player.blockPosition();
        Level level = player.level();
        for (int dx = -5; dx <= 5; dx++) {
            for (int dy = -5; dy <= 5; dy++) {
                for (int dz = -5; dz <= 5; dz++) {
                    BlockPos checkPos = new BlockPos(playerPos.getX() + dx, playerPos.getY() + dy, playerPos.getZ() + dz);
                    BlockEntity be = level.getBlockEntity(checkPos);
                    if (be instanceof DevNormalBlockEntity || be instanceof DevAdvancedBlockEntity) {
                        return (IFEnergyStorage) be;
                    }
                }
            }
        }
        return null;
    }

    private static int getSkillCost(Skill skill) {
        return 100 + skill.getLevel() * 50;
    }

    public static void syncToClient(ServerPlayer player) {
        PlayerAbilityData data = player.getData(AcademyAttachments.PLAYER_ABILITY);
        PacketDistributor.sendToPlayer(player, new SyncAbilityDataPacket(data.toSyncTag()));
    }
}
