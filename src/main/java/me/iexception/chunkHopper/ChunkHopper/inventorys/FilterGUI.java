package me.iexception.chunkHopper.ChunkHopper.inventorys;

import me.iexception.chunkHopper.ChunkHopper.ChunkHopper;
import me.iexception.chunkHopper.utils.ChatUtils;
import me.iexception.chunkHopper.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class FilterGUI {

    public static Inventory openFilterGUI(Player player, ChunkHopper chunkHopper) {

        Inventory inventory = Bukkit.createInventory(null, 54, ChatUtils.format("&cBeheer Filter"));

        for(int i = 36; i <= 44; i++){
            inventory.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE, 1).setDisplayName("&f").build());
        }

        inventory.setItem(45, new ItemBuilder(Material.ARROW, 1).setDisplayName("&7Ga terug").build());

        inventory.setItem(49, new ItemBuilder(Material.GRAY_DYE, 1).setDisplayName("&7Disabled")
                .setLore(""
                        , "&cᴋʟɪᴋ ᴏᴍ ʜᴇᴛ ʜᴏᴘᴘᴇʀ ғɪʟᴛᴇʀ ᴛᴇ ᴀᴄᴛɪᴠᴇʀᴇɴ").build());

        player.openInventory(inventory);
        return inventory;
    }

}
