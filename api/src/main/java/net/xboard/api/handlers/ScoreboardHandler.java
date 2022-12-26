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
	 * Creates a new scoreboard to player.
	 *
	 * @param player Player object.
	 */
	void create(Player player);
	
	/**
	 * Removes the scoreboard to player.
	 *
	 * @param uuid Uuid of player.
	 */
	void remove(UUID uuid);
	
	void clean();
}
