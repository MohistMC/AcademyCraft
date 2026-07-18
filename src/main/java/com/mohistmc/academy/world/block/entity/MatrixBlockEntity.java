package com.mohistmc.academy.world.block.entity;

import com.mohistmc.academy.energy.api.block.IWirelessMatrix;
import com.mohistmc.academy.world.AcademyBlockEntities;
import java.util.UUID;
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
    private String password = "";
    private int capacity = DEFAULT_CAPACITY;
    private double bandwidth = DEFAULT_BANDWIDTH;
    private double range = DEFAULT_RANGE;
    private UUID ownerUUID = null;
    private boolean initialized = false;

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
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; setChanged(); }

    public void setCapacity(int capacity) { this.capacity = capacity; setChanged(); }
    public void setBandwidth(double bandwidth) { this.bandwidth = bandwidth; setChanged(); }
    public void setRange(double range) { this.range = range; setChanged(); }

    // ==================== Owner ====================

    public UUID getOwnerUUID() { return ownerUUID; }
    public void setOwnerUUID(UUID uuid) { this.ownerUUID = uuid; setChanged(); }

    public boolean isOwner(net.minecraft.world.entity.player.Player player) {
        return ownerUUID == null || player.getUUID().equals(ownerUUID);
    }

    // ==================== Initialization ====================

    public boolean isInitialized() { return initialized; }
    public void setInitialized(boolean init) { this.initialized = init; setChanged(); }

    /**
     * 根据矩阵核心等级调整性能参数
     */
    public void applyCoreLevel(int coreLevel) {
        this.capacity = switch (coreLevel) {
            case 0 -> 5;   // 基础
            case 1 -> 10;  // 改良
            case 2 -> 20;  // 高级
            default -> 5;
        };
        this.bandwidth = switch (coreLevel) {
            case 0 -> 100;
            case 1 -> 250;
            case 2 -> 500;
            default -> 100;
        };
        this.range = switch (coreLevel) {
            case 0 -> 25;
            case 1 -> 50;
            case 2 -> 100;
            default -> 25;
        };
        setChanged();
    }

    // ==================== NBT ====================

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("ssid")) ssid = tag.getString("ssid");
        if (tag.contains("password")) password = tag.getString("password");
        if (tag.contains("capacity")) capacity = tag.getInt("capacity");
        if (tag.contains("bandwidth")) bandwidth = tag.getDouble("bandwidth");
        if (tag.contains("matrix_range")) range = tag.getDouble("matrix_range");
        if (tag.contains("ownerUUID")) ownerUUID = UUID.fromString(tag.getString("ownerUUID"));
        if (tag.contains("initialized")) initialized = tag.getBoolean("initialized");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putString("ssid", ssid);
        tag.putString("password", password);
        tag.putInt("capacity", capacity);
        tag.putDouble("bandwidth", bandwidth);
        tag.putDouble("matrix_range", range);
        if (ownerUUID != null) tag.putString("ownerUUID", ownerUUID.toString());
        tag.putBoolean("initialized", initialized);
    }
}
