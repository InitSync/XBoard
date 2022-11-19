package net.xboard.scoreboard;

import fr.mrmicky.fastboard.FastBoard;
import net.xboard.XBoard;
import net.xboard.api.events.ScoreboardCreateEvent;
import net.xboard.scoreboard.tasks.TitleUpdateTask;
import net.xboard.utils.PlaceholderUtils;
import net.xconfig.bukkit.config.BukkitConfigurationHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * Implementation of ScoreboardHandler that handles internally the players Scoreboards.
 *
 * @author InitSync
 * @version 1.0.1
 * @since 1.0.0
 * @see net.xboard.scoreboard.ScoreboardHandler
 */
public final class ScoreboardHandlerImpl implements ScoreboardHandler {
	private final XBoard plugin;
	private final BukkitConfigurationHandler configurationHandler;
	private final Map<UUID, FastBoard> scoreboards;
	private final Map<UUID, BukkitTask> scoreboardTasks;
	private final Map<UUID, BukkitTask> titleTasks;
	
	public ScoreboardHandlerImpl(XBoard plugin, BukkitConfigurationHandler configurationHandler) {
		this.plugin = Objects.requireNonNull(plugin, "The XBoard instance is null.");
		this.configurationHandler = Objects.requireNonNull(configurationHandler, "The BukkitConfigurationHandler instance is null.");
		this.scoreboards = new HashMap<>();
		this.scoreboardTasks = new HashMap<>();
		this.titleTasks = new HashMap<>();
	}
	
	/**
	 * Returns a FastBoard object using the uuid as a key.
	 *
	 * @param uuid Uuid of player.
	 * @return A FastBoard object.
	 */
	@Override
	public FastBoard getByUuid(UUID uuid) {
		return scoreboards.getOrDefault(uuid, null);
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
						 final UUID playerId = player.getUniqueId();
						 scoreboards.put(playerId, board);
						 
						 if (configurationHandler.condition("config.yml", "config.scoreboard.allow-animated-title")) {
							 titleTasks.put(playerId,
								 new TitleUpdateTask(configurationHandler, board).runTaskTimerAsynchronously(plugin,
									 0L,
									 configurationHandler.number("config.yml", "config.scoreboard.title.update-rate")));
							 board.updateTitle(PlaceholderUtils.parse(player, board.getTitle()));
						 } else {
							 board.updateTitle(PlaceholderUtils.parse(player,
								 configurationHandler.text("config.yml", "config.scoreboard.title.default")
									 .replace("<release>", plugin.release)));
						 }
						 
						 scoreboardTasks.put(playerId,
							 Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
								 board.updateLines(PlaceholderUtils.parse(player,
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
		if (!scoreboards.containsKey(uuid) || !scoreboardTasks.containsKey(uuid)) return;
		
		final FastBoard board = scoreboards.remove(uuid);
		if (!board.isDeleted()) {
			if (configurationHandler.condition("config.yml", "config.scoreboard.allow-animated-title")) titleTasks.remove(uuid).cancel();
			scoreboardTasks.remove(uuid).cancel();
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
		
		if (!scoreboards.containsKey(playerId) || !scoreboardTasks.containsKey(playerId)) {
			create(player);
			return true;
		}
		
		final FastBoard board = scoreboards.remove(playerId);
		if (!board.isDeleted()) {
			if (configurationHandler.condition("config.yml", "config.scoreboard.allow-animated-title")) titleTasks.remove(playerId).cancel();
			scoreboardTasks.remove(playerId).cancel();
			board.delete();
		}
		return false;
	}
	
	@Override
	public void clean() {
		if (configurationHandler.condition("config.yml", "config.scoreboard.allow-animated-title")) titleTasks.clear();
		scoreboardTasks.clear();
		scoreboards.clear();
	}
}
