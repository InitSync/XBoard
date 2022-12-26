package net.xboard.plugin.utils;

import com.google.common.base.Preconditions;
import net.xboard.plugin.XBoard;
import net.xtitle.api.TitleManager;
import org.bukkit.entity.Player;

public class Utils {
	private static final TitleManager TITLE_MANAGER = XBoard.getPlugin().getTitleManager();
	
	private Utils() {}
	
	public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		Preconditions.checkArgument(!title.isEmpty(), "The title message is empty.");
		Preconditions.checkArgument(!subtitle.isEmpty(), "The subtitle message is empty.");
		
		TITLE_MANAGER.sendTitle(player, fadeIn, stay, fadeOut, title, subtitle);
	}
	
	public static void clearTitle(Player player) {
		TITLE_MANAGER.clearTitle(player);
	}
	
	public static void sendActionBar(Player player, String message, long duration) {
		Preconditions.checkArgument(!message.isEmpty(), "The actionbar message is empty.");
		
		TITLE_MANAGER.sendActionBar(XBoard.getPlugin(), player, message, duration);
	}
}
