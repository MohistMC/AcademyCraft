package com.mohistmc.academy.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class AcademyNode {

    private String name = "Unnamed";
    private String pass = "";
    private BlockPos pos;
    private Level level;
    private boolean authed = false;

    public AcademyNode(String name, String pass, BlockPos pos, Level level) {
        this.name = name;
        this.pos = pos;
        this.pass = pass;
        this.level = level;
        this.authed = !pass.isBlank();
    }

    public AcademyNode(String name, BlockPos pos, Level level) {
        this(name, "", pos, level);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
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

    public boolean isNeedAuth() {
        return authed;
    }

    public void needAuth() {
        this.authed = true;
    }

    public void noNeedAuth() {
        this.authed = false;
    }
}
