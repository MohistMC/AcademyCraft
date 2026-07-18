package com.mohistmc.academy.worldgen;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.world.AcademyBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

/**
 * AcademyCraft 世界生成 —— 矿石和 PhaseLiquid 湖。
 * 使用 NeoForge 1.21.1 的 BiomeModifier / datapack 方式实现矿物生成。
 *
 * @author Mgazul
 */
@EventBusSubscriber(modid = AcademyCraft.MODID)
public final class AcademyWorldGen {

    private AcademyWorldGen() {}

    // ==================== Configured Feature Keys ====================

    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_IMAGSIL =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "ore_imagsil"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_RESO =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "ore_reso"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_CRYSTAL =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "ore_crystal"));

    // ==================== Datagen (bootstrap) ====================

    public static void bootstrapConfiguredFeatures(BootstrapContext<ConfiguredFeature<?, ?>> ctx) {
        // Imagsil Ore — 矿物生成配置，类似铁矿石
        ctx.register(ORE_IMAGSIL, new ConfiguredFeature<>(
                Feature.ORE,
                new OreConfiguration(
                        new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES),
                        AcademyBlocks.IMAGSIL_ORE.get().defaultBlockState(),
                        8  // vein size
                )
        ));

        // Reso Ore — 稍稀有
        ctx.register(ORE_RESO, new ConfiguredFeature<>(
                Feature.ORE,
                new OreConfiguration(
                        new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES),
                        AcademyBlocks.RESO_ORE.get().defaultBlockState(),
                        6
                )
        ));

        // Crystal Ore — 类似钻石矿
        ctx.register(ORE_CRYSTAL, new ConfiguredFeature<>(
                Feature.ORE,
                new OreConfiguration(
                        new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES),
                        AcademyBlocks.CRYSTAL_ORE.get().defaultBlockState(),
                        4
                )
        ));
    }

    // ==================== Events ====================

    @SubscribeEvent
    public static void onServerStart(ServerAboutToStartEvent event) {
        // 世界生成在 datapack 层面通过 BiomeModifier JSON 配置，
        // 这里主要记录日志确认生成已启用
    }
}
