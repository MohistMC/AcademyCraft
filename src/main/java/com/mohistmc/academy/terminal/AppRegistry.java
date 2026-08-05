package com.mohistmc.academy.terminal;

import com.mojang.logging.LogUtils;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;

public class AppRegistry {

    public static final BuiltinApp SKILL_TREE = new BuiltinApp("skill_tree", "item.academy.app_skill_tree");
    public static final BuiltinApp FREQ_TRANSMITTER = new BuiltinApp("freq_transmitter", "item.academy.app_freq_transmitter");
    public static final BuiltinApp MEDIA_PLAYER = new BuiltinApp("media_player", "item.academy.app_media_player");
    public static final BuiltinApp TUTORIAL = new BuiltinApp("tutorial", "item.academy.app_tutorial");
    public static final BuiltinApp SETTINGS = new BuiltinApp("settings", "item.academy.app_settings");

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

    public static void bindOpenAction(BuiltinApp app, Consumer<Minecraft> action) {
        app.setOpenAction(action);
    }

    private static void registerBuiltins() {
        register(SKILL_TREE);
        register(FREQ_TRANSMITTER);
        register(MEDIA_PLAYER);
        register(TUTORIAL);
        register(SETTINGS);
    }
}
