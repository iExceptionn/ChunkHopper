package me.iexception.chunkHopper.ChunkHopper;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.UUID;

public class ChunkHopper {

    private Chunk chunk;
    private Location location;
    private UUID uuid;
    private String type;
    private Location linkedChest;
    private ArrayList<Material> items;

    public ChunkHopper(Chunk chunk, Location location, UUID uuid, String type, Location linkedChest, ArrayList<Material> items){
        this.chunk = chunk;
        this.location = location;
        this.uuid = uuid;
        this.type = type;
        this.linkedChest = linkedChest;
        this.items = items;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public void setChunk(Chunk chunk) {
        this.chunk = chunk;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public ArrayList<Material> getItems() {
        return items;
    }

    public void setItems(ArrayList<Material> items) {
        this.items = items;
    }

    public Location getLinkedChest() {
        return linkedChest;
    }

    public void setLinkedChest(Location linkedChest) {
        this.linkedChest = linkedChest;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
