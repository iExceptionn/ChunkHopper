package me.iexception.chunkHopper.ChunkHopper.inventorys;

import me.iexception.chunkHopper.ChunkHopper.ChunkHopper;
import me.iexception.chunkHopper.utils.ChatUtils;
import me.iexception.chunkHopper.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class PlayerManagGUI {

    public static Inventory openPlayerManagGUI(Player player, ChunkHopper chunkHopper) {

        Inventory inventory = Bukkit.createInventory(null, 27, ChatUtils.format("&cBeheer Spelers"));

        inventory.setItem(4, new ItemBuilder(Material.PLAYER_HEAD, 1).setSkullOwner(Bukkit.getPlayer(chunkHopper.getUuid()).getName())
                .setDisplayName("&c" + Bukkit.getPlayer(chunkHopper.getUuid()).getName())
                .setLore(""
                        , "&c→ &7ʀᴏʟ: &fᴇɪɢᴇɴᴀᴀʀ"
                        , "&c→ &7sᴛᴀᴛᴜs: " + (Bukkit.getOfflinePlayer(chunkHopper.getUuid()).isOnline() ? "&aᴏɴʟɪɴᴇ" : "&cᴏғғʟɪɴᴇ")).build());

        //inventory.setItem(11, new ItemBuilder(Material.PLAYER_HEAD, 1).setSkullOwner("Springspier")
        //        .setDisplayName("&c" + Bukkit.getOfflinePlayer("Springspier").getName())
        //        .setLore(""
        //                , "&c→ &7ʀᴏʟ: &fʟɪᴅ"
        //                , "&c→ &7sᴛᴀᴛᴜs: " + (Bukkit.getOfflinePlayer("Springspier").isOnline() ? "&aᴏɴʟɪɴᴇ" : "&cᴏғғʟɪɴᴇ")
        //                , ""
        //                , "&cᴋʟɪᴋ ᴏᴍ ᴅᴇᴢᴇ sᴘᴇʟᴇʀ ᴛᴇ ᴠᴇʀᴡɪᴊᴅᴇʀᴇɴ").build());

        inventory.setItem(18, new ItemBuilder(Material.ARROW, 1).setDisplayName("&7Ga terug").build());

        inventory.setItem(22, new ItemBuilder(Material.PAPER, 1).setDisplayName("&cUitleg")
                .setLore(""
                        , "&cᴊᴇ ᴋᴜɴᴛ ɪᴇᴍᴀɴᴅ ᴛᴏᴇᴠᴏᴇɢᴇɴ ᴠɪᴀ ʜᴇᴛ ᴄᴏᴍᴍᴀɴᴅ"
                        , "&c→ &7/ᴄʜᴜɴᴋʜᴏᴘᴘᴇʀ ᴀᴅᴅ <sᴘᴇʟᴇʀ>").build());

        player.openInventory(inventory);
        return inventory;
    }
}
