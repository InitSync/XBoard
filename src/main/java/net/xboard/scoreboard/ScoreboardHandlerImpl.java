package net.xboard.scoreboard;

import fr.mrmicky.fastboard.FastBoard;
import net.xboard.XBoard;
import net.xboard.api.events.ScoreboardCreateEvent;
import net.xboard.utils.TextUtils;
import net.xconfig.bukkit.config.BukkitConfigurationHandler;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * Implementation of ScoreboardHandler that handles internally the players Scoreboards.
 *
 * @author InitSync
 * @version 1.0.0
 * @since 1.0.0
 * @see net.xboard.scoreboard.ScoreboardHandler
 */
public final class ScoreboardHandlerImpl implements ScoreboardHandler {
	private final XBoard plugin;
	private final BukkitConfigurationHandler configurationHandler;
	private final Map<UUID, FastBoard> scoreboards;
	private final Map<UUID, BukkitTask> tasks;
	private final BukkitScheduler scheduler;
	
	public ScoreboardHandlerImpl(XBoard plugin, BukkitConfigurationHandler configurationHandler) {
		this.plugin = Objects.requireNonNull(plugin, "The XBoard instance is null.");
		this.configurationHandler = Objects.requireNonNull(configurationHandler, "The BukkitConfigurationHandler instance is null.");
		this.scoreboards = new HashMap<>();
		this.tasks = new HashMap<>();
		this.scheduler = plugin.getServer().getScheduler();
	}
	
	/**
	 * Returns a FastBoard object using the uuid as a key.
	 *
	 * @param uuid Uuid of player.
	 * @return A FastBoard object.
	 */
	@Override
	public FastBoard getByUuid(UUID uuid) {
		Objects.requireNonNull(uuid, "The uuid is null.");
		
		return scoreboards.getOrDefault(uuid, null);
	}
	
	/**
	 * Returns the Map that contains all the FastBoard objects (Scoreboards).
	 *
	 * @return A Map object.
	 */
	@Override
	public Map<UUID, FastBoard> scoreboards() {
		return scoreboards;
	}
	
	/**
	 * Returns the Map that contains all the Scoreboard tasks.
	 *
	 * @return A Map object.
	 */
	@Override
	public Map<UUID, BukkitTask> tasks() {
		return tasks;
	}
	
	/**
	 * Creates a new scoreboard to player.
	 *
	 * @param player Player object.
	 */
	@Override
	public void create(Player player) {
		final ScoreboardCreateEvent createEvent = new ScoreboardCreateEvent(player);
		plugin.getServer()
			.getPluginManager()
			.callEvent(createEvent);
		if (!createEvent.isCancelled()) {
			if (configurationHandler.condition("config.yml", "config.scoreboard.allow")) {
				configurationHandler.textList("config.yml", "config.scoreboard.worlds")
					 .forEach(world -> {
						 if (!player.getWorld().getName().equals(world)) return;
						
						 final FastBoard board = new FastBoard(player);
						 board.updateTitle(TextUtils.parse(player,
							 configurationHandler.text("config", "config.scoreboard.title")
								 .replace("<release>", plugin.release)));
						 
						 final UUID playerId = player.getUniqueId();
						 scoreboards.put(playerId, board);
						 tasks.put(playerId, scheduler.runTaskTimerAsynchronously(plugin, () -> {
							 board.updateLines(TextUtils.parse(player,
									 configurationHandler.text("config.yml", "config.scoreboard.body.lines"))
								 .split("\n"));
						 }, 0L, configurationHandler.number("config.yml", "config.scoreboard.body.update-rate")));
					 });
			}
		}
	}
	
	/**
	 * Removes the scoreboard to player.
	 *
	 * @param uuid Uuid of player.
	 */
	@Override
	public void remove(UUID uuid) {
		if (!scoreboards.containsKey(uuid) || !tasks.containsKey(uuid)) return;
		
		final FastBoard board = scoreboards.remove(uuid);
		if (!board.isDeleted()) {
			tasks.remove(uuid).cancel();
			board.delete();
		}
	}
	
	/**
	 * Toggle the scoreboard to player.
	 *
	 * @param player Player object.
	 */
	@Override
	public boolean toggle(Player player) {
		final UUID playerId = player.getUniqueId();
		
		if (!scoreboards.containsKey(playerId) || !tasks.containsKey(playerId)) {
			create(player);
			return true;
		}
		
		final FastBoard board = scoreboards.remove(playerId);
		if (!board.isDeleted()) {
			tasks.remove(playerId).cancel();
			board.delete();
		}
		
		return false;
	}
}
