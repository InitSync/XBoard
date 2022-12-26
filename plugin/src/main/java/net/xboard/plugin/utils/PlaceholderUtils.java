package net.xboard.plugin.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class PlaceholderUtils {
	private static final boolean PLACEHOLDERS = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
	
	private PlaceholderUtils() {}
	
	public static String parse(Player player, String text) {
		if (!PLACEHOLDERS) return text.replace("<br>", "\n");
		
		return PlaceholderAPI.setPlaceholders(player, text.replace("<br>", "\n"));
	}
}
