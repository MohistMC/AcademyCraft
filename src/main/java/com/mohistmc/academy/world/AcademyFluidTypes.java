package com.mohistmc.academy.world;

import com.mohistmc.academy.AcademyCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.DeferredRegister;


public class AcademyFluidTypes {
    public static final DeferredRegister<Fluid> FLUID_TYPES = DeferredRegister.create(Registries.FLUID, AcademyCraft.MODID);

    //public static final Supplier<Fluid> PHASE_LIQUID = FLUID_TYPES.register("phase_liquid_type", PhaseLiquidType::new);
}
