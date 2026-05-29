package com.mohistmc.academy.world;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.world.fluid.PhaseFluid;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AcademyFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, AcademyCraft.MODID);

    public static final DeferredHolder<Fluid, FlowingFluid> PHASE_LIQUID = FLUIDS.register("phase_liquid", PhaseFluid.Source::new);
    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_PHASE_LIQUID = FLUIDS.register("phase_liquid_flowing", PhaseFluid.Flowing::new);

}
