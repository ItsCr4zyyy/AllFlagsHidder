package org.wcrazy;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.wcrazy.Colors.formatHexColors;

public class InventoryListener implements Listener {

    private final AllFlagsHidder plugin;
    private final ProtocolManager protocolManager;

    public InventoryListener(AllFlagsHidder plugin) {
        this.plugin = plugin;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        registerPacketListener();
    }

    private void registerPacketListener() {
        protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL,
                PacketType.Play.Client.WINDOW_CLICK) {


            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                Bukkit.getScheduler().runTask(plugin, () -> {
                    hideFlagsInInventory(player.getInventory());
                });
            }
        });
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        String openedTitle = event.getView().getTitle();
        ConfigurationSection guiSection = plugin.getConfig().getConfigurationSection("hidden-guis");

        if (guiSection == null) return;

        for (String key : guiSection.getKeys(false)) {
            ConfigurationSection section = guiSection.getConfigurationSection(key);
            if (section == null) continue;

            String configTitle = formatHexColors(section.getString("title", ""));
            if (configTitle.equals(openedTitle)) {
                List<String> flags = section.getStringList("flags-to-hide");
                List<String> whitelist = section.getStringList("whitelist");
                List<String> blacklist = section.getStringList("blacklist");
                hideFlagsInInventory(event.getInventory(), flags, whitelist, blacklist);
                break;
            }
        }
    }


    public void hideFlagsInInventory(Inventory inv, List<String> flagNames, List<String> whitelist, List<String> blacklist) {
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item == null || item.getType().isAir()) continue;

            String itemKey = item.getType().getKey().toString();

            if (!whitelist.isEmpty() && !whitelist.contains(itemKey)) continue;
            if (!blacklist.isEmpty() && blacklist.contains(itemKey)) continue;

            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;

            for (String flagName : flagNames) {
                try {
                    if (flagName.equalsIgnoreCase("HIDE_ITEM_SPECIFICS")) {
                        meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE);
                        meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_SPEED);
                        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,
                                new AttributeModifier(UUID.randomUUID(), "dummy_attack_damage", 0.0,
                                        AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
                        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED,
                                new AttributeModifier(UUID.randomUUID(), "dummy_attack_speed", 0.0,
                                        AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
                        continue;
                    }

                    ItemFlag flag = ItemFlag.valueOf(flagName.toUpperCase());
                    meta.addItemFlags(flag);
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning("Invalid ItemFlag in config: " + flagName);
                }
            }

            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
    }
    public void hideFlagsInInventory(Inventory inv) {
        ConfigurationSection invSection = plugin.getConfig().getConfigurationSection("hide-inventory");

        if (invSection == null || !invSection.getBoolean("enabled")) return;

        List<String> flags = invSection.getStringList("flags-to-hide");
        List<String> whitelist = invSection.getStringList("whitelist");
        List<String> blacklist = invSection.getStringList("blacklist");

        if (flags == null) flags = new ArrayList<>();

        hideFlagsInInventory(inv, flags, whitelist, blacklist);
    }

}
