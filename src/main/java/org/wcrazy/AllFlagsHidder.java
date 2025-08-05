package org.wcrazy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class AllFlagsHidder extends JavaPlugin {

    String serverVersion = Bukkit.getBukkitVersion().split("-")[0];
    String pluginVersion = getDescription().getVersion();
    private String noPerms;

    @Override
    public void onEnable() {
        boolean isModernServer = serverVersion.startsWith("1.20.5") || serverVersion.startsWith("1.20.6") || serverVersion.startsWith("1.21");
        getLogger().info("\u001B[32mAllFlagsHidder v" + pluginVersion + " running on server version " + serverVersion + "\u001B[0m");
        if (!isModernServer) {
            getLogger().info("\u001B[91mYou're using unsupported server version.\u001B[0m");
            this.setEnabled(false);
            return;
        }
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        getCommand("allflagshidder").setExecutor(new AFHCommand(this));
        new Updater(this, "ItsCr4zyyy", "AllFlagsHidder").checkForUpdate();
    }

    @Override
    public void onDisable() {
    }

    public void loadConfigValues() {
        this.noPerms = getConfig().getString("no-permission");
    }

    public String getNoPerms() {
        return noPerms;
    }
}
