package com.mohistmc.academy.world.item;

import com.mohistmc.academy.terminal.AppRegistry;

public class AppSkillTree extends BaseApp {
    public AppSkillTree() {
        super(new Properties());
    }

    @Override
    public String getAppId() {
        return AppRegistry.SKILL_TREE.getAppId();
    }
}
