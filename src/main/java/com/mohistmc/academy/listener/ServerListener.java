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
 * @author lliiooll
 */
public class ServerListener {

    private static final Logger LOGGER = LogUtils.getLogger();

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(
                Commands.literal("academy")
                        .requires(source -> source.hasPermission(2))
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
