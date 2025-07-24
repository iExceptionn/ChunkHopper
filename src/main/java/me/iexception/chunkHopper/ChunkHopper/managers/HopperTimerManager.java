package me.iexception.chunkHopper.ChunkHopper.managers;

import me.iexception.chunkHopper.ChunkHopper.ChunkHopper;
import me.iexception.chunkHopper.Core;
import me.iexception.chunkHopper.utils.FileManager;
import me.iexception.chunkHopper.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HopperTimerManager {

    private static final HopperTimerManager hopperTimerManager = new HopperTimerManager();
    private final Map<ChunkHopper, HopperTask> activeTasks = new HashMap<>();
    public boolean isRepeatRunning;

    public boolean hasActiveTask(ChunkHopper chunkHopper) {
        return activeTasks.containsKey(chunkHopper);
    }

    public void startHopperTask(ChunkHopper chunkHopper) {
        stopHopperTask(chunkHopper);
        HopperTask task = new HopperTask(chunkHopper);

        Integer integer;
        switch (chunkHopper.getType().toLowerCase()){
            case "rare":
                integer = FileManager.get("config.yml").getInt("settings.rare-hopper");
                break;
            case "legendary":
                integer = FileManager.get("config.yml").getInt("settings.legendary-hopper");
                break;
            case "common":
            default:
                integer = FileManager.get("config.yml").getInt("settings.common-hopper");
                break;
        }

        task.runTaskTimer(Core.getInstance(), 0, integer);
        activeTasks.put(chunkHopper, task);
    }

    public void stopHopperTask(ChunkHopper chunkHopper) {
        HopperTask task = activeTasks.get(chunkHopper);
        if (task != null) {
            task.cancel();
            activeTasks.remove(chunkHopper);
        }
    }

    public class HopperTask extends BukkitRunnable {
        private final ChunkHopper chunkHopper;
        private UUID lastHopperUUID;

        public HopperTask(ChunkHopper chunkHopper) {
            this.chunkHopper = chunkHopper;
            this.lastHopperUUID = generateInventoryUUID(chunkHopper.getLocation());
        }

        @Override
        public void run() {
            Location hopperLocation = chunkHopper.getLocation();
            Location chestLocation = chunkHopper.getLinkedChest();

            if (hopperLocation == null || chestLocation == null) {
                stopTask();
                return;
            }

            Block hopperBlock = hopperLocation.getBlock();
            Block chestBlock = chestLocation.getBlock();

            if (!(hopperBlock.getState() instanceof Hopper) || !(chestBlock.getState() instanceof Chest)) {
                stopTask();
                return;
            }

            Hopper hopper = (Hopper) hopperBlock.getState();
            Chest chest = (Chest) chestBlock.getState();
            Inventory hopperInv = hopper.getInventory();
            Inventory chestInv = chest.getInventory();

            if (isHopperEmpty(hopperInv)) {
                stopTask();
                return;
            }

            UUID newHopperUUID = generateInventoryUUID(hopperLocation);
            if (!newHopperUUID.equals(lastHopperUUID)) {
                stopTask();
                return;
            }

            int maxItems = getTransferAmount(chunkHopper);
            int itemsMoved = 0;

            for (int i = 0; i < hopperInv.getSize(); i++) {
                if (itemsMoved >= maxItems) break;

                ItemStack item = hopperInv.getItem(i);
                if (item != null && item.getType() != Material.AIR) {

                    int itemsToMove = Math.min(item.getAmount(), maxItems - itemsMoved);
                    ItemStack singleItem = new ItemStack(item.getType(), itemsToMove);

                    ItemMeta itemMeta = item.getItemMeta();
                    if (itemMeta != null) {
                        singleItem.setItemMeta(itemMeta);
                    }

                    Map<Integer, ItemStack> leftOver = chestInv.addItem(singleItem);
                    if (leftOver.isEmpty()) {
                        itemsMoved += itemsToMove;
                        int newAmount = item.getAmount() - itemsToMove;
                        hopperInv.setItem(i, newAmount > 0 ? new ItemStack(item.getType(), newAmount) : null);
                    } else {
                        // De chest had onvoldoende ruimte, dus stop
                        break;
                    }
                }
            }

            ChunkHopperManager.getInstance().collectDrops(chunkHopper);

            // Update UUID
            lastHopperUUID = generateInventoryUUID(hopperLocation);
        }

        private void stopTask() {
            cancel();
            HopperTimerManager.getInstance().stopHopperTask(chunkHopper);
        }
    }

    private static UUID generateInventoryUUID(Location loc) {
        Block block = loc.getBlock();
        if (block.getState() instanceof Hopper hopper) {
            return UUID.nameUUIDFromBytes(hopper.getInventory().toString().getBytes());
        } else if (block.getState() instanceof Chest chest) {
            return UUID.nameUUIDFromBytes(chest.getInventory().toString().getBytes());
        }
        return UUID.randomUUID();
    }

    public boolean isHopperEmpty(Inventory hopperInv) {
        for (ItemStack item : hopperInv.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                return false;
            }
        }
        return true;
    }

    public boolean isHopperEmpty(ChunkHopper chunkHopper) {
        Block hopperBlock = chunkHopper.getLocation().getBlock();

        if (hopperBlock.getType() != Material.HOPPER) {
            return true;
        }

        BlockState state = hopperBlock.getState();
        if (!(state instanceof Container)) {
            return true;
        }

        Inventory hopperInv = ((Container) state).getInventory();

        // Controleer of er iets in de hopper zit
        for (ItemStack item : hopperInv.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                return false;
            }
        }

        return true;
    }

    public static HopperTimerManager getInstance() {
        return hopperTimerManager;
    }

    public void startHopperRepeating(){

        if(Bukkit.getOnlinePlayers().isEmpty()){
            isRepeatRunning = false;
            return;
        }

        isRepeatRunning = true;

        Bukkit.getScheduler().runTaskTimer(Core.getInstance(), new Runnable() {
            @Override
            public void run() {
                for(ChunkHopper chunkHopper : ChunkHopperManager.getInstance().loadedHoppers){
                    if(Bukkit.getPlayer(chunkHopper.getUuid()) != null){
                        if(!hasActiveTask(chunkHopper)){
                            if(!isHopperEmpty(chunkHopper)){
                                startHopperTask(chunkHopper);
                            }
                        }
                    }
                }
            }
        }, 0L, 20L * 5L);

    }

    private Integer getTransferAmount(ChunkHopper chunkHopper){

        switch (chunkHopper.getType().toLowerCase()){
            case "rare":
                return FileManager.get("config.yml").getInt("item-settings.rare-hopper");
            case "legendary":
                return FileManager.get("config.yml").getInt("item-settings.legendary-hopper");
            case "common":
            default:
                return FileManager.get("config.yml").getInt("item-settings.common-hopper");
        }
    }
}