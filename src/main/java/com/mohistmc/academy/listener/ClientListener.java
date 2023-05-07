package com.mohistmc.academy.listener;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.client.block.entity.PhaseLiquidBlockEntity;
import com.mohistmc.academy.client.block.entity.model.CatEngineModel;
import com.mohistmc.academy.client.block.entity.render.CatEngineRender;
import com.mohistmc.academy.client.block.entity.render.PhaseLiquidRender;
import com.mohistmc.academy.world.AcademyBlockEntities;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;

/**
 * 客户端事件监听器
 *
 * @author lliiooll
 */
@Mod.EventBusSubscriber(modid = AcademyCraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientListener {

    private static ClientListener INSTANCE = null;
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        LOGGER.info("HELLO FROM CLIENT SETUP");
        LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    //将所有的生物的皮肤贴图信息写在这个函数里，有几个写几个
    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CatEngineModel.LAYER_LOCATION, CatEngineModel::createBodyLayer);
    }

    //将所有的生物的渲染信息写在这个函数里，有几个写几个
    @SubscribeEvent
    public static void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(AcademyBlockEntities.CAT_ENGINE.get(), CatEngineRender::new);
        event.registerBlockEntityRenderer(AcademyBlockEntities.PHASE_LIQUID.get(), PhaseLiquidRender::new);
    }


    public static ClientListener getInstance() {
        if (INSTANCE == null) INSTANCE = new ClientListener();
        return INSTANCE;
    }
}
