package com.mohistmc.academy.world.block;

import com.mohistmc.academy.world.AcademyItems;
import net.minecraft.core.BlockPos;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.data.tags.VanillaBlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.common.data.BlockTagsProvider;

import java.util.ArrayList;
import java.util.List;

public class ResoOre extends Block {

    public ResoOre() {
        super(Properties.of(Material.STONE)
                .sound(SoundType.STONE)
                .noOcclusion()
                .strength(4.0f)
                .requiresCorrectToolForDrops()
        );
    }

    @Override
    public List<ItemStack> getDrops(BlockState p_60537_, LootContext.Builder p_60538_) {
        return new ArrayList<>() {{
            add(new ItemStack(AcademyItems.RESO_CRYSTAL.get()));
        }};
    }



}
