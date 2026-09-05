package com.mohistmc.academy.energy.impl;

import com.mohistmc.academy.energy.api.block.IWirelessMatrix;
import com.mohistmc.academy.energy.api.block.IWirelessNode;
import com.mohistmc.academy.energy.impl.VBlocks.VWMatrix;
import com.mohistmc.academy.energy.impl.VBlocks.VWNode;
import com.mohistmc.academy.utils.MathUtils;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

/**
 * 无线网络 —— 一个矩阵 + N 个节点，在节点间均衡能量。
 */
public class WirelessNet {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final int UPDATE_INTERVAL = 40;
    private static final double BUFFER_MAX = 2000;

    private final WiWorldData data;
    Level level;

    private final List<VWNode> nodes = new LinkedList<>();
    private final List<VWNode> toRemoveNodes = new ArrayList<>();
    private VWMatrix matrix;

    private String ssid;
    private String password;

    private double buffer;

    private boolean disposed = false;

    WirelessNet(WiWorldData data, VWMatrix matrix, String ssid, String pass) {
        this.data = data;
        this.matrix = matrix;
        this.ssid = ssid;
        this.password = pass;
    }

    WirelessNet(WiWorldData data, CompoundTag tag) {
        this.data = data;

        matrix = new VWMatrix(tag.getCompound("matrix"));

        ssid = tag.getString("ssid");
        password = tag.getString("password");
        buffer = tag.getDouble("buffer");

        ListTag list = tag.getList("list", 10); // 10 = TAG_Compound
        for (int i = 0; i < list.size(); ++i) {
            doAddNode(new VWNode(list.getCompound(i)));
        }
    }

    CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("matrix", matrix.toNBT());
        tag.putString("ssid", ssid);
        tag.putString("password", password);
        tag.putDouble("buffer", buffer);

        ListTag list = new ListTag();
        for (VWNode vn : nodes) {
            if (!vn.isLoaded(level) || vn.get(level) != null) {
                list.add(vn.toNBT());
            }
        }
        tag.put("list", list);

        return tag;
    }

    // ==================== Accessors ====================

    public String getSSID() { return ssid; }
    public void setSSID(String ssid) { this.ssid = ssid; }
    public String getPassword() { return password; }

    public boolean resetPassword(String np) {
        password = np;
        return true;
    }

    public boolean isDisposed() { return disposed; }

    public int getLoad() { return nodes.size(); }

    public int getCapacity() {
        IWirelessMatrix imat = matrix.get(level);
        return imat == null ? 0 : imat.getCapacity();
    }

    public IWirelessMatrix getMatrix() {
        return matrix.get(level);
    }

    // ==================== 生命周期 ====================

    void dispose() {
        disposed = true;
        data.markDirty();
    }

    void onCreate(WiWorldData data) {
        data.netLookup.put(matrix, this);
    }

    void onCleanup(WiWorldData data) {
        data.netLookup.remove(ssid);
        data.netLookup.remove(matrix);
        for (VWNode n : nodes) {
            data.netLookup.remove(n);
        }
    }

    // ==================== 节点管理 ====================

    boolean addNode(VWNode node, String password) {
        if (!password.equals(this.password))
            return false;
        if (getLoad() >= getCapacity())
            return false;

        IWirelessMatrix imat = matrix.get(level);
        if (imat == null) return false;

        double r = imat.getRange();
        if (node.distSq(matrix) > r * r)
            return false;

        // 检查节点是否已加入其他网络
        WirelessNet other = data.getNetwork(node.get(level));
        if (other != null) {
            other.removeNode(node);
        }

        doAddNode(node);
        return true;
    }

    private void doAddNode(VWNode node) {
        nodes.add(node);
        data.netLookup.put(node, this);
    }

    void removeNode(VWNode node) {
        toRemoveNodes.add(node);
    }

    // ==================== 验证 ====================

    boolean validate() {
        if (matrix.isLoaded(level)) {
            IWirelessMatrix mat = matrix.get(level);
            if (mat == null) {
                disposed = true;
            }
        }
        return !disposed;
    }

    boolean isInRange(int x, int y, int z) {
        IWirelessMatrix imat = matrix.get(level);
        if (imat == null) return false;
        double r = imat.getRange();
        return MathUtils.distanceSq(x, y, z, matrix.x, matrix.y, matrix.z) <= r * r;
    }

    // ==================== Tick ====================

    void tick() {
        validate();
        if (!matrix.isLoaded(level)) return;

        IWirelessMatrix imat = matrix.get(level);
        if (imat == null) {
            dispose();
            return;
        }

        // 随机打乱节点列表，避免总对同一个节点均衡
        Collections.shuffle(nodes);

        double sum = 0, maxSum = 0;
        boolean changed = false;
        for (VWNode vn : nodes) {
            if (vn.isLoaded(level)) {
                IWirelessNode node = vn.get(level);
                if (node == null) {
                    removeNode(vn);
                    changed = true;
                } else {
                    sum += node.getEnergy();
                    maxSum += node.getMaxEnergy();
                }
            }
        }

        // 清理待删除节点
        if (!toRemoveNodes.isEmpty()) {
            data.netLookup.keySet().removeAll(toRemoveNodes);
            nodes.removeAll(toRemoveNodes);
            toRemoveNodes.clear();
            changed = true;
        }

        double percent = maxSum > 0 ? sum / maxSum : 0;
        double transferLeft = imat.getBandwidth();

        for (VWNode vn : nodes) {
            if (!vn.isLoaded(level)) continue;
            IWirelessNode node = vn.get(level);
            if (node == null) continue;

            double cur = node.getEnergy();
            double targ = node.getMaxEnergy() * percent;
            double delta = targ - cur;
            delta = Math.signum(delta) * Math.min(Math.abs(delta),
                    Math.min(transferLeft, node.getBandwidth()));

            if (buffer + delta > BUFFER_MAX) {
                delta = BUFFER_MAX - buffer;
            } else if (buffer + delta < 0) {
                delta = -buffer;
            }

            transferLeft -= Math.abs(delta);
            buffer += delta;
            node.setEnergy(cur + delta);
            if (delta != 0) changed = true;

            if (transferLeft == 0) break;
        }

        if (changed) data.markDirty();
    }

    private void debug(Object msg) {
        LOGGER.debug("WN:{}", msg);
    }
}
