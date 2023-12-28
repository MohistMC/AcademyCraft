package com.mohistmc.academy.world.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

public class ImagsilOre extends Block {

    public ImagsilOre() {
        super(Properties.of()
                .sound(SoundType.STONE)
                .noOcclusion()
                .strength(4.0f)
                .requiresCorrectToolForDrops()
        );

    }
}
