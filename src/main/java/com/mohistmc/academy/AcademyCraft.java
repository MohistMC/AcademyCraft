package com.mohistmc.academy;

import com.mohistmc.academy.config.ACConfig;
import com.mohistmc.academy.crafting.AcademyRecipeSerializers;
import com.mohistmc.academy.crafting.AcademyRecipeTypes;
import com.mohistmc.academy.crafting.MFIFRecipes;
import com.mohistmc.academy.listener.ServerListener;
import com.mohistmc.academy.network.ConnectToNodePacket;
import com.mohistmc.academy.network.ConsoleCommandPacket;
import com.mohistmc.academy.network.DisconnectFromNodePacket;
import com.mohistmc.academy.network.InitMatrixPacket;
import com.mohistmc.academy.network.LearnSkillPacket;
import com.mohistmc.academy.network.MetalFormerActionMessage;
import com.mohistmc.academy.network.NodeConfigPacket;
import com.mohistmc.academy.network.NodeListSyncPacket;
import com.mohistmc.academy.network.OpenDevGuiPacket;
import com.mohistmc.academy.network.RequestNodesPacket;
import com.mohistmc.academy.network.OpenTutorialGuiPacket;
import com.mohistmc.academy.network.SetSkillSlotPacket;
import com.mohistmc.academy.network.SkillKeyDownPacket;
import com.mohistmc.academy.network.SkillKeyUpPacket;
import com.mohistmc.academy.network.StartTerminalInstallPacket;
import com.mohistmc.academy.network.SyncAbilityDataPacket;
import com.mohistmc.academy.network.SyncChargingStatePacket;
import com.mohistmc.academy.network.ToggleAbilityPacket;
import com.mohistmc.academy.network.UseSkillPacket;
import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.SkillRegistry;
import com.mohistmc.academy.terminal.AppRegistry;
import com.mohistmc.academy.terminal.MediaTrackRegistry;
import com.mohistmc.academy.world.AcademyBlockEntities;
import com.mohistmc.academy.world.AcademyBlocks;
import com.mohistmc.academy.world.AcademyEntities;
import com.mohistmc.academy.world.AcademyFluids;
import com.mohistmc.academy.world.AcademyItems;
import com.mohistmc.academy.world.AcademyMenus;
import com.mohistmc.academy.world.AcademySounds;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

@Mod(AcademyCraft.MODID)
public class AcademyCraft {
    public static final String MODID = "academy";
    private static final Logger LOGGER = LogUtils.getLogger();

    public AcademyCraft(IEventBus modEventBus, ModContainer modContainer) {

        AcademyMenus.MENUS.register(modEventBus);
        AcademyItems.ITEMS.register(modEventBus);
        AcademyItems.TABS.register(modEventBus);
        AcademyBlocks.BLOCKS.register(modEventBus);
        AcademyFluids.FLUID_TYPES.register(modEventBus);
        AcademyFluids.FLUIDS.register(modEventBus);
        AcademyEntities.ENTITIES.register(modEventBus);
        AcademyBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        AcademySounds.SOUND_EVENTS.register(modEventBus);
        AcademyAttachments.ATTACHMENT_TYPES.register(modEventBus);
        AcademyRecipeTypes.RECIPE_TYPES.register(modEventBus);
        AcademyRecipeSerializers.SERIALIZERS.register(modEventBus);

        // 注册配置
        modContainer.registerConfig(ModConfig.Type.SERVER, ACConfig.Server.SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, ACConfig.Client.SPEC);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerPayloads);
        modEventBus.addListener(this::registerCapabilities);

        NeoForge.EVENT_BUS.register(new ServerListener());
    }

    private void registerCapabilities(final RegisterCapabilitiesEvent event) {
        // 金属成型机：侧面自动输入输出
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                AcademyBlockEntities.METAL_FORMER.get(),
                (be, side) -> be.getHandlerForSide(side)
        );
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        SkillRegistry.init();
        AppRegistry.init();
        MediaTrackRegistry.init();
        MFIFRecipes.init();
        LOGGER.info("AcademyCraft Skill Registry initialized with {} skills", SkillRegistry.getAllSkills().size());
    }

    private void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(MODID).versioned("1.0");
        registrar.playToServer(
                LearnSkillPacket.TYPE,
                LearnSkillPacket.STREAM_CODEC,
                LearnSkillPacket::handle
        );
        registrar.playToServer(
                UseSkillPacket.TYPE,
                UseSkillPacket.STREAM_CODEC,
                UseSkillPacket::handle
        );
        registrar.playToServer(
                SetSkillSlotPacket.TYPE,
                SetSkillSlotPacket.STREAM_CODEC,
                SetSkillSlotPacket::handle
        );
        registrar.playToServer(
                ToggleAbilityPacket.TYPE,
                ToggleAbilityPacket.STREAM_CODEC,
                ToggleAbilityPacket::handle
        );
        registrar.playToClient(
                SyncAbilityDataPacket.TYPE,
                SyncAbilityDataPacket.STREAM_CODEC,
                SyncAbilityDataPacket::handle
        );
        registrar.playToClient(
                OpenDevGuiPacket.TYPE,
                OpenDevGuiPacket.STREAM_CODEC,
                OpenDevGuiPacket::handle
        );
        registrar.playToClient(
                OpenTutorialGuiPacket.TYPE,
                OpenTutorialGuiPacket.STREAM_CODEC,
                OpenTutorialGuiPacket::handle
        );
        registrar.playToClient(
                StartTerminalInstallPacket.TYPE,
                StartTerminalInstallPacket.STREAM_CODEC,
                StartTerminalInstallPacket::handle
        );
        registrar.playToServer(
                SkillKeyDownPacket.TYPE,
                SkillKeyDownPacket.STREAM_CODEC,
                SkillKeyDownPacket::handle
        );
        registrar.playToServer(
                SkillKeyUpPacket.TYPE,
                SkillKeyUpPacket.STREAM_CODEC,
                SkillKeyUpPacket::handle
        );
        registrar.playToClient(
                SyncChargingStatePacket.TYPE,
                SyncChargingStatePacket.STREAM_CODEC,
                SyncChargingStatePacket::handle
        );
        registrar.playToServer(
                InitMatrixPacket.TYPE,
                InitMatrixPacket.STREAM_CODEC,
                InitMatrixPacket::handle
        );
        registrar.playToServer(
                RequestNodesPacket.TYPE,
                RequestNodesPacket.STREAM_CODEC,
                RequestNodesPacket::handle
        );
        registrar.playToClient(
                NodeListSyncPacket.TYPE,
                NodeListSyncPacket.STREAM_CODEC,
                NodeListSyncPacket::handle
        );
        registrar.playToServer(
                ConnectToNodePacket.TYPE,
                ConnectToNodePacket.STREAM_CODEC,
                ConnectToNodePacket::handle
        );
        registrar.playToServer(
                DisconnectFromNodePacket.TYPE,
                DisconnectFromNodePacket.STREAM_CODEC,
                DisconnectFromNodePacket::handle
        );
        registrar.playToServer(
                ConsoleCommandPacket.TYPE,
                ConsoleCommandPacket.STREAM_CODEC,
                ConsoleCommandPacket::handle
        );
        registrar.playToServer(
                NodeConfigPacket.TYPE,
                NodeConfigPacket.STREAM_CODEC,
                NodeConfigPacket::handle
        );
        registrar.playToServer(
                MetalFormerActionMessage.TYPE,
                MetalFormerActionMessage.STREAM_CODEC,
                MetalFormerActionMessage::handle
        );
    }
}
