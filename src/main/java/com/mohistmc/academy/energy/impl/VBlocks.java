package com.mohistmc.academy.energy.impl;

import com.mohistmc.academy.energy.api.block.IWirelessGenerator;
import com.mohistmc.academy.energy.api.block.IWirelessMatrix;
import com.mohistmc.academy.energy.api.block.IWirelessNode;
import com.mohistmc.academy.energy.api.block.IWirelessReceiver;
import com.mohistmc.academy.energy.api.block.IWirelessTile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * 虚拟方块 —— 用坐标定位方块实体，支持延迟加载。
 * 替代旧版 LambdaLib2 的世界查找。
 *
 * @author WeAthFolD (original), Mgazul (port)
 */
public final class VBlocks {

    private VBlocks() {}

    /**
     * 虚拟方块基类。
     */
    public static abstract class VBlock<T extends IWirelessTile> {

        protected final int x, y, z;
        protected final boolean ignoreChunk;

        public VBlock(BlockEntity be, boolean ignoreChunk) {
            BlockPos pos = be.getBlockPos();
            this.x = pos.getX();
            this.y = pos.getY();
            this.z = pos.getZ();
            this.ignoreChunk = ignoreChunk;
        }

        public VBlock(CompoundTag tag, boolean ignoreChunk) {
            this.x = tag.getInt("x");
            this.y = tag.getInt("y");
            this.z = tag.getInt("z");
            this.ignoreChunk = ignoreChunk;
        }

        public double distSq(VBlock<?> another) {
            double dx = another.x - x, dy = another.y - y, dz = another.z - z;
            return dx * dx + dy * dy + dz * dz;
        }

        public boolean isLoaded(Level level) {
            return level.isLoaded(new BlockPos(x, y, z));
        }

        @SuppressWarnings("unchecked")
        public T get(Level level) {
            if (!ignoreChunk && !isLoaded(level))
                return null;
            if (level == null)
                return null;

            BlockPos pos = new BlockPos(x, y, z);
            BlockEntity be = level.getBlockEntity(pos);
            if (be == null || !isAcceptable(be)) {
                return null;
            }
            return (T) be;
        }

        public CompoundTag toNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putInt("x", x);
            tag.putInt("y", y);
            tag.putInt("z", z);
            return tag;
        }

        @Override
        public int hashCode() {
            return x ^ y ^ z;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != this.getClass())
                return false;
            VBlock<?> vb = (VBlock<?>) obj;
            return vb.x == x && vb.y == y && vb.z == z;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "[" + x + ", " + y + ", " + z + "]";
        }

        protected abstract boolean isAcceptable(BlockEntity be);
    }

    // ==================== 网络虚拟块 ====================

    public static final class VWMatrix extends VBlock<IWirelessMatrix> {
        public VWMatrix(IWirelessMatrix matrix) {
            super((BlockEntity) matrix, true);
        }
        public VWMatrix(CompoundTag tag) {
            super(tag, true);
        }
        @Override
        protected boolean isAcceptable(BlockEntity be) {
            return be instanceof IWirelessMatrix;
        }
    }

    public static final class VWNode extends VBlock<IWirelessNode> {
        public VWNode(IWirelessNode node) {
            super((BlockEntity) node, false);
        }
        public VWNode(CompoundTag tag) {
            super(tag, false);
        }
        @Override
        protected boolean isAcceptable(BlockEntity be) {
            return be instanceof IWirelessNode;
        }
    }

    // ==================== 节点连接虚拟块 ====================

    public static final class VNNode extends VBlock<IWirelessNode> {
        public VNNode(IWirelessNode node) {
            super((BlockEntity) node, true);
        }
        public VNNode(CompoundTag tag) {
            super(tag, true);
        }
        @Override
        protected boolean isAcceptable(BlockEntity be) {
            return be instanceof IWirelessNode;
        }
    }

    public static final class VNGenerator extends VBlock<IWirelessGenerator> {
        public VNGenerator(IWirelessGenerator gen) {
            super((BlockEntity) gen, true);
        }
        public VNGenerator(CompoundTag tag) {
            super(tag, true);
        }
        @Override
        protected boolean isAcceptable(BlockEntity be) {
            return be instanceof IWirelessGenerator;
        }
    }

    public static final class VNReceiver extends VBlock<IWirelessReceiver> {
        public VNReceiver(IWirelessReceiver rec) {
            super((BlockEntity) rec, true);
        }
        public VNReceiver(CompoundTag tag) {
            super(tag, true);
        }
        @Override
        protected boolean isAcceptable(BlockEntity be) {
            return be instanceof IWirelessReceiver;
        }
    }
}
