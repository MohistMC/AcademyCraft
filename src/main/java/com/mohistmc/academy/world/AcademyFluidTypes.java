package com.mohistmc.academy.world;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.world.fluidtype.PhaseLiquidType;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;


public class AcademyFluidTypes {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(Registries.FLUID, AcademyCraft.MODID);

    public static final Supplier<FluidType> PHASE_LIQUID = FLUID_TYPES.register("phase_liquid_type", PhaseLiquidType::new);
}
