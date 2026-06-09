package com.mohistmc.academy.world.block.entity;

import com.mohistmc.academy.energy.api.block.IWirelessNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 无线节点基类 —— 实现 IWirelessNode 接口以参与 IF 能源系统。
 *
 * @author Mgazul
 */
public abstract class BaseNodeBlockEntity extends AcademyContainerBlockEntity implements IWirelessNode {

    private static final double DEFAULT_MAX_ENERGY = 5000;
    private static final double DEFAULT_BANDWIDTH = 50;

    private double energy = 0;
    private double maxEnergy = DEFAULT_MAX_ENERGY;
    private double bandwidth = DEFAULT_BANDWIDTH;
    private String nodeName = "Unnamed";
    private String password = "";

    public BaseNodeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

    /** 子类实现 — 返回节点等级对应的信号范围（IWirelessNode 接口） */
    @Override
    public abstract double getRange();

    /** 是否已连接到矩阵网络 */
    public boolean isConnected() {
        // TODO: 检查 WiWorldData 中是否存在此节点
        return false;
    }

    // ==================== IWirelessNode ====================

    @Override
    public double getMaxEnergy() { return maxEnergy; }

    @Override
    public double getEnergy() { return energy; }

    @Override
    public void setEnergy(double value) {
        this.energy = Math.max(0, Math.min(value, maxEnergy));
        setChanged();
    }

    @Override
    public double getBandwidth() { return bandwidth; }

    @Override
    public int getCapacity() {
        // 子类可覆盖以提供不同的负载容量
        return 5;
    }

    @Override
    public String getNodeName() { return nodeName; }

    @Override
    public String getPassword() { return password; }

    // ==================== Setters ====================

    public void setNodeName(String name) { this.nodeName = name; setChanged(); }
    public void setPassword(String password) { this.password = password; setChanged(); }
    public void setMaxEnergy(double maxEnergy) { this.maxEnergy = maxEnergy; setChanged(); }
    public void setBandwidth(double bandwidth) { this.bandwidth = bandwidth; setChanged(); }

    // ==================== NBT ====================

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("node_energy")) energy = tag.getDouble("node_energy");
        if (tag.contains("node_maxEnergy")) maxEnergy = tag.getDouble("node_maxEnergy");
        if (tag.contains("node_bandwidth")) bandwidth = tag.getDouble("node_bandwidth");
        if (tag.contains("node_name")) nodeName = tag.getString("node_name");
        if (tag.contains("node_pass")) password = tag.getString("node_pass");
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putDouble("node_energy", energy);
        tag.putDouble("node_maxEnergy", maxEnergy);
        tag.putDouble("node_bandwidth", bandwidth);
        tag.putString("node_name", nodeName);
        tag.putString("node_pass", password);
    }
}
