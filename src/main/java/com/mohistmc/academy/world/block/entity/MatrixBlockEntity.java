package com.mohistmc.academy.world.block.entity;

import com.mohistmc.academy.energy.api.block.IWirelessMatrix;
import com.mohistmc.academy.world.AcademyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 无线矩阵方块实体 —— 无线能源网络的核心。
 * 实现 IWirelessMatrix 接口以参与 IF 能源系统。
 *
 * @author Mgazul
 */
public class MatrixBlockEntity extends BlockEntity implements IWirelessMatrix {

    private static final int DEFAULT_CAPACITY = 5;
    private static final double DEFAULT_BANDWIDTH = 100;
    private static final double DEFAULT_RANGE = 25;

    private String ssid = "";
    private int capacity = DEFAULT_CAPACITY;
    private double bandwidth = DEFAULT_BANDWIDTH;
    private double range = DEFAULT_RANGE;

    public MatrixBlockEntity(BlockPos pos, BlockState state) {
        super(AcademyBlockEntities.MATRIX.get(), pos, state);
    }

    // ==================== IWirelessMatrix ====================

    @Override
    public int getCapacity() { return capacity; }

    @Override
    public double getBandwidth() { return bandwidth; }

    @Override
    public double getRange() { return range; }

    // ==================== 自定义属性 ====================

    public String getSSID() { return ssid; }
    public void setSSID(String ssid) { this.ssid = ssid; setChanged(); }

    public void setCapacity(int capacity) { this.capacity = capacity; setChanged(); }
    public void setBandwidth(double bandwidth) { this.bandwidth = bandwidth; setChanged(); }
    public void setRange(double range) { this.range = range; setChanged(); }

    // ==================== NBT ====================

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("ssid")) ssid = tag.getString("ssid");
        if (tag.contains("capacity")) capacity = tag.getInt("capacity");
        if (tag.contains("bandwidth")) bandwidth = tag.getDouble("bandwidth");
        if (tag.contains("matrix_range")) range = tag.getDouble("matrix_range");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putString("ssid", ssid);
        tag.putInt("capacity", capacity);
        tag.putDouble("bandwidth", bandwidth);
        tag.putDouble("matrix_range", range);
    }
}
