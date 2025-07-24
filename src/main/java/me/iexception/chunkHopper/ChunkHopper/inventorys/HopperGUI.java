package me.iexception.chunkHopper.ChunkHopper.inventorys;

import me.iexception.chunkHopper.ChunkHopper.ChunkHopper;
import me.iexception.chunkHopper.utils.ChatUtils;
import me.iexception.chunkHopper.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class HopperGUI {

    public static Inventory openHopperGui(Player player, ChunkHopper chunkHopper) {

        Inventory inventory = Bukkit.createInventory(null, 27, ChatUtils.format("&cHopper Editor"));

        inventory.setItem(11, new ItemBuilder(Material.BOOK, 1).setDisplayName("&cHopper Informatie")
                .setLore(""
                        , "&cɪɴғᴏᴍᴀᴛɪᴇ"
                        , " &c❙ &fᴇɪɢᴇɴᴀᴀʀ: &c" + Bukkit.getOfflinePlayer(chunkHopper.getUuid()).getName()
                        , " &c❙ &fᴛʏᴘᴇ: &c" + chunkHopper.getType()
                        , " &c❙ &fʟɪɴᴋᴇᴅ ᴄʜᴇsᴛ: &c" + ((chunkHopper.getLinkedChest() == null) ? "&cGeen" : ("&fX:&c" + chunkHopper.getLinkedChest().getX() + " &fY&c" + chunkHopper.getLinkedChest().getY() + " &fZ:&c" + chunkHopper.getLinkedChest().getZ()))
                        , " &c❙ &fʟᴏᴄᴀᴛɪᴇ: &7X:&c" + chunkHopper.getLocation().getX() + " &7Y:&c" + chunkHopper.getLocation().getY() + " &7Z:&c" + chunkHopper.getLocation().getZ()).build());

        inventory.setItem(13, new ItemBuilder(Material.CHEST, 1).setDisplayName("&cLink Chest")
                .setLore(""
                        , "&c→ &7ʀᴇᴄʜᴛᴇʀ ᴋʟɪᴋ ᴏᴘ ᴅɪᴛ ɪᴛᴇᴍ ᴏᴍ ᴊᴇ ʜᴏᴘᴘᴇʀ ᴛᴇ ʟɪɴᴋᴇɴ"
                        , "&c→ &7ʟɪɴᴋᴇʀ ᴋʟɪᴋ ᴏᴘ ᴅɪᴛ ɪᴛᴇᴍ ᴏᴍ ᴊᴇ ʜᴏᴘᴘᴇʀ ᴛᴇ ᴜɴʟɪɴᴋᴇɴ").build());

        inventory.setItem(15, new ItemBuilder(Material.NETHER_STAR, 1).setDisplayName("&cInstellingen")
                .setLore(""
                        , "&cᴘᴀs ʜɪᴇʀ ᴅᴇ ɪɴsᴛᴇʟʟɪɴɢᴇɴ ᴠᴀɴ ᴊᴇ ʜᴏᴘᴘᴇʀ ᴀᴀɴ"
                        , "&c→ &7ʙᴇʜᴇᴇʀ ᴅᴇ ʟᴇᴅᴇɴ ᴠᴀɴ ᴊᴇ ʜᴏᴘᴘᴇʀ"
                        , "&c→ &7ʙᴇʜᴇᴇʀ ʜᴇᴛ ғɪʟᴛᴇʀ ᴠᴀɴ ᴊᴇ ʜᴏᴘᴘᴇʀ").build());

        player.openInventory(inventory);
        return inventory;
    }
}
