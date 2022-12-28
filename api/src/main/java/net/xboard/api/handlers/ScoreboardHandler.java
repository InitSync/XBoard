package net.xboard.api.handlers;

import net.xboard.api.scoreboard.SimpleBoard;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface ScoreboardHandler {
	/**
	 * Returns a SimpleBoard object using the uuid of player.
	 *
	 * @param uuid Uuid of player.
	 * @return A FastBoard object.
	 */
	SimpleBoard getScoreboard(UUID uuid);
	
	/**
	 * Creates a new scoreboard to player checking the mode specified at the configuration.
	 *
	 * @param player Player object.
	 */
	void create(Player player);
	
	/**
	 * Creates a new scoreboard to player using the world name specified.
	 *
	 * @param player Player object.
	 */
	void createByWorld(Player player);
	
	/**
	 * Creates a new scoreboard global.
	 *
	 * @param player Player object.
	 */
	void createGlobal(Player player);
	
	/**
	 * Removes the scoreboard to player.
	 *
	 * @param uuid Uuid of player.
	 */
	void remove(UUID uuid);
	
	/**
	 * Reloads all the enabled scoreboards.
	 */
	void reload();
}
