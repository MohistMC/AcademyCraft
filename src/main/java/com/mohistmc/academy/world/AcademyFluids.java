package com.mohistmc.academy.world;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.world.fluid.PhaseFluid;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AcademyFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, AcademyCraft.MODID);

    public static final RegistryObject<FlowingFluid> PHASE_LIQUID = FLUIDS.register("phase_liquid", PhaseFluid.Source::new);
    public static final RegistryObject<FlowingFluid> FLOWING_PHASE_LIQUID = FLUIDS.register("phase_liquid_flowing", PhaseFluid.Flowing::new);

}
