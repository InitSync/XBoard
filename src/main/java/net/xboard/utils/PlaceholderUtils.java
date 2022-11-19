package net.xboard.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.xboard.XBoard;
import net.xconfig.bukkit.utils.TextUtils;
import org.bukkit.entity.Player;

public final class PlaceholderUtils {
	private static final boolean PLACEHOLDERS = XBoard.instance()
		.getServer()
		.getPluginManager()
		.getPlugin("PlaceholderAPI") != null;
	
	private PlaceholderUtils() {}
	
	public static String parse(Player player, String text) {
		if (!PLACEHOLDERS) return TextUtils.colorize(text.replace("<br>", "\n"));
		
		return TextUtils.colorize(PlaceholderAPI.setPlaceholders(player, text));
	}
}
