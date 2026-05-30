package com.mohistmc.academy.terminal;

import com.mojang.logging.LogUtils;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

/**
 * @author Mgazul
 * @date 2026/5/31 04:09
 */
public class AppRegistry {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<String, TerminalApp> APPS = new LinkedHashMap<>();
    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;
        initialized = true;
        registerBuiltins();
        LOGGER.info("AcademyCraft AppRegistry initialized with {} apps", APPS.size());
    }

    public static void register(TerminalApp app) {
        APPS.put(app.getAppId(), app);
    }

    public static TerminalApp getApp(String appId) {
        return APPS.get(appId);
    }

    public static List<TerminalApp> getAllApps() {
        return List.copyOf(APPS.values());
    }

    public static boolean isRegistered(String appId) {
        return APPS.containsKey(appId);
    }

    public static void bindOpenAction(String appId, java.util.function.Consumer<net.minecraft.client.Minecraft> action) {
        TerminalApp app = APPS.get(appId);
        if (app instanceof BuiltinApp builtin) {
            builtin.setOpenAction(action);
        }
    }

    private static void registerBuiltins() {
        register(new BuiltinApp("skill_tree", "item.academy.app_skill_tree", "◆"));
        register(new BuiltinApp("freq_transmitter", "item.academy.app_freq_transmitter", "⚡"));
        register(new BuiltinApp("media_player", "item.academy.app_media_player", "♫"));
        register(new BuiltinApp("tutorial", "item.academy.app_tutorial", "☁"));
        register(new BuiltinApp("settings", "item.academy.app_settings", "⚙"));
    }
}
