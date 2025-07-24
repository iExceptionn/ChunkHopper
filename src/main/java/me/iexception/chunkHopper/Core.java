package me.iexception.chunkHopper;

import dev.rosewood.rosestacker.api.RoseStackerAPI;
import me.iexception.chunkHopper.ChunkHopper.commands.ChuckHopperCommand;
import me.iexception.chunkHopper.ChunkHopper.events.InventoryListener;
import me.iexception.chunkHopper.ChunkHopper.events.PlayerEvents;
import me.iexception.chunkHopper.ChunkHopper.managers.ChunkHopperManager;
import me.iexception.chunkHopper.ChunkHopper.events.ItemEvents;
import me.iexception.chunkHopper.utils.FileManager;
import me.iexception.chunkHopper.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Core extends JavaPlugin implements Listener {

    public static Core instance;
    public RoseStackerAPI roseStackerAPI;

    public static Core getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        loadConfigs();
        loadEvents();
        loadCommands();
        MessageUtils.getInstance().loadMessages();

        ChunkHopperManager.getInstance().LoadHoppers();


        MessageUtils.getInstance().sendConsoleMessage("enable"
                , String.valueOf(MessageUtils.getInstance().loadedMessage.size())
                , String.valueOf(ChunkHopperManager.getInstance().loadedHoppers.size()));

        if (Bukkit.getPluginManager().isPluginEnabled("RoseStacker")) {
            this.roseStackerAPI = RoseStackerAPI.getInstance();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        MessageUtils.getInstance().sendConsoleMessage("disable"
                , String.valueOf(ChunkHopperManager.getInstance().loadedHoppers.size()));
        ChunkHopperManager.getInstance().saveHoppers();


        instance = null;
    }

    private void loadConfigs() {
        FileManager.load(this, "config.yml");
        FileManager.load(this, "messages.yml");
        FileManager.load(this, "chunkhoppers.yml");
        FileManager.load(this, "items.yml");
    }

    private void loadEvents() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(this, this);
        pluginManager.registerEvents(new ItemEvents(), this);
        pluginManager.registerEvents(new PlayerEvents(), this);
        pluginManager.registerEvents(new InventoryListener(), this);
    }

    private void loadCommands() {
        getCommand("chunkhopper").setExecutor(new ChuckHopperCommand());
    }
}
