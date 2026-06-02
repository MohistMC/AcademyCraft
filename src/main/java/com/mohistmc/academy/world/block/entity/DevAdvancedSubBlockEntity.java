package com.mohistmc.academy.world.block.entity;

import com.mohistmc.academy.world.AcademyBlockEntities;
import com.mohistmc.academy.world.block.IDevSubStructure;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DevAdvancedSubBlockEntity extends BlockEntity implements IDevSubStructure {
    private UUID structureId;
    private BlockPos mainPos;

    public DevAdvancedSubBlockEntity(BlockPos pos, BlockState state) {
        super(AcademyBlockEntities.DEV_ADVANCED_SUB.get(), pos, state);
    }

    @Override
    public UUID getStructureId() {
        return structureId;
    }

    @Override
    public void setStructureId(UUID structureId) {
        this.structureId = structureId;
        setChanged();
    }

    @Override
    public BlockPos getMainPos() {
        return mainPos;
    }

    @Override
    public void setMainPos(BlockPos mainPos) {
        this.mainPos = mainPos;
        setChanged();
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("structureId")) {
            this.structureId = UUID.fromString(tag.getString("structureId"));
        }
        if (tag.contains("mainPos")) {
            this.mainPos = BlockPos.of(tag.getLong("mainPos"));
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (structureId != null) {
            tag.putString("structureId", structureId.toString());
        }
        if (mainPos != null) {
            tag.putLong("mainPos", mainPos.asLong());
        }
    }
}
