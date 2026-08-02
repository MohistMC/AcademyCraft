package com.mohistmc.academy.world;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.world.fluid.PhaseFluid;
import com.mohistmc.academy.world.fluidtype.PhaseLiquidType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class AcademyFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, AcademyCraft.MODID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, AcademyCraft.MODID);

    public static final DeferredHolder<FluidType, FluidType> PHASE_LIQUID_TYPE = FLUID_TYPES.register("phase_liquid_type", PhaseLiquidType::new);
    public static final DeferredHolder<Fluid, FlowingFluid> PHASE_LIQUID = FLUIDS.register("phase_liquid", PhaseFluid.Source::new);
    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_PHASE_LIQUID = FLUIDS.register("phase_liquid_flowing", PhaseFluid.Flowing::new);

}
