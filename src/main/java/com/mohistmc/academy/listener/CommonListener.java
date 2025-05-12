package com.mohistmc.academy.listener;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.client.block.gui.NodeBasicGui;
import com.mohistmc.academy.client.block.gui.WindBaseGui;
import com.mohistmc.academy.client.block.gui.WindMainGui;
import com.mohistmc.academy.world.AcademyMenus;
import com.mohistmc.academy.world.provider.AcademyBlockTagsProvider;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/**
 * 通用事件监听器
 *
 * @author lliiooll
 */
@EventBusSubscriber(modid = AcademyCraft.MODID, bus = EventBusSubscriber.Bus.MOD)
public class CommonListener {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        gen.addProvider(event.includeServer(), new AcademyBlockTagsProvider(packOutput, lookupProvider, existingFileHelper));

    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(AcademyMenus.WIND_BASE_MENU.get(), WindBaseGui::new);
        event.register(AcademyMenus.WIND_MAIN_MENU.get(), WindMainGui::new);
        event.register(AcademyMenus.NODE_BASIC.get(), NodeBasicGui::new);
    }

}
