package me.iexception.chunkHopper.ChunkHopper.events;

import me.iexception.chunkHopper.ChunkHopper.ChunkHopper;
import me.iexception.chunkHopper.ChunkHopper.managers.ChunkHopperManager;
import me.iexception.chunkHopper.ChunkHopper.managers.HopperTimerManager;
import me.iexception.chunkHopper.Core;
import me.iexception.chunkHopper.utils.MessageUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemEvents implements Listener {

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {

        ItemStack itemStack = event.getItemInHand();

        Player player = event.getPlayer();
        if (event.getBlock().getType() == Material.HOPPER) {
            if (ChunkHopperManager.getInstance().getType(event.getItemInHand()) != null) {
                if (ChunkHopperManager.getInstance().getHopper(event.getBlock().getChunk()) == null) {
                    MessageUtils.getInstance().sendMessage(player, "hopper-placed");

                    String type = ChunkHopperManager.getInstance().getType(itemStack);


                    ChunkHopper chunkHopper = new ChunkHopper(event.getBlock().getChunk(), event.getBlock().getLocation(), player.getUniqueId(), type, null, null);
                    ChunkHopperManager.getInstance().addHopper(chunkHopper);
                } else {
                    MessageUtils.getInstance().sendMessage(player, "hopper-in-chunk");
                    event.setCancelled(true);
                }
            }
        }

    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();
        if (event.getBlock().getType() == Material.HOPPER) {
            ChunkHopper chunkHopper = ChunkHopperManager.getInstance().getHopper(event.getBlock().getChunk());
            if ((chunkHopper != null) && (chunkHopper.getChunk().equals(event.getBlock().getChunk())) && (chunkHopper.getLocation().equals(event.getBlock().getLocation()))) {
                if (!(chunkHopper.getUuid().equals(player.getUniqueId())) && !player.hasPermission("chunkhopper.bypass")) {
                    event.setCancelled(true);
                    return;
                }

                ChunkHopperManager.getInstance().removeHopper(chunkHopper);
                //MessageUtils.getInstance().sendMessage(player, "broke-hopper");

                if (player.getGameMode() == GameMode.CREATIVE) {
                    player.getInventory().addItem(ChunkHopperManager.getInstance().getType(chunkHopper.getType()));
                } else {
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), ChunkHopperManager.getInstance().getType(chunkHopper.getType()));

                    Inventory inventory = ChunkHopperManager.getInstance().getHopperInventory(event.getBlock().getLocation());
                    List<ItemStack> newDrops = new ArrayList<>();

                    for (ItemStack item : inventory.getContents()) {
                        if (item != null && item.getType() != Material.AIR) {
                            newDrops.add(item);
                        }
                    }

                    for (ItemStack drop : newDrops) {
                        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), drop);
                    }

                    event.setDropItems(false);
                }

                if (HopperTimerManager.getInstance().hasActiveTask(chunkHopper)) {
                    HopperTimerManager.getInstance().stopHopperTask(chunkHopper);
                }

            }
        }
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {

        Item item = event.getEntity();
        Chunk chunk = item.getLocation().getChunk();

        if (ChunkHopperManager.getInstance().getHopper(chunk) != null) {
            ChunkHopper chunkHopper = ChunkHopperManager.getInstance().getHopper(chunk);
            if (ChunkHopperManager.getInstance().checkIfHopperExist(chunkHopper.getLocation())) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        ChunkHopperManager.getInstance().collectDrops(chunkHopper);
                    }
                }.runTaskLater(Core.getInstance(), 1L);

                if (!HopperTimerManager.getInstance().hasActiveTask(chunkHopper)) {
                    if (chunkHopper.getLinkedChest() != null) {
                        HopperTimerManager.getInstance().startHopperTask(chunkHopper);
                    }
                }
            } else {
                ChunkHopperManager.getInstance().removeHopper(chunkHopper);
                MessageUtils.getInstance().sendConsoleMessage("hopper-not-found", Bukkit.getOfflinePlayer(chunkHopper.getUuid()).getName(), String.valueOf(chunkHopper.getLocation().getX()), String.valueOf(chunkHopper.getLocation().getY()), String.valueOf(chunkHopper.getLocation().getZ()), chunkHopper.getType());
            }
        }

    }

    @EventHandler
    public void onBlockExplode(EntityExplodeEvent event) {

        for (Block block : event.blockList()) {
            if (block.getType() == Material.HOPPER) {
                if (ChunkHopperManager.getInstance().getHopper(block.getChunk()) != null) {
                    ChunkHopper chunkHopper = ChunkHopperManager.getInstance().getHopper(block.getChunk());
                    if (chunkHopper.getLocation().equals(block.getLocation())) {
                        event.blockList().remove(block);
                    }

                }


            }
        }

    }

    @EventHandler
    public void transEvent(InventoryMoveItemEvent event) {

        if (event.getSource().getHolder() instanceof Hopper) {
            Hopper hopper = (Hopper) event.getSource().getHolder();
            if (ChunkHopperManager.getInstance().getHopper(event.getInitiator().getLocation().getChunk()) != null) {
                ChunkHopper chunkHopper = ChunkHopperManager.getInstance().getHopper(hopper.getLocation().getChunk());
                if (chunkHopper.getLocation().equals(hopper.getLocation())) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
