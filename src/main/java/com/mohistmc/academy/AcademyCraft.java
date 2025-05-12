package com.mohistmc.academy;

import com.mohistmc.academy.listener.CommonListener;
import com.mohistmc.academy.world.AcademyBlockEntities;
import com.mohistmc.academy.world.AcademyBlocks;
import com.mohistmc.academy.world.AcademyEntities;
import com.mohistmc.academy.world.AcademyFluidTypes;
import com.mohistmc.academy.world.AcademyFluids;
import com.mohistmc.academy.world.AcademyItems;
import com.mohistmc.academy.world.AcademyMenus;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;


@Mod(AcademyCraft.MODID)
public class AcademyCraft {
    public static final String MODID = "academy";
    private static final Logger LOGGER = LogUtils.getLogger();

    public AcademyCraft(IEventBus modEventBus, ModContainer modContainer) {

        CommonListener listener = CommonListener.getInstance();
        listener.init(modEventBus);
        AcademyMenus.MENUS.register(modEventBus);
        AcademyItems.ITEMS.register(modEventBus);
        AcademyItems.TABS.register(modEventBus);
        AcademyBlocks.BLOCKS.register(modEventBus);
        AcademyFluidTypes.FLUID_TYPES.register(modEventBus);
        AcademyFluids.FLUIDS.register(modEventBus);
        AcademyEntities.ENTITIES.register(modEventBus);
        AcademyBlockEntities.BLOCK_ENTITIES.register(modEventBus);

    }


}
