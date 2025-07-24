package me.iexception.chunkHopper.ChunkHopper.events;

import me.iexception.chunkHopper.ChunkHopper.ChunkHopper;
import me.iexception.chunkHopper.ChunkHopper.inventorys.FilterGUI;
import me.iexception.chunkHopper.ChunkHopper.inventorys.HopperGUI;
import me.iexception.chunkHopper.ChunkHopper.inventorys.PlayerManagGUI;
import me.iexception.chunkHopper.ChunkHopper.inventorys.SettingsGUI;
import me.iexception.chunkHopper.ChunkHopper.managers.HopperTimerManager;
import me.iexception.chunkHopper.utils.ChatUtils;
import me.iexception.chunkHopper.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class InventoryListener implements Listener {

    public static HashMap<UUID, ChunkHopper> isLinking = new HashMap<>();

    @EventHandler
    public void InventoryClickEvent(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        ItemStack itemStack = event.getCurrentItem();

        if (itemStack != null && !itemStack.getType().isAir()) {
            String title = itemStack.getItemMeta().getDisplayName();
            if (event.getView().getTitle().equalsIgnoreCase(ChatUtils.format("&cHopper Editor"))) {
                event.setCancelled(true);
                if (event.getClick().isRightClick() && event.getCurrentItem().getType() == Material.CHEST && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatUtils.format(title))) {

                    isLinking.put(player.getUniqueId(), PlayerEvents.lastOpendHopper.get(player.getUniqueId()));
                    player.closeInventory();
                    MessageUtils.getInstance().sendMessage(player, "linking-chest");

                } else if (event.getClick().isLeftClick() && event.getCurrentItem().getType() == Material.CHEST && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatUtils.format(title))) {

                    ChunkHopper chunkHopper = PlayerEvents.lastOpendHopper.get(player.getUniqueId());
                    if (chunkHopper.getLinkedChest() != null) {

                        chunkHopper.setLinkedChest(null);
                        MessageUtils.getInstance().sendMessage(player, "unlicked-chest");
                        if (HopperTimerManager.getInstance().hasActiveTask(chunkHopper)) {
                            HopperTimerManager.getInstance().stopHopperTask(chunkHopper);
                        }
                        player.closeInventory();
                    }


                } else if (event.getCurrentItem().getType() == Material.NETHER_STAR && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatUtils.format(title))) {
                    SettingsGUI.openSettingsGui(player);
                }
            }

            if (event.getView().getTitle().equalsIgnoreCase(ChatUtils.format("&cHopper Instellingen"))) {
                event.setCancelled(true);
                if (event.getCurrentItem().getType() == Material.ARROW && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatUtils.format(title))) {
                    ChunkHopper chunkHopper = PlayerEvents.lastOpendHopper.get(player.getUniqueId());
                    HopperGUI.openHopperGui(player, chunkHopper);
                } else if (event.getCurrentItem().getType() == Material.PLAYER_HEAD && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatUtils.format(title))) {
                    ChunkHopper chunkHopper = PlayerEvents.lastOpendHopper.get(player.getUniqueId());
                    PlayerManagGUI.openPlayerManagGUI(player, chunkHopper);
                } else if (event.getCurrentItem().getType() == Material.HOPPER && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatUtils.format(title))) {
                    ChunkHopper chunkHopper = PlayerEvents.lastOpendHopper.get(player.getUniqueId());
                    FilterGUI.openFilterGUI(player, chunkHopper);
                }
            }

            if (event.getView().getTitle().equalsIgnoreCase(ChatUtils.format("&cBeheer Spelers"))) {
                event.setCancelled(true);
                if (event.getCurrentItem().getType() == Material.ARROW && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatUtils.format(title))) {
                    SettingsGUI.openSettingsGui(player);
                }
            }

            if(event.getView().getTitle().equalsIgnoreCase(ChatUtils.format("&cBeheer Filter"))){
                event.setCancelled(true);
                if (event.getCurrentItem().getType() == Material.ARROW && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatUtils.format(title))) {
                    SettingsGUI.openSettingsGui(player);
                }
            }
        }
    }
}
