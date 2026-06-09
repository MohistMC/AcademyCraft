package com.mohistmc.academy.listener;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.client.KeyInputHandler;
import com.mohistmc.academy.client.block.entity.model.CatEngineModel;
import com.mohistmc.academy.client.block.entity.render.CatEngineRender;
import com.mohistmc.academy.client.block.entity.render.PhaseLiquidRender;
import com.mohistmc.academy.client.block.entity.render.WindGenFanRender;
import com.mohistmc.academy.client.effect.ElectroArcEntity;
import com.mohistmc.academy.client.effect.RippleMarkEntity;
import com.mohistmc.academy.client.effect.SpriteEffectEntity;
import com.mohistmc.academy.client.effect.WaveEffectEntity;
import com.mohistmc.academy.client.entity.CoinRenderer;
import com.mohistmc.academy.client.renderer.RailgunBeamRenderer;
import com.mohistmc.academy.world.AcademyBlockEntities;
import com.mohistmc.academy.world.AcademyEntities;
import com.mohistmc.academy.world.block.IDevMachine;
import com.mohistmc.academy.world.entity.OreHighlightEntity;
import com.mojang.logging.LogUtils;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import org.slf4j.Logger;

/**
 * 客户端事件监听器
 *
 * @author lliiooll
 */
@EventBusSubscriber(modid = AcademyCraft.MODID, value = Dist.CLIENT)
public class ClientListener {

    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        KeyInputHandler.register(event);
    }

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CatEngineModel.LAYER_LOCATION, CatEngineModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(AcademyBlockEntities.CAT_ENGINE.get(), CatEngineRender::new);
        event.registerBlockEntityRenderer(AcademyBlockEntities.PHASE_LIQUID.get(), PhaseLiquidRender::new);
        event.registerBlockEntityRenderer(AcademyBlockEntities.WINDGEN_FAN.get(), WindGenFanRender::new);

        event.registerEntityRenderer(AcademyEntities.COIN_ENTITY.get(), CoinRenderer::new);
        event.registerEntityRenderer(AcademyEntities.ORE_HIGHLIGHT.get(), OreHighlightEntity.Renderer::new);
        event.registerEntityRenderer(AcademyEntities.RAILGUN_BEAM.get(), RailgunBeamRenderer::new);
        event.registerEntityRenderer(AcademyEntities.WAVE_EFFECT.get(), WaveEffectEntity.Renderer::new);
        event.registerEntityRenderer(AcademyEntities.ELECTRO_ARC.get(), ElectroArcEntity.Renderer::new);
        event.registerEntityRenderer(AcademyEntities.RIPPLE_MARK.get(), RippleMarkEntity.Renderer::new);
        event.registerEntityRenderer(AcademyEntities.SPRITE_EFFECT.get(), SpriteEffectEntity.Renderer::new);
    }

    @SubscribeEvent
    public static void onBlockHighlight(RenderHighlightEvent.Block event) {
        BlockHitResult hitResult = event.getTarget();

        var level = event.getCamera().getEntity().level();
        BlockState state = level.getBlockState(hitResult.getBlockPos());
        if (state.getBlock() instanceof IDevMachine) {
          event.setCanceled(true);
        }
    }
}
