package com.mohistmc.academy.listener;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.client.block.gui.DevAdvancedGui;
import com.mohistmc.academy.client.block.gui.ImagFusorGui;
import com.mohistmc.academy.client.block.gui.MatrixGui;
import com.mohistmc.academy.client.block.gui.MetalFomerGui;
import com.mohistmc.academy.client.block.gui.NodeAdvancedGui;
import com.mohistmc.academy.client.block.gui.NodeBasicGui;
import com.mohistmc.academy.client.block.gui.NodeStandardGui;
import com.mohistmc.academy.client.block.gui.PhaseGenGui;
import com.mohistmc.academy.client.block.gui.SolarGenGui;
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
 */
@EventBusSubscriber(modid = AcademyCraft.MODID)
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
        event.register(AcademyMenus.NODE_STANDARD_MENU.get(), NodeStandardGui::new);
        event.register(AcademyMenus.NODE_ADVANCED_MENU.get(), NodeAdvancedGui::new);
        event.register(AcademyMenus.IMAG_FUSOR_MENU.get(), ImagFusorGui::new);
        event.register(AcademyMenus.SOLAR_GEN_MENU.get(), SolarGenGui::new);
        event.register(AcademyMenus.PHASE_GEN_MENU.get(), PhaseGenGui::new);
        event.register(AcademyMenus.MATRIX_MENU.get(), MatrixGui::new);
        event.register(AcademyMenus.METAL_FORMER_MENU.get(), MetalFomerGui::new);
        event.register(AcademyMenus.DEV_ADVANCED_MENU.get(), DevAdvancedGui::new);
    }

}
