package com.mohistmc.academy.listener;

import com.mohistmc.academy.client.block.gui.NodeBasicGui;
import com.mohistmc.academy.client.block.gui.WindBaseGui;
import com.mohistmc.academy.client.block.gui.WindMainGui;
import com.mohistmc.academy.world.AcademyMenus;
import com.mohistmc.academy.world.provider.AcademyBlockTagsProvider;
import com.mojang.logging.LogUtils;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.slf4j.Logger;

/**
 * 通用事件监听器
 *
 * @author lliiooll
 */
public class CommonListener {

    private static CommonListener INSTANCE = null;
    private static final Logger LOGGER = LogUtils.getLogger();
    private IEventBus modEventBus;

    public static CommonListener getInstance() {
        if (INSTANCE == null) INSTANCE = new CommonListener();
        return INSTANCE;
    }

    /**
     * 初始化事件
     */
    public void init(FMLJavaModLoadingContext context) {
        if (this.modEventBus == null) {
            this.modEventBus = context.getModEventBus();
        }
        this.modEventBus.addListener(this::commonSetup);
        this.modEventBus.addListener(this::gatherData);
        this.modEventBus.addListener(this::clientSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        gen.addProvider(event.includeServer(), new AcademyBlockTagsProvider(packOutput, lookupProvider, existingFileHelper));

    }

    /**
     * 初始化事件
     *
     * @param event
     */
    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(AcademyMenus.WIND_BASE_MENU.get(), WindBaseGui::new);
            MenuScreens.register(AcademyMenus.WIND_MAIN_MENU.get(), WindMainGui::new);
            MenuScreens.register(AcademyMenus.NODE_BASIC.get(), NodeBasicGui::new);
        });
    }

    public IEventBus getModEventBus() {
        return modEventBus;
    }

}
