package cn.academy.worldgen;

import cn.academy.ACBlocks;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.ArrayUtils;

/**
 * AC Ore Generator
 * @author KSkun
 */
public class ACWorldGen implements IWorldGenerator {

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world,
                         IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if(WorldGenInit.GENERATE_ORES && world.provider.getDimensionType()== DimensionType.OVERWORLD) {
            genOverworld(world, random, chunkX * 16, chunkZ * 16);
        }
    }
    
    private List<CustomWorldGen> generators = Arrays.asList(
            new CustomWorldGen("reso_ore", new WorldGenMinable(ACBlocks.reso_ore.getDefaultState(), 9), 60, 8),
            new CustomWorldGen("constraint_metal", new WorldGenMinable(ACBlocks.constraint_metal.getDefaultState(),  12), 60, 8),
            new CustomWorldGen("crystal_ore", new WorldGenMinable(ACBlocks.crystal_ore.getDefaultState(), 12), 60, 12),
            new CustomWorldGen("imagsil_ore", new WorldGenMinable(ACBlocks.imagsil_ore.getDefaultState(),  11), 60, 8));
    
    private void genOverworld(World world, Random random, int x, int z) {
        for(CustomWorldGen gen : generators) {
            if (ArrayUtils.contains(WorldGenInit.ORES_WHITELISTED, gen.getKey())) {
                gen.generate(world, random, x, z);
            }
        }
    }
}