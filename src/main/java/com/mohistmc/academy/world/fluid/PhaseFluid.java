package com.mohistmc.academy.world.fluid;

import com.mohistmc.academy.world.AcademyBlocks;
import com.mohistmc.academy.world.AcademyFluidTypes;
import com.mohistmc.academy.world.AcademyFluids;
import com.mohistmc.academy.world.AcademyItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidType;

import java.util.Optional;

public abstract class PhaseFluid extends FlowingFluid {

    @Override
    public Fluid getFlowing() {
        return AcademyFluids.FLOWING_PHASE_LIQUID.get();
    }

    @Override
    public Fluid getSource() {
        return AcademyFluids.PHASE_LIQUID.get();
    }

    @Override
    protected boolean canConvertToSource(Level p_256009_) {
        return false;
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor p_76450_, BlockPos p_76451_, BlockState p_76452_) {
        BlockEntity blockentity = p_76452_.hasBlockEntity() ? p_76450_.getBlockEntity(p_76451_) : null;
        Block.dropResources(p_76452_, p_76450_, p_76451_, blockentity);
    }


    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Optional.of(SoundEvents.BUCKET_FILL);
    }

    @Override
    protected int getSlopeFindDistance(LevelReader p_76074_) {
        return 4;
    }

    @Override
    protected int getDropOff(LevelReader p_76087_) {
        return 1;
    }

    @Override
    public Item getBucket() {
        return AcademyItems.PHASE_BUCKET.get();
    }

    @Override
    public boolean canBeReplacedWith(FluidState p_76458_, BlockGetter p_76459_, BlockPos p_76460_, Fluid p_76461_, Direction p_76462_) {
        return p_76462_ == Direction.DOWN && !p_76461_.is(FluidTags.WATER);
    }

    @Override
    public int getTickDelay(LevelReader p_76120_) {
        return 5;
    }

    @Override
    protected float getExplosionResistance() {
        return 100.0F;
    }

    @Override
    protected BlockState createLegacyBlock(FluidState p_76136_) {
        return AcademyBlocks.PHASE_LIQUID.get().defaultBlockState().setValue(LiquidBlock.LEVEL, Integer.valueOf(getLegacyLevel(p_76136_)));
    }

    @Override
    public boolean isSame(Fluid p_76122_) {
        return p_76122_ == AcademyFluids.PHASE_LIQUID.get() || p_76122_ == AcademyFluids.FLOWING_PHASE_LIQUID.get();
    }

    @Override
    public FluidType getFluidType() {
        return AcademyFluidTypes.PHASE_LIQUID.get();
    }


    public static class Source extends PhaseFluid {
        @Override
        public boolean isSource(FluidState p_76140_) {
            return true;
        }

        @Override
        public int getAmount(FluidState p_164509_) {
            return 8;
        }
    }

    public static class Flowing extends PhaseFluid {
        @Override
        public boolean isSource(FluidState p_76140_) {
            return false;
        }

        @Override
        public int getAmount(FluidState p_164509_) {
            return p_164509_.getValue(LEVEL);
        }

        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> p_76046_) {
            super.createFluidStateDefinition(p_76046_);
            p_76046_.add(LEVEL);
        }

    }
}
