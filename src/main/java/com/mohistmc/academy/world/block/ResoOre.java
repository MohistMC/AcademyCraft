package com.mohistmc.academy.world.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

public class ResoOre extends Block {

    public ResoOre() {
        super(Properties.of()
                .sound(SoundType.STONE)
                .noOcclusion()
                .strength(4.0f)
                .requiresCorrectToolForDrops()
        );
    }


}
