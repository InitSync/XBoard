package net.xboard.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import net.xboard.XBoard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for Strings.
 *
 * @author InitSync
 * @version 1.0.0
 * @since 1.0.0
 */
public final class TextUtils {
	private static final int VERSION = Integer.parseInt(Bukkit.getBukkitVersion()
		 .split("-")[0]
		 .split("\\.")[1]);
	private static final boolean PLACEHOLDERS = XBoard.instance()
		 .getServer()
		 .getPluginManager()
		 .getPlugin("PlaceholderAPI") != null;
	private static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}}");
	private static final StringBuilder BUILDER = new StringBuilder();
	
	private TextUtils() {}
	
	/**
	 * Parse the string with the placeholders provided by PlaceholderAPI.
	 *
	 * @param player Player Object for apply the placeholders.
	 * @param text Text to parse.
	 * @return Text parsed with the placeholders.
	 */
	public static @NotNull String parse(@NotNull Player player, @NotNull String text) {
		Objects.requireNonNull(player, "The player is null.");
		
		if (!PLACEHOLDERS) return colorize(text);
		
		return colorize(PlaceholderAPI.setPlaceholders(player, text));
	}
	
	/**
	 * Colorize the string passed as parameter, if the server version number is lower than 16 (-1.16),
	 * will colorize the colors normally, overwise, will apply the HEX colors.
	 * <p>
	 * [!] Code taken out from SternalBoard repository:
	 * https://github.com/ShieldCommunity/SternalBoard/blob/main/plugin/src/main/java/com/xism4
	 * /sternalboard/utils/PlaceholderUtils.java
	 *
	 * @param text Text to colorize.
	 * @return Parsed and colorized text.
	 */
	public static @NotNull String colorize(@NotNull String text) {
		text = text.replace("<br>", "\n");
		
		if (VERSION < 16) return ChatColor.translateAlternateColorCodes('&', text);
		
		String[] parts = text.split(String.format("((?<=%1$s)|(?=%1$s))", "&"));
		Matcher matcher = HEX_PATTERN.matcher(text);
		
		if (text.contains("&#")) {
			final int length = parts.length;
			for (int i = 0 ; i < length ; i++) {
				if (parts[i].equalsIgnoreCase("&")) {
					i++;
					if (parts[i].charAt(0) == '#') BUILDER.append(ChatColor.of(parts[i].substring(0, 7)));
					else BUILDER.append(ChatColor.translateAlternateColorCodes('&', "&" + parts[i]));
				} else {
					while (matcher.find()) {
						final String color = parts[i].substring(matcher.start(), matcher.end());
						parts[i] = parts[i].replace(color, ChatColor.of(color) + "");
						matcher = HEX_PATTERN.matcher(text);
					}
					BUILDER.append(parts[i]);
				}
			}
		} else {
			while (matcher.find()) {
				final String color = text.substring(matcher.start(), matcher.end());
				text = text.replace(color, ChatColor.of(color) + "");
				matcher = HEX_PATTERN.matcher(text);
			}
			BUILDER.append(text);
		}
		
		return BUILDER.toString();
	}
}
