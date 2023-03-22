package com.mohistmc.academy;

import com.mohistmc.academy.listener.CommonListener;
import com.mohistmc.academy.world.AcademyBlockEntities;
import com.mohistmc.academy.world.AcademyBlocks;
import com.mohistmc.academy.world.AcademyEntities;
import com.mohistmc.academy.world.AcademyItems;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;


@Mod(AcademyCraft.MODID)
public class AcademyCraft {
    public static final String MODID = "academy";
    private static final Logger LOGGER = LogUtils.getLogger();

    public AcademyCraft() {
        CommonListener listener = CommonListener.getInstance();
        listener.init();
        AcademyItems.ITEMS.register(listener.getModEventBus());
        AcademyBlocks.BLOCKS.register(listener.getModEventBus());
        AcademyEntities.ENTITIES.register(listener.getModEventBus());
        AcademyBlockEntities.BLOCK_ENTITIES.register(listener.getModEventBus());
    }


}