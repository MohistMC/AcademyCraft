package com.mohistmc.academy.world.block.entity;

import com.mohistmc.academy.energy.api.block.IWirelessGenerator;
import com.mohistmc.academy.world.AcademyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 风力猫引擎 —— 实现 IWirelessGenerator 向无线能源网络供电。
 * @author Mgazul
 */
public class CatEngineBlockEntity extends BlockEntity implements IWirelessGenerator {

    public int time;
    public float rot;
    public float oRot;
    public float tRot;
    public boolean enable = false;
    public float rH = 0;

    private static final int MAX_STORAGE = 3000;
    private static final double BANDWIDTH = 30;
    private float storedEnergy = 0;

    public CatEngineBlockEntity(BlockPos pos, BlockState state) {
        super(AcademyBlockEntities.CAT_ENGINE.get(), pos, state);
    }

    // ==================== Animation ====================

    public static void tickAnim(Level level, BlockPos blockPos, BlockState blockState, CatEngineBlockEntity e) {
        e.oRot = e.rot;
        Player player = level.getNearestPlayer(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 10, false);
        if (player != null) {
            double d0 = player.getX() - ((double) blockPos.getX() + 0.5D);
            double d1 = player.getZ() - ((double) blockPos.getZ() + 0.5D);
            e.tRot = (float) Mth.atan2(d1, d0);
        }
        while (e.rot >= (float) Math.PI) e.rot -= ((float) Math.PI * 2F);
        while (e.rot < -(float) Math.PI) e.rot += ((float) Math.PI * 2F);
        while (e.tRot >= (float) Math.PI) e.tRot -= ((float) Math.PI * 2F);
        while (e.tRot < -(float) Math.PI) e.tRot += ((float) Math.PI * 2F);

        float f2;
        for (f2 = e.tRot - e.rot; f2 >= (float) Math.PI; f2 -= ((float) Math.PI * 2F)) {}
        while (f2 < -(float) Math.PI) f2 += ((float) Math.PI * 2F);
        e.rot += f2 * 0.4F;
        ++e.time;
    }

    /** 每 tick 发电 */
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        // 高处 + 有天空视野 = 发电更多
        float rate = 1.0f;
        if (level.canSeeSky(pos)) {
            rate += 1.5f;
        }
        rate += pos.getY() * 0.02f;

        storedEnergy = Math.min(MAX_STORAGE, storedEnergy + rate);
        if (storedEnergy > 0) setChanged();
    }

    // ==================== IWirelessGenerator ====================

    @Override
    public double getProvidedEnergy(double req) {
        double give = Math.min(req, storedEnergy);
        storedEnergy -= (float) give;
        if (give > 0) setChanged();
        return give;
    }

    @Override
    public double getBandwidth() {
        return BANDWIDTH;
    }

    public float getStoredEnergy() { return storedEnergy; }
    public int getMaxStorage() { return MAX_STORAGE; }

    // ==================== NBT ====================

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("storedEnergy")) storedEnergy = tag.getFloat("storedEnergy");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putFloat("storedEnergy", storedEnergy);
    }
}
