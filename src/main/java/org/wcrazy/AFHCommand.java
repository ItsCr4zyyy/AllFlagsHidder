package org.wcrazy;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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
            sender.sendMessage(formatHexColors(plugin.getConfig().getString("no-permission")));
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            sender.sendMessage(formatHexColors("&aAllFlagsHidder-" + plugin.pluginVersion + " config was reloaded successfully."));
            plugin.getLogger().info("Config was reloaded by " + sender.getName());
            return true;
        }

        sender.sendMessage(formatHexColors("&8+------------------------------------+"));
        sender.sendMessage(formatHexColors("&r &6&l★&r &fAllFlagsHidder commands:"));
        sender.sendMessage(formatHexColors("&r &8➥ &e/allflagshidder reload"));
        sender.sendMessage(formatHexColors("&8+------------------------------------+"));
        return true;
    }
}
