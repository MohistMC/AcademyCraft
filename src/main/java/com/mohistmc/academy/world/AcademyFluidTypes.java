package com.mohistmc.academy.world;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.world.fluid.PhaseFluid;
import com.mohistmc.academy.world.fluidtype.PhaseLiquidType;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class AcademyFluidTypes {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, AcademyCraft.MODID);

    public static final RegistryObject<FluidType> PHASE_LIQUID = FLUID_TYPES.register("phase_liquid_type", PhaseLiquidType::new);
}
