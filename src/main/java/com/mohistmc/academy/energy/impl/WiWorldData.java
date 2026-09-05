package com.mohistmc.academy.energy.impl;

import com.mohistmc.academy.energy.api.block.IWirelessGenerator;
import com.mohistmc.academy.energy.api.block.IWirelessMatrix;
import com.mohistmc.academy.energy.api.block.IWirelessNode;
import com.mohistmc.academy.energy.api.block.IWirelessReceiver;
import com.mohistmc.academy.energy.api.block.IWirelessUser;
import com.mohistmc.academy.energy.impl.VBlocks.VNGenerator;
import com.mohistmc.academy.energy.impl.VBlocks.VNNode;
import com.mohistmc.academy.energy.impl.VBlocks.VNReceiver;
import com.mohistmc.academy.energy.impl.VBlocks.VWMatrix;
import com.mohistmc.academy.energy.impl.VBlocks.VWNode;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.saveddata.SavedData;
import org.slf4j.Logger;

/**
 * 维度级无线能源世界数据。
 * 存储所有 WirelessNet 和 NodeConn，每 tick 更新。
 */
public class WiWorldData extends SavedData {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String DATA_ID = "academy_wen";

    // ==================== 工厂 ====================

    public static WiWorldData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                FACTORY,
                DATA_ID
        );
    }

    public static WiWorldData getNonCreate(ServerLevel level) {
        var existing = level.getDataStorage().get(FACTORY, DATA_ID);
        if (existing != null) {
            existing.level = level;
        }
        return existing;
    }

    private static final SavedData.Factory<WiWorldData> FACTORY = new SavedData.Factory<>(
            WiWorldData::new,
            (tag, provider) -> {
                WiWorldData data = new WiWorldData();
                data.load(tag, provider);
                return data;
            }
    );

    // ==================== 实例 ====================

    Level level;

    /** 真实脏标记：仅当无线网络结构或状态实际变更时才需要存档 */
    private boolean dirty = true;

    public WiWorldData() {}

    private void load(CompoundTag tag, HolderLookup.Provider provider) {
        if (tag.contains("net")) {
            CompoundTag netTag = tag.getCompound("net");
            loadNetwork(netTag);
        }
        if (tag.contains("node")) {
            CompoundTag nodeTag = tag.getCompound("node");
            loadNode(nodeTag);
        }
    }

    // ==================== 网络数据 ====================

    final Map<Object, WirelessNet> netLookup = new HashMap<>();
    final Set<WirelessNet> netList = new HashSet<>();
    private final List<WirelessNet> toRemove = new ArrayList<>();

    boolean createNetwork(IWirelessMatrix matrix, String ssid, String password) {
        VWMatrix vm = new VWMatrix(matrix);
        if (netLookup.containsKey(vm)) {
            WirelessNet old = netLookup.get(vm);
            doRemoveNetwork(old);
        }

        WirelessNet net = new WirelessNet(this, vm, ssid, password);
        doAddNetwork(net);
        markDirty();
        return true;
    }

    public Collection<WirelessNet> rangeSearch(int x, int y, int z, double range, int max) {
        Set<WirelessNet> set = new HashSet<>();
        if (level == null) return set;

        BlockPos searchCenter = new BlockPos(x, y, z);
        int r = (int) Math.ceil(range);

        for (int dx = -r; dx <= r; dx += 4) {
            for (int dy = -r; dy <= r; dy += 4) {
                for (int dz = -r; dz <= r; dz += 4) {
                    BlockPos bp = searchCenter.offset(dx, dy, dz);
                    if (!level.isLoaded(bp)) continue;

                    BlockEntity be = level.getBlockEntity(bp);
                    WirelessNet net = null;
                    if (be instanceof IWirelessMatrix) {
                        net = getNetwork((IWirelessMatrix) be);
                    } else if (be instanceof IWirelessNode) {
                        net = getNetwork((IWirelessNode) be);
                    }
                    if (net != null && net.isInRange(x, y, z)
                            && net.getLoad() < net.getCapacity()) {
                        set.add(net);
                        if (set.size() >= max) return set;
                    }
                }
            }
        }
        return set;
    }

    public WirelessNet getNetwork(IWirelessMatrix matrix) {
        return privateGetNetwork(new VWMatrix(matrix));
    }

    public WirelessNet getNetwork(IWirelessNode node) {
        return privateGetNetwork(new VWNode(node));
    }

    private WirelessNet privateGetNetwork(Object key) {
        WirelessNet ret = netLookup.get(key);
        if (ret != null && ret.validate()) {
            return ret;
        }
        return null;
    }

    private void doRemoveNetwork(WirelessNet net) {
        netList.remove(net);
        net.onCleanup(this);
        markDirty();
    }

    private void doAddNetwork(WirelessNet net) {
        netList.add(net);
        net.onCreate(this);
    }

    private void loadNetwork(CompoundTag tag) {
        if (!tag.contains("networks")) return;
        ListTag list = tag.getList("networks", 10);
        for (int i = 0; i < list.size(); ++i) {
            WirelessNet net = new WirelessNet(this, list.getCompound(i));
            doAddNetwork(net);
        }
    }

    private void saveNetwork(CompoundTag tag) {
        ListTag list = new ListTag();
        for (WirelessNet net : netList) {
            if (!net.isDisposed()) {
                list.add(net.toNBT());
            }
        }
        tag.put("networks", list);
    }

    // ==================== 节点连接数据 ====================

    final Map<Object, NodeConn> nodeLookup = new HashMap<>();
    final Set<NodeConn> nodeList = new HashSet<>();
    private final List<NodeConn> nToRemove = new ArrayList<>();

    public NodeConn getNodeConnection(IWirelessNode node) {
        VNNode vnn = new VNNode(node);
        NodeConn ret = privateGetNodeConn(vnn);
        if (ret == null) {
            doAddNode(ret = new NodeConn(this, vnn));
        }
        return ret;
    }

    public NodeConn getNodeConnection(IWirelessUser user) {
        if (user instanceof IWirelessGenerator) {
            return privateGetNodeConn(new VNGenerator((IWirelessGenerator) user));
        } else if (user instanceof IWirelessReceiver) {
            return privateGetNodeConn(new VNReceiver((IWirelessReceiver) user));
        }
        return null;
    }

    private NodeConn privateGetNodeConn(Object key) {
        NodeConn ret = nodeLookup.get(key);
        if (ret != null && ret.validate()) {
            return ret;
        }
        return null;
    }

    private void doAddNode(NodeConn conn) {
        nodeList.add(conn);
        conn.onAdded(this);
        markDirty();
    }

    private void doRemoveNode(NodeConn conn) {
        nodeList.remove(conn);
        conn.onCleanup(this);
        markDirty();
    }

    private void loadNode(CompoundTag tag) {
        if (!tag.contains("list")) return;
        ListTag list = tag.getList("list", 10);
        for (int i = 0; i < list.size(); ++i) {
            doAddNode(new NodeConn(this, list.getCompound(i)));
        }
    }

    private void saveNode(CompoundTag tag) {
        ListTag list = new ListTag();
        for (NodeConn c : nodeList) {
            if (!c.isDisposed()) {
                list.add(c.toNBT());
            }
        }
        tag.put("list", list);
    }

    // ==================== Tick ====================

    public void tick() {
        tickNetwork();
        tickNode();
    }

    private void tickNetwork() {
        for (WirelessNet net : toRemove) {
            doRemoveNetwork(net);
        }
        toRemove.clear();

        for (WirelessNet net : netList) {
            if (net.isDisposed()) {
                toRemove.add(net);
            } else {
                net.level = level;
                net.tick();
            }
        }
    }

    private void tickNode() {
        for (NodeConn nc : nToRemove) {
            doRemoveNode(nc);
        }
        nToRemove.clear();

        for (NodeConn conn : nodeList) {
            if (conn.isDisposed()) {
                nToRemove.add(conn);
            } else {
                conn.tick();
            }
        }
    }

    // ==================== SavedData ====================

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        CompoundTag netTag = new CompoundTag();
        saveNetwork(netTag);
        tag.put("net", netTag);

        CompoundTag nodeTag = new CompoundTag();
        saveNode(nodeTag);
        tag.put("node", nodeTag);

        dirty = false;
        return tag;
    }

    public void markDirty() {
        dirty = true;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }
}
