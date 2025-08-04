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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
                    if (plugin.getConfig().getBoolean("hide-inventory", false)) {
                        hideFlagsInInventory(player.getInventory());
                    }
                });
            }
        });
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        String title = event.getView().getTitle();
        List<String> hiddenGuis = plugin.getConfig().getStringList("hidden-gui-titles");

        for (String rawTitle : hiddenGuis) {
            String formattedTitle = formatHexColors(rawTitle);
            if (formattedTitle.equals(title)) {
                hideFlagsInInventory(event.getInventory());
                break;
            }
        }
    }

    public void hideFlagsInInventory(Inventory inv) {
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item == null || item.getType().isAir()) continue;

            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;

            meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE);
            meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_SPEED);

            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,
                    new AttributeModifier(UUID.randomUUID(), "dummy_attack_damage", 0.0,
                            AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED,
                    new AttributeModifier(UUID.randomUUID(), "dummy_attack_speed", 0.0,
                            AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));

            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
            meta.addItemFlags(ItemFlag.HIDE_DYE);
            meta.addItemFlags(ItemFlag.HIDE_STORED_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
            meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);

            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
    }
}
