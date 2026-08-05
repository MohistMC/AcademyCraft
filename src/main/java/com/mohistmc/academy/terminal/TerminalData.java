package com.mohistmc.academy.terminal;

import java.util.HashSet;
import java.util.Set;

public class TerminalData {

    private boolean installed = false;
    private final Set<String> installedApps = new HashSet<>();

    public TerminalData() {
        installedApps.add(AppRegistry.SETTINGS.getAppId());
        installedApps.add(AppRegistry.TUTORIAL.getAppId());
    }

    public boolean isInstalled() {
        return installed;
    }

    public void setInstalled(boolean installed) {
        this.installed = installed;
    }

    public boolean hasApp(String appId) {
        return installedApps.contains(appId);
    }

    public boolean hasApp(TerminalApp app) {
        return installedApps.contains(app.getAppId());
    }

    public void installApp(String appId) {
        installedApps.add(appId);
    }

    public void installApp(TerminalApp app) {
        installedApps.add(app.getAppId());
    }

    public Set<String> getInstalledApps() {
        return installedApps;
    }

    public boolean isBuiltIn(TerminalApp app) {
        return app.isBuiltIn();
    }
}
