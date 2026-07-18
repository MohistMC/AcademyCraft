package com.mohistmc.academy.listener;

import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

/**
 * 服务器事件监听器
 *
 * @author lliiooll, Mgazul
 */
public class ServerListener {

    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(
                Commands.literal("aim")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("help")
                                .executes(context -> {
                                    context.getSource().sendSuccess(() -> Component.literal("§b===== AcademyCraft 能力指令 ====="), false);
                                    context.getSource().sendSuccess(() -> Component.literal("§7/aim help §f- 显示帮助"), false);
                                    context.getSource().sendSuccess(() -> Component.literal("§7/aim info §f- 查看当前能力信息"), false);
                                    context.getSource().sendSuccess(() -> Component.literal("§7/aim devmode §f- 切换开发者模式（解锁所有技能）"), false);
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                        .then(Commands.literal("info")
                                .executes(context -> {
                                    CommandSourceStack source = context.getSource();
                                    if (source.getEntity() instanceof net.minecraft.server.level.ServerPlayer player) {
                                        PlayerAbilityData data = player.getData(AcademyAttachments.PLAYER_ABILITY);
                                        source.sendSuccess(() -> Component.literal("§b===== 当前能力信息 ====="), false);
                                        if (data.hasAbility()) {
                                            source.sendSuccess(() -> Component.literal("§7能力: ")
                                                    .append(Component.translatable(data.getCurrentAbility().getTranslationKey())), false);
                                        } else {
                                            source.sendSuccess(() -> Component.literal("§7能力: §c无"), false);
                                        }
                                        source.sendSuccess(() -> Component.literal("§7等级: §f" + data.getPlayerLevel()), false);
                                        source.sendSuccess(() -> Component.literal("§7CP: §b" + (int) data.getCurrentCp() + "§7/§b" + (int) data.getMaxCp()), false);
                                        source.sendSuccess(() -> Component.literal("§7过载: §c" + (int) data.getCurrentOverload() + "§7/§c" + (int) data.getMaxOverload()), false);
                                        source.sendSuccess(() -> Component.literal("§7开发者模式: " + (data.isDevMode() ? "§a开启" : "§c关闭")), false);
                                        source.sendSuccess(() -> Component.literal("§7已学技能: §f" + data.getLearnedSkills().size()), false);
                                    }
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                        .then(Commands.literal("devmode")
                                .executes(context -> {
                                    CommandSourceStack source = context.getSource();
                                    if (source.getEntity() instanceof net.minecraft.server.level.ServerPlayer player) {
                                        PlayerAbilityData data = player.getData(AcademyAttachments.PLAYER_ABILITY);
                                        boolean newMode = !data.isDevMode();
                                        data.setDevMode(newMode);
                                        data.syncTo(player);
                                        if (newMode) {
                                            source.sendSuccess(() -> Component.literal("§a开发者模式已开启 — 所有技能已解锁"), false);
                                        } else {
                                            source.sendSuccess(() -> Component.literal("§c开发者模式已关闭 — 恢复正常的技能解锁逻辑"), false);
                                        }
                                    } else {
                                        source.sendFailure(Component.literal("§c该命令只能由玩家执行"));
                                    }
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
        );
    }
}
