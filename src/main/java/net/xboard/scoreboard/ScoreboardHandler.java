package net.xboard.scoreboard;

import fr.mrmicky.fastboard.FastBoard;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

/**
 * Model for the ScoreboardHandler.
 *
 * @author InitSync
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ScoreboardHandler {
	/**
	 * Returns a FastBoard object using the uuid as a key.
	 *
	 * @param uuid Uuid of player.
	 * @return A FastBoard object.
	 */
	@Nullable FastBoard getByUuid(@NotNull UUID uuid);
	
	/**
	 * Returns the Map that contains all the FastBoard objects (Scoreboards).
	 *
	 * @return A Map object.
	 */
	@NotNull Map<UUID, FastBoard> scoreboards();
	
	/**
	 * Returns the Map that contains all the Scoreboard tasks.
	 *
	 * @return A Map object.
	 */
	@NotNull Map<UUID, BukkitTask> tasks();
	
	/**
	 * Creates a new scoreboard to player.
	 *
	 * @param player Player object.
	 */
	void create(@NotNull Player player);
	
	/**
	 * Removes the scoreboard to player.
	 *
	 * @param uuid Uuid of player.
	 */
	void remove(@NotNull UUID uuid);
	
	/**
	 * Toggles the scoreboard visibility.
	 *
	 * @param player Player object.
	 * @return A boolean value.
	 */
	boolean toggle(@NotNull Player player);
}
