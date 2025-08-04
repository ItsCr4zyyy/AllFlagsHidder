package org.wcrazy;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

import static org.wcrazy.Colors.formatHexColors;

public class AFHCommand implements CommandExecutor {

    private final AllFlagsHidder plugin;

    public AFHCommand(AllFlagsHidder plugin) {
        this.plugin = plugin;
        plugin.getCommand("allflagshidder").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("afh.reload")) {
            sender.sendMessage(formatHexColors(plugin.getNoPerms()));
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            plugin.loadConfigValues();
            sender.sendMessage(formatHexColors("&aAllFlagsHidder-" + plugin.pluginVersion + " config was reloaded successfully."));
            plugin.getLogger().info(formatHexColors("&fConfig was reloaded by &2" + sender.getName()));
            return true;
        }

        sender.sendMessage(formatHexColors("&eUsage: /allflagshidder reload"));
        return true;
    }
}
