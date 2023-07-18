package com.mohistmc.academy.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class AcademyNode {

    private String name = "Unnamed";
    private BlockPos pos;
    private Level level;
    private boolean authed = false;

    public AcademyNode(String name, BlockPos pos, Level level, boolean authed) {
        this.name = name;
        this.pos = pos;
        this.level = level;
        this.authed = authed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public boolean isAuthed() {
        return authed;
    }

    public void setAuthed(boolean authed) {
        this.authed = authed;
    }
}
