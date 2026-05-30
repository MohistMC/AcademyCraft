package com.mohistmc.academy;

import com.mohistmc.academy.listener.ServerListener;
import com.mohistmc.academy.network.LearnSkillPacket;
import com.mohistmc.academy.network.OpenDevGuiPacket;
import com.mohistmc.academy.network.SetSkillSlotPacket;
import com.mohistmc.academy.network.SyncAbilityDataPacket;
import com.mohistmc.academy.network.ToggleAbilityPacket;
import com.mohistmc.academy.network.UseSkillPacket;
import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.SkillRegistry;
import com.mohistmc.academy.terminal.AppRegistry;
import com.mohistmc.academy.world.AcademyBlockEntities;
import com.mohistmc.academy.world.AcademyBlocks;
import com.mohistmc.academy.world.AcademyEntities;
import com.mohistmc.academy.world.AcademyFluidTypes;
import com.mohistmc.academy.world.AcademyFluids;
import com.mohistmc.academy.world.AcademyItems;
import com.mohistmc.academy.world.AcademyMenus;
import com.mohistmc.academy.world.AcademySounds;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
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
        AcademyFluidTypes.FLUID_TYPES.register(modEventBus);
        AcademyFluids.FLUIDS.register(modEventBus);
        AcademyEntities.ENTITIES.register(modEventBus);
        AcademyBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        AcademySounds.SOUND_EVENTS.register(modEventBus);
        AcademyAttachments.ATTACHMENT_TYPES.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerPayloads);

        NeoForge.EVENT_BUS.register(new ServerListener());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        SkillRegistry.init();
        AppRegistry.init();
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
    }
}
