package me.iexception.chunkHopper.ChunkHopper.managers;

import dev.rosewood.rosestacker.RoseStacker;
import dev.rosewood.rosestacker.api.RoseStackerAPI;
import dev.rosewood.rosestacker.stack.StackedItem;
import me.iexception.chunkHopper.ChunkHopper.ChunkHopper;
import me.iexception.chunkHopper.Core;
import me.iexception.chunkHopper.utils.FileManager;
import me.iexception.chunkHopper.utils.ItemBuilder;
import me.iexception.chunkHopper.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ChunkHopperManager {

    public ArrayList<ChunkHopper> loadedHoppers = new ArrayList<>();
    private static final ChunkHopperManager chunkHopperManager = new ChunkHopperManager();

    public void LoadHoppers() {

        if (FileManager.get("chunkhoppers.yml").getStringList("hoppers").isEmpty()) {
            return;
        }

        for (String key : FileManager.get("chunkhoppers.yml").getStringList("hoppers")) {

            String[] strings = key.split(";");
            Chunk chunk = Bukkit.getWorld(strings[2]).getChunkAt(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]));
            Location location = new Location(Bukkit.getWorld(strings[2]), Double.parseDouble(strings[3]), Double.parseDouble(strings[4]), Double.parseDouble(strings[5]));
            UUID uuid = UUID.fromString(strings[6]);
            String type = strings[7];

            Location linkedChest = null;
            if (!strings[8].equalsIgnoreCase("null")) {
                linkedChest = new Location(Bukkit.getWorld(strings[2]), Double.parseDouble(strings[8]), Double.parseDouble(strings[9]), Double.parseDouble(strings[10]));
            }

            if (checkIfHopperExist(location)) {
                ChunkHopper chunkHopper = new ChunkHopper(chunk, location, uuid, type, linkedChest, null);
                loadedHoppers.add(chunkHopper);
            } else {
                MessageUtils.getInstance().sendConsoleMessage("hopper-not-found", Bukkit.getOfflinePlayer(uuid).getName(), String.valueOf(location.getX()), String.valueOf(location.getY()), String.valueOf(location.getZ()), type);
            }

        }
        autoSave();
    }

    public void saveHoppers() {

        ArrayList<String> savingHoppers = new ArrayList<>();

        for (ChunkHopper chunkHopper : loadedHoppers) {


            Chunk chunk = chunkHopper.getChunk();
            Location location = chunkHopper.getLocation();
            UUID uuid = chunkHopper.getUuid();
            Location linkedChest = chunkHopper.getLinkedChest();

            String chunkString = (chunk.getX() + ";" + chunk.getZ());
            String locationString = (location.getWorld().getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ());

            String linkedChestString = "null";
            if (chunkHopper.getLinkedChest() != null) {
                linkedChestString = (linkedChest.getX() + ";" + linkedChest.blockY() + ";" + linkedChest.getZ());
            }

            String stringTotal = chunkString + ";" + locationString + ";" + uuid + ";" + chunkHopper.getType() + ";" + linkedChestString;
            savingHoppers.add(stringTotal);

        }

        FileManager.get("chunkhoppers.yml").set("hoppers", savingHoppers);
        FileManager.save(Core.getInstance(), "chunkhoppers.yml");
    }

    public boolean checkIfHopperExist(Location location) {
        return location.getBlock().getType() == Material.HOPPER;
    }

    public void addHopper(ChunkHopper chunkHopper) {
        loadedHoppers.add(chunkHopper);
    }

    public void removeHopper(ChunkHopper chunkHopper) {
        loadedHoppers.remove(chunkHopper);
    }

    public ChunkHopper getHopper(Chunk chunk) {
        for (ChunkHopper chunkHopper : loadedHoppers) {
            if (chunkHopper.getChunk().equals(chunk)) {
                return chunkHopper;
            }
        }
        return null;
    }

    public Inventory getHopperInventory(Location location) {
        if (location.getBlock().getType() == Material.HOPPER) {
            return ((org.bukkit.block.Hopper) location.getBlock().getState()).getInventory();
        }
        return null;
    }

    public void collectDrops(ChunkHopper chunkHopper) {
        Location hopperLocation = chunkHopper.getLocation();
        if (hopperLocation == null) return;

        Chunk chunk = hopperLocation.getChunk();
        Hopper hopper = (Hopper) hopperLocation.getBlock().getState();
        Inventory hopperInv = hopper.getInventory();

        for (Entity entity : chunk.getEntities()) {
            if (entity instanceof Item itemEntity) {
                ItemStack drop = itemEntity.getItemStack();
                StackedItem stackedItem = Core.getInstance().roseStackerAPI.getStackedItem(itemEntity);

                if (stackedItem != null) {
                    int stackSize = stackedItem.getStackSize();
                    drop.setAmount(stackSize);

                    HashMap<Integer, ItemStack> leftovers = hopperInv.addItem(drop);
                    if (leftovers.isEmpty()) {
                        Core.getInstance().roseStackerAPI.removeItemStack(stackedItem);
                        itemEntity.remove();
                    } else {
                        int remaining = leftovers.get(0).getAmount();
                        int accepted = stackSize - remaining;
                        if (accepted > 0) {
                            stackedItem.setStackSize(stackSize - accepted);
                            drop.setAmount(stackedItem.getStackSize());
                            itemEntity.setItemStack(drop);
                        }
                    }
                } else {
                    HashMap<Integer, ItemStack> leftovers = hopperInv.addItem(drop);
                    if (leftovers.isEmpty()) {
                        itemEntity.remove();
                    } else {
                        drop.setAmount(leftovers.get(0).getAmount());
                        itemEntity.setItemStack(drop);
                    }
                }
            }
        }
    }

    public void collectDrops(ChunkHopper chunkHopper, ItemStack itemStack) {
        Location hopperLocation = chunkHopper.getLocation();
        if (hopperLocation == null) return;

        Hopper hopper = (Hopper) hopperLocation.getBlock().getState();
        Inventory hopperInv = hopper.getInventory();

        for (int i = 0; i < itemStack.getAmount(); i++) {
            ItemStack itemToAdd = itemStack.clone();
            itemToAdd.setAmount(1);

            HashMap<Integer, ItemStack> leftovers = hopperInv.addItem(itemToAdd);

            if (!leftovers.isEmpty()) {
                itemToAdd.setAmount(leftovers.get(0).getAmount());
                hopperLocation.getWorld().dropItemNaturally(hopperLocation, itemToAdd);
            }
        }

    }

    private boolean isWithinRange(Location loc1, Location loc2, double range) {
        return loc1.getWorld().equals(loc2.getWorld()) && loc1.distanceSquared(loc2) <= range * range;
    }

    public String getType(ItemStack itemStack) {

        String commonName = MessageUtils.getInstance().getMessage("hopper-common");
        ArrayList<String> commonLore = MessageUtils.getInstance().getStringList("hopper-common-lore");

        ItemStack commonHopper = new ItemBuilder(Material.HOPPER, 1).setDisplayName(commonName).setLore(commonLore).build();

        if (isEqualItem(itemStack, commonHopper)) {
            return "common";
        }

        String rareName = MessageUtils.getInstance().getMessage("hopper-rare");
        ArrayList<String> rareLore = MessageUtils.getInstance().getStringList("hopper-rare-lore");

        ItemStack rareHopper = new ItemBuilder(Material.HOPPER, 1).setDisplayName(rareName).setLore(rareLore).build();

        if (isEqualItem(itemStack, rareHopper)) {
            return "rare";
        }

        String legendaryName = MessageUtils.getInstance().getMessage("hopper-legendary");
        ArrayList<String> legendaryLore = MessageUtils.getInstance().getStringList("hopper-legendary-lore");

        ItemStack legendaryHopper = new ItemBuilder(Material.HOPPER, 1).setDisplayName(legendaryName).setLore(legendaryLore).build();

        if (isEqualItem(itemStack, legendaryHopper)) {
            return "legendary";
        }

        return null;
    }

    public ItemStack getType(String type) {

        String commonName = MessageUtils.getInstance().getMessage("hopper-common");
        ArrayList<String> commonLore = MessageUtils.getInstance().getStringList("hopper-common-lore");

        ItemStack commonHopper = new ItemBuilder(Material.HOPPER, 1).setDisplayName(commonName).setLore(commonLore).build();

        if (type.equals("common")) {
            return commonHopper;
        }

        String rareName = MessageUtils.getInstance().getMessage("hopper-rare");
        ArrayList<String> rareLore = MessageUtils.getInstance().getStringList("hopper-rare-lore");

        ItemStack rareHopper = new ItemBuilder(Material.HOPPER, 1).setDisplayName(rareName).setLore(rareLore).build();

        if (type.equals("rare")) {
            return rareHopper;
        }

        String legendaryName = MessageUtils.getInstance().getMessage("hopper-legendary");
        ArrayList<String> legendaryLore = MessageUtils.getInstance().getStringList("hopper-legendary-lore");

        ItemStack legendaryHopper = new ItemBuilder(Material.HOPPER, 1).setDisplayName(legendaryName).setLore(legendaryLore).build();

        if (type.equals("legendary")) {
            return legendaryHopper;
        }

        return null;
    }

    private boolean isEqualItem(ItemStack item, ItemStack targetItem) {
        if (item == null || targetItem == null) return false;

        if (item.getType() != targetItem.getType()) return false;

        ItemMeta itemMeta = item.getItemMeta();
        ItemMeta targetItemMeta = targetItem.getItemMeta();

        if (itemMeta == null || targetItemMeta == null) return false;

        if (!itemMeta.getDisplayName().equals(targetItemMeta.getDisplayName())) return false;
        if (itemMeta.getLore() == null && targetItemMeta.getLore() != null) return false;
        if (itemMeta.getLore() != null && !itemMeta.getLore().equals(targetItemMeta.getLore())) return false;

        return true;
    }

    public int getLinkedChest(Location location) {

        Integer linkedChest = 0;

        for (ChunkHopper chunkHopper : loadedHoppers) {
            if ((chunkHopper.getLinkedChest() != null) && chunkHopper.getLinkedChest().equals(location)) {
                linkedChest++;
            }
        }

        return linkedChest;
    }

    public static ChunkHopperManager getInstance() {
        return chunkHopperManager;
    }

    public void autoSave() {


        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                saveHoppers();
                MessageUtils.getInstance().sendConsoleMessage("auto-saved", String.valueOf(loadedHoppers.size()));
                autoSave();
            }
        };

        task.runTaskLater(Core.getInstance(), 20 * 1800);
    }

}
