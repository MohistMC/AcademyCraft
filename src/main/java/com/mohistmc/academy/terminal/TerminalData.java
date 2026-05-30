package com.mohistmc.academy.terminal;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Mgazul
 * @date 2026/5/31 02:45
 */
public class TerminalData {

    private boolean installed = false;
    private final Set<String> installedApps = new HashSet<>();

    public TerminalData() {
        installedApps.add("settings");
        installedApps.add("tutorial");
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

    public void installApp(String appId) {
        installedApps.add(appId);
    }

    public Set<String> getInstalledApps() {
        return installedApps;
    }

    public boolean isBuiltIn(String appId) {
        return "settings".equals(appId) || "tutorial".equals(appId);
    }
}
