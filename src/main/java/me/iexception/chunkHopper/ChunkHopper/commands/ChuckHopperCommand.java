package me.iexception.chunkHopper.ChunkHopper.commands;

import me.iexception.chunkHopper.ChunkHopper.ChunkHopper;
import me.iexception.chunkHopper.ChunkHopper.managers.ChunkHopperManager;
import me.iexception.chunkHopper.Core;
import me.iexception.chunkHopper.utils.ItemBuilder;
import me.iexception.chunkHopper.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ChuckHopperCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {


        switch (args.length) {
            case 0:
                if (!sender.hasPermission("chunkhopper.help") || !sender.hasPermission("chunkhopper.*")) {
                    MessageUtils.getInstance().sendMessage(sender, "no-permissions");
                    break;
                }
                MessageUtils.getInstance().sendMessage(sender, "command-help");
                break;
            case 1:
                if (args[0].equalsIgnoreCase("give")) {
                    if (!sender.hasPermission("chunkhopper.give") || !sender.hasPermission("chunkhopper.*")) {
                        MessageUtils.getInstance().sendMessage(sender, "no-permissions");
                        break;
                    }
                    MessageUtils.getInstance().sendMessage(sender, "command-usage");
                    break;
                }
                if (args[0].equalsIgnoreCase("add")) {
                    MessageUtils.getInstance().sendMessage(sender, "command-usage-add");
                    break;
                }
                if (args[0].equalsIgnoreCase("reload")) {
                    if (!sender.hasPermission("chunkhopper.reload") || !sender.hasPermission("chunkhopper.*")) {
                        MessageUtils.getInstance().sendMessage(sender, "no-permissions");
                        break;
                    }
                    MessageUtils.getInstance().sendMessage(sender, "command-usage-reload");

                    MessageUtils.getInstance().reloadMessages();

                    break;
                }

                if (args[0].equalsIgnoreCase("about")) {
                    if (!sender.hasPermission("chunkhopper.about") || !sender.hasPermission("chunkhopper.*")) {
                        MessageUtils.getInstance().sendMessage(sender, "no-permissions");
                        break;
                    }
                    PluginDescriptionFile pluginDescriptionFile = Core.getInstance().getDescription();
                    MessageUtils.getInstance().sendMessage(sender, "about"
                            , "iException"
                            , pluginDescriptionFile.getVersion()
                            , pluginDescriptionFile.getDescription()
                            , "ch, chunkh, chunkhop");

                    break;
                }

                if (args[0].equalsIgnoreCase("info")) {
                    if (!sender.hasPermission("chunkhopper.info") || !sender.hasPermission("chunkhopper.*")) {
                        MessageUtils.getInstance().sendMessage(sender, "no-permissions");
                        break;
                    }
                    if (!(sender instanceof Player)) break;
                    Player player = (Player) sender;
                    ChunkHopper chunkHopper = ChunkHopperManager.getInstance().getHopper(player.getChunk());
                    if (chunkHopper == null) {
                        MessageUtils.getInstance().sendMessage(sender, "chunk-info-none");
                        break;
                    } else {
                        MessageUtils.getInstance().sendMessage(sender, "chunk-info"
                                , Bukkit.getServer().getOfflinePlayer(chunkHopper.getUuid()).getName()
                                , chunkHopper.getType()
                                , String.valueOf(chunkHopper.getLocation().getX())
                                , String.valueOf(chunkHopper.getLocation().getY())
                                , String.valueOf(chunkHopper.getLocation().getZ()));
                    }
                    break;
                }
                if (!sender.hasPermission("chunkhopper.help") || !sender.hasPermission("chunkhopper.*")) {
                    MessageUtils.getInstance().sendMessage(sender, "no-permissions");
                    break;
                }
                MessageUtils.getInstance().sendMessage(sender, "command-help");
                break;
            case 2:
                if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("give")) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        MessageUtils.getInstance().sendMessage(sender, "player-not-online", args[1]);
                        break;
                    }

                    switch (args[0].toLowerCase()) {
                        case "add":
                            if(target == sender){
                                MessageUtils.getInstance().sendMessage(sender, "cant-add-yourself");
                                break;
                            }

                            MessageUtils.getInstance().sendMessage(sender, "added-player", target.getName());
                            break;
                        case "give":
                            if (!sender.hasPermission("chunkhopper.give") || !sender.hasPermission("chunkhopper.*")) {
                                MessageUtils.getInstance().sendMessage(sender, "no-permissions");
                                break;
                            }
                            MessageUtils.getInstance().sendMessage(sender, "command-usage");
                            break;
                    }

                    break;
                }
            case 3:
                if (!sender.hasPermission("chunkhopper.give") || !sender.hasPermission("chunkhopper.*")) {
                    MessageUtils.getInstance().sendMessage(sender, "no-permissions");
                    break;
                }
                if (args[2].equalsIgnoreCase("common") || args[2].equalsIgnoreCase("rare") || args[2].equalsIgnoreCase("legendary")) {
                    Player target = Bukkit.getPlayer(args[1]);
                    switch (args[2].toLowerCase()) {
                        case "common":
                            String commonName = MessageUtils.getInstance().getMessage("hopper-common");
                            ArrayList<String> commonLore = MessageUtils.getInstance().getStringList("hopper-common-lore");

                            ItemStack commonHopper = new ItemBuilder(Material.HOPPER, 1).setDisplayName(commonName).setLore(commonLore).build();
                            target.getInventory().addItem(commonHopper);

                            break;
                        case "rare":
                            String rareName = MessageUtils.getInstance().getMessage("hopper-rare");
                            ArrayList<String> rareLore = MessageUtils.getInstance().getStringList("hopper-rare-lore");

                            ItemStack rareHopper = new ItemBuilder(Material.HOPPER, 1).setDisplayName(rareName).setLore(rareLore).build();
                            target.getInventory().addItem(rareHopper);
                            break;
                        case "legendary":
                            String legendaryName = MessageUtils.getInstance().getMessage("hopper-legendary");
                            ArrayList<String> legendaryLore = MessageUtils.getInstance().getStringList("hopper-legendary-lore");

                            ItemStack legendaryHopper = new ItemBuilder(Material.HOPPER, 1).setDisplayName(legendaryName).setLore(legendaryLore).build();
                            target.getInventory().addItem(legendaryHopper);
                            break;
                        default:
                            MessageUtils.getInstance().sendMessage(sender, "command-usage");
                            break;
                    }
                }
                MessageUtils.getInstance().sendMessage(sender, "command-usage");
                break;
            case 4:
                if (!sender.hasPermission("chunkhopper.give") || !sender.hasPermission("chunkhopper.*")) {
                    MessageUtils.getInstance().sendMessage(sender, "no-permissions");
                    break;
                }
                if (args[2].equalsIgnoreCase("common") || args[2].equalsIgnoreCase("rare") || args[2].equalsIgnoreCase("legendary")) {
                    Player target = Bukkit.getPlayer(args[1]);
                    switch (args[2].toLowerCase()) {
                        case "common":
                            String commonName = MessageUtils.getInstance().getMessage("hopper-common");
                            ArrayList<String> commonLore = MessageUtils.getInstance().getStringList("hopper-common-lore");

                            ItemStack commonHopper = new ItemBuilder(Material.HOPPER, Integer.valueOf(args[3])).setDisplayName(commonName).setLore(commonLore).build();
                            target.getInventory().addItem(commonHopper);

                            break;
                        case "rare":
                            String rareName = MessageUtils.getInstance().getMessage("hopper-rare");
                            ArrayList<String> rareLore = MessageUtils.getInstance().getStringList("hopper-rare-lore");

                            ItemStack rareHopper = new ItemBuilder(Material.HOPPER, Integer.valueOf(args[3])).setDisplayName(rareName).setLore(rareLore).build();
                            target.getInventory().addItem(rareHopper);
                            break;
                        case "legendary":
                            String legendaryName = MessageUtils.getInstance().getMessage("hopper-legendary");
                            ArrayList<String> legendaryLore = MessageUtils.getInstance().getStringList("hopper-legendary-lore");

                            ItemStack legendaryHopper = new ItemBuilder(Material.HOPPER, Integer.valueOf(args[3])).setDisplayName(legendaryName).setLore(legendaryLore).build();
                            target.getInventory().addItem(legendaryHopper);
                            break;
                        default:
                            MessageUtils.getInstance().sendMessage(sender, "command-usage");
                            break;
                    }
                }
        }

        return false;
    }


}
