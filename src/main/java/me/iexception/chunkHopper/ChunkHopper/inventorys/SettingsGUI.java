package me.iexception.chunkHopper.ChunkHopper.inventorys;

import me.iexception.chunkHopper.ChunkHopper.ChunkHopper;
import me.iexception.chunkHopper.utils.ChatUtils;
import me.iexception.chunkHopper.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class SettingsGUI {

    public static Inventory openSettingsGui(Player player) {

        Inventory inventory = Bukkit.createInventory(null, 27, ChatUtils.format("&cHopper Instellingen"));

        inventory.setItem(12, new ItemBuilder(Material.PLAYER_HEAD, 1).setDisplayName("&cBeheer spelers").build());

        inventory.setItem(14, new ItemBuilder(Material.HOPPER, 1).setDisplayName("&cBeheer filter").build());

        inventory.setItem(18, new ItemBuilder(Material.ARROW, 1).setDisplayName("&7Ga terug").build());

        player.openInventory(inventory);
        return inventory;
    }
}
