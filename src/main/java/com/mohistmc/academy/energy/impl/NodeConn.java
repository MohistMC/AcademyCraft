package com.mohistmc.academy.energy.impl;

import com.mohistmc.academy.energy.api.block.IWirelessGenerator;
import com.mohistmc.academy.energy.api.block.IWirelessNode;
import com.mohistmc.academy.energy.api.block.IWirelessReceiver;
import com.mohistmc.academy.energy.impl.VBlocks.VBlock;
import com.mohistmc.academy.energy.impl.VBlocks.VNGenerator;
import com.mohistmc.academy.energy.impl.VBlocks.VNNode;
import com.mohistmc.academy.energy.impl.VBlocks.VNReceiver;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

/**
 * 节点连接 —— 一个节点 + M 个发电机 + K 个接收器。
 * 每 tick 先从发电机收集能量到节点，再从节点分配能量到接收器。
 */
public class NodeConn {

    private static final Logger LOGGER = LogUtils.getLogger();

    private final WiWorldData data;
    private final VNNode node;

    private boolean disposed = false;
    private final List<VNReceiver> receivers = new LinkedList<>();
    private final List<VNGenerator> generators = new LinkedList<>();
    private final List<VNReceiver> toRemoveReceivers = new ArrayList<>();
    private final List<VNGenerator> toRemoveGenerators = new ArrayList<>();

    public NodeConn(WiWorldData data, VNNode node) {
        this.data = data;
        this.node = node;
    }

    public NodeConn(WiWorldData data, CompoundTag tag) {
        this.data = data;
        this.node = new VNNode(tag.getCompound("node"));

        ListTag recList = tag.getList("receivers", 10);
        for (int i = 0; i < recList.size(); ++i) {
            addReceiver(new VNReceiver(recList.getCompound(i)));
        }

        ListTag genList = tag.getList("generators", 10);
        for (int i = 0; i < genList.size(); ++i) {
            addGenerator(new VNGenerator(genList.getCompound(i)));
        }
    }

    // ==================== NBT ====================

    CompoundTag toNBT() {
        Level level = getLevel();
        CompoundTag ret = new CompoundTag();

        ListTag list = new ListTag();
        for (VNReceiver r : receivers) {
            if (!r.isLoaded(level) || r.get(level) != null) {
                list.add(r.toNBT());
            }
        }
        ret.put("receivers", list);

        list = new ListTag();
        for (VNGenerator g : generators) {
            if (!g.isLoaded(level) || g.get(level) != null) {
                list.add(g.toNBT());
            }
        }
        ret.put("generators", list);

        ret.put("node", node.toNBT());
        return ret;
    }

    // ==================== 生命周期 ====================

    public void dispose() { disposed = true; }
    public boolean isDisposed() { return disposed; }

    void onAdded(WiWorldData data) {
        data.nodeLookup.put(node, this);
    }

    void onCleanup(WiWorldData data) {
        data.nodeLookup.remove(node);
        for (VNGenerator gen : generators) data.nodeLookup.remove(gen);
        for (VNReceiver rec : receivers) data.nodeLookup.remove(rec);
    }

    boolean validate() {
        Level level = getLevel();
        if (!disposed && node.isLoaded(level)) {
            if (node.get(level) == null || (generators.isEmpty() && receivers.isEmpty())) {
                disposed = true;
            }
        }
        return !disposed;
    }

    // ==================== 添加/移除 ====================

    boolean addReceiver(VNReceiver receiver) {
        if (getLoad() >= getCapacity() || !checkRange(receiver))
            return false;

        Level level = getLevel();
        if (level != null) {
            NodeConn old = data.getNodeConnection(receiver.get(level));
            if (old != null) old.removeReceiver(receiver);
        }

        receivers.add(receiver);
        data.nodeLookup.put(receiver, this);
        return true;
    }

    void removeReceiver(VNReceiver receiver) {
        toRemoveReceivers.add(receiver);
    }

    boolean addGenerator(VNGenerator gen) {
        if (getLoad() >= getCapacity() || !checkRange(gen))
            return false;

        Level level = getLevel();
        NodeConn old = data.getNodeConnection(gen.get(level));
        if (old != null) old.removeGenerator(gen);

        generators.add(gen);
        data.nodeLookup.put(gen, this);
        return true;
    }

    void removeGenerator(VNGenerator gen) {
        toRemoveGenerators.add(gen);
    }

    private boolean checkRange(VBlock<?> block) {
        IWirelessNode inode = node.get(getLevel());
        double range = inode == null ? 1000 : inode.getRange();
        return block.distSq(node) <= range * range;
    }

    // ==================== Accessors ====================

    public IWirelessNode getNode() {
        return node.get(getLevel());
    }

    private Level getLevel() {
        return data.level;
    }

    public int getLoad() {
        return receivers.size() + generators.size();
    }

    public int getCapacity() {
        Level level = getLevel();
        IWirelessNode inode = level == null ? null : node.get(getLevel());
        return inode == null ? Integer.MAX_VALUE : inode.getCapacity();
    }

    // ==================== Tick ====================

    void tick() {
        validate();
        Level level = getLevel();
        if (!node.isLoaded(level)) return;

        IWirelessNode iNode = node.get(level);
        if (iNode == null) return;

        double transferLeft = iNode.getBandwidth();

        // 1. 从发电机收集能量
        Collections.shuffle(generators);
        Iterator<VNGenerator> genIter = generators.iterator();
        while (transferLeft > 0 && genIter.hasNext()) {
            VNGenerator gen = genIter.next();
            if (!gen.isLoaded(level)) continue;

            IWirelessGenerator igen = gen.get(level);
            if (igen == null) {
                removeGenerator(gen);
            } else {
                double cur = iNode.getEnergy();
                double required = Math.min(transferLeft,
                        Math.min(igen.getBandwidth(), iNode.getMaxEnergy() - cur));
                double amt = igen.getProvidedEnergy(required);

                if (amt > required) {
                    LOGGER.warn("Energy input overflow for generator {}", igen);
                    amt = required;
                }

                cur += amt;
                iNode.setEnergy(cur);
                transferLeft -= amt;
            }
        }

        // 2. 向接收器分配能量
        transferLeft = iNode.getBandwidth();
        Collections.shuffle(receivers);
        Iterator<VNReceiver> recIter = receivers.iterator();
        while (transferLeft > 0 && recIter.hasNext()) {
            VNReceiver rec = recIter.next();
            if (!rec.isLoaded(level)) continue;

            IWirelessReceiver irec = rec.get(level);
            if (irec == null) {
                removeReceiver(rec);
            } else {
                double cur = iNode.getEnergy();
                double give = Math.min(cur, Math.min(transferLeft, irec.getBandwidth()));
                give = Math.min(irec.getRequiredEnergy(), give);

                give = give - irec.injectEnergy(give);
                cur -= give;
                transferLeft -= give;
                iNode.setEnergy(cur);
            }
        }

        // 清理待删除的发电机/接收器
        data.nodeLookup.keySet().removeAll(toRemoveGenerators);
        generators.removeAll(toRemoveGenerators);
        data.nodeLookup.keySet().removeAll(toRemoveReceivers);
        receivers.removeAll(toRemoveReceivers);

        toRemoveGenerators.clear();
        toRemoveReceivers.clear();
    }
}
