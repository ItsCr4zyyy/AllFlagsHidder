package org.wcrazy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class AllFlagsHidder extends JavaPlugin {

    private Updater updater;

    String serverVersion = Bukkit.getBukkitVersion().split("-")[0];
    String pluginVersion = getDescription().getVersion();

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

        updater = new Updater(this, "ItsCr4zyyy", "AllFlagsHidder");
        updater.checkForUpdate();
        getServer().getPluginManager().registerEvents(new JoinListener(updater, this), this);
    }

    @Override
    public void onDisable() {
    }
}
