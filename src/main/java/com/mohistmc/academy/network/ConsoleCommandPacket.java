package com.mohistmc.academy.network;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.skill.AbilityCategory;
import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.world.block.entity.DevAdvancedBlockEntity;
import com.mohistmc.academy.world.item.BaseFactor;
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
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 客户端→服务端：高级开发机控制台命令。
 *
 * @author Mgazul
 */
public record ConsoleCommandPacket(BlockPos pos, String command) implements CustomPacketPayload {

    public static final Type<ConsoleCommandPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "console_command"));

    public static final StreamCodec<ByteBuf, ConsoleCommandPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, ConsoleCommandPacket::pos,
                    ByteBufCodecs.STRING_UTF8, ConsoleCommandPacket::command,
                    ConsoleCommandPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ConsoleCommandPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            Level level = player.level();
            BlockEntity be = level.getBlockEntity(packet.pos());

            if (!(be instanceof DevAdvancedBlockEntity dev)) {
                player.sendSystemMessage(Component.literal("§c未找到高级能力开发机"));
                return;
            }

            PlayerAbilityData data = player.getData(AcademyAttachments.PLAYER_ABILITY);

            switch (packet.command()) {
                case "learn" -> {
                    // 打开技能树GUI (通过现有机制)
                    // 由于 DevMachineBase 已经有通过 OpenDevGuiPacket 打开技能树的逻辑，
                    // 这里直接触发相同的流程
                    player.sendSystemMessage(Component.literal("§a请在技能树界面选择要学习的技能"));
                }
                case "reset" -> {
                    // 检查条件：高级开发机 + 线圈 + 因子 + 至少 Level 2
                    if (!dev.isReadyForReset()) {
                        player.sendSystemMessage(Component.literal("§c重置失败：请放入高压磁增幅线圈和能力诱导因子"));
                        return;
                    }
                    if (data.getPlayerLevel() < 2) {
                        player.sendSystemMessage(Component.literal("§c重置失败：至少需要异能力者（Level 2）"));
                        return;
                    }
                    if (!data.hasAbility()) {
                        player.sendSystemMessage(Component.literal("§c你没有可以重置的能力"));
                        return;
                    }

                    // 获取因子中存储的能力类型
                    ItemStack factorStack = dev.getItems().get(1);
                    if (!(factorStack.getItem() instanceof BaseFactor factor)) {
                        player.sendSystemMessage(Component.literal("§c请放入能力诱导因子"));
                        return;
                    }

                    AbilityCategory newAbility = factor.getCategory();
                    if (newAbility == data.getCurrentAbility()) {
                        player.sendSystemMessage(Component.literal("§c目标和当前能力相同，无需重置"));
                        return;
                    }

                    // 消耗线圈和因子
                    dev.getItems().get(0).shrink(1);
                    dev.getItems().get(1).shrink(1);
                    dev.setChanged();

                    // 执行重置：保留等级-1，更换能力
                    int newLevel = Math.max(1, data.getPlayerLevel() - 1);
                    data.reset();
                    data.setCurrentAbility(newAbility);
                    data.setPlayerLevel(newLevel);
                    data.syncTo(player);

                    player.sendSystemMessage(Component.literal("§a能力重置成功！"));
                    player.sendSystemMessage(Component.literal("§7当前能力: ")
                            .append(Component.translatable(newAbility.getTranslationKey())));
                    player.sendSystemMessage(Component.literal("§7当前等级: Level " + newLevel));
                }
                default -> {
                    player.sendSystemMessage(Component.literal("§c未知命令: " + packet.command()));
                    player.sendSystemMessage(Component.literal("§7可用命令: learn, reset"));
                }
            }
        });
    }
}
