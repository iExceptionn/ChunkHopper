package me.iexception.chunkHopper.utils;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtils {

    private final static Pattern pattern = Pattern.compile("#[a-fA-f0-9]{6}");

    public static @NotNull String format(String input) {

        input = input.replaceAll("%prefix%", FileManager.get("config.yml").getString("prefix"));

        Matcher match = pattern.matcher(input);
        while (match.find()) {
            String color = input.substring(match.start(), match.end());
            input = input.replace(color, ChatColor.of(color) + "");
            match = pattern.matcher(input);
        }

        return ChatColor.translateAlternateColorCodes('&', input);
    }

}
