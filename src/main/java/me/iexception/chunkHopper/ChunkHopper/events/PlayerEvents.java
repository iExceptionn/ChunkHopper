package me.iexception.chunkHopper.ChunkHopper.events;

import me.iexception.chunkHopper.ChunkHopper.ChunkHopper;
import me.iexception.chunkHopper.ChunkHopper.inventorys.HopperGUI;
import me.iexception.chunkHopper.ChunkHopper.managers.ChunkHopperManager;
import me.iexception.chunkHopper.ChunkHopper.managers.HopperTimerManager;
import me.iexception.chunkHopper.utils.FileManager;
import me.iexception.chunkHopper.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public class PlayerEvents implements Listener {

    public static HashMap<UUID, ChunkHopper> lastOpendHopper = new HashMap<>();

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        if (player.isSneaking() && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock() != null && event.getClickedBlock().getType().equals(Material.HOPPER)) {
                ChunkHopper chunkHopper = ChunkHopperManager.getInstance().getHopper(event.getClickedBlock().getChunk());
                if (chunkHopper != null && chunkHopper.getLocation().equals(event.getClickedBlock().getLocation())) {
                    event.setCancelled(true);
                    if((!chunkHopper.getUuid().equals(player.getUniqueId())) && !player.hasPermission("chunkhopper.bypass")){
                        MessageUtils.getInstance().sendMessage(player, "not-your-hopper", Bukkit.getServer().getOfflinePlayer(chunkHopper.getUuid()).getName());
                        return;
                    }
                    HopperGUI.openHopperGui(player, chunkHopper);
                    lastOpendHopper.put(player.getUniqueId(), chunkHopper);
                }
            }
        }

        if (InventoryListener.isLinking.containsKey(player.getUniqueId())) {
            if(event.getClickedBlock().equals(Material.AIR)) return;
            if ((event.getAction().isLeftClick() || event.getAction() == Action.RIGHT_CLICK_BLOCK) && (event.getClickedBlock().getType().equals(Material.CHEST) || event.getClickedBlock().getType().equals(Material.TRAPPED_CHEST))) {
                event.setCancelled(true);

                if(ChunkHopperManager.getInstance().getLinkedChest(event.getClickedBlock().getLocation()) >= FileManager.get("config.yml").getInt("chest-limit")){
                    MessageUtils.getInstance().sendMessage(player, "max-chest-linked");
                    InventoryListener.isLinking.remove(player.getUniqueId());
                    return;
                }

                ChunkHopper chunkHopper = InventoryListener.isLinking.get(player.getUniqueId());
                chunkHopper.setLinkedChest(event.getClickedBlock().getLocation());

                MessageUtils.getInstance().sendMessage(player, "chest-linked");
            } else {
                MessageUtils.getInstance().sendMessage(player, "link-chest-only");
            }
            InventoryListener.isLinking.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        for(ChunkHopper chunkHopper : ChunkHopperManager.getInstance().loadedHoppers){
            if(chunkHopper.getUuid().equals(player.getUniqueId())){
                if(!(HopperTimerManager.getInstance().isHopperEmpty(chunkHopper)) && !(HopperTimerManager.getInstance().hasActiveTask(chunkHopper)) && (chunkHopper.getLinkedChest() != null)){
                    HopperTimerManager.getInstance().startHopperTask(chunkHopper);
                }
            }
        }

        if(!HopperTimerManager.getInstance().isRepeatRunning){
            HopperTimerManager.getInstance().startHopperRepeating();
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){

        Player player = event.getPlayer();
        for(ChunkHopper chunkHopper : ChunkHopperManager.getInstance().loadedHoppers){
            if(chunkHopper.getUuid().equals(player.getUniqueId())){
                if(HopperTimerManager.getInstance().hasActiveTask(chunkHopper)){
                    HopperTimerManager.getInstance().stopHopperTask(chunkHopper);
                }
            }
        }

    }
}
