package net.xboard.plugin.scoreboard;

import net.xboard.api.events.ScoreboardCreateEvent;
import net.xboard.api.handlers.ScoreboardHandler;
import net.xboard.api.scoreboard.SimpleBoard;
import net.xboard.plugin.XBoard;
import net.xboard.plugin.scoreboard.tasks.TitleUpdateTask;
import net.xboard.plugin.utils.PlaceholderUtils;
import net.xconfig.bukkit.config.BukkitConfigurationHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Implementation of ScoreboardHandler that handles internally the players Scoreboards.
 *
 * @author InitSync
 * @version 1.0.2
 * @since 1.0.0
 * @see ScoreboardHandler
 */
public final class SimpleScoreboardHandler
implements ScoreboardHandler {
	private final XBoard plugin;
	private final BukkitConfigurationHandler configurationHandler;
	private final Map<UUID, SimpleBoard> scoreboards;
	private final Map<UUID, BukkitTask> boardTasks;
	private final Map<UUID, BukkitTask> titleTasks;
	
	public SimpleScoreboardHandler(XBoard plugin, BukkitConfigurationHandler configurationHandler) {
		this.plugin = Objects.requireNonNull(plugin, "The XBoard instance is null.");
		this.configurationHandler = Objects.requireNonNull(configurationHandler, "The BukkitConfigurationHandler instance is null.");
		this.scoreboards = new HashMap<>();
		this.boardTasks = new HashMap<>();
		this.titleTasks = new HashMap<>();
	}
	
	/**
	 * Returns a SimpleBoard object using the uuid of player.
	 *
	 * @param uuid Uuid of player.
	 * @return A FastBoard object.
	 */
	@Override
	public SimpleBoard getScoreboard(UUID uuid) {
		return scoreboards.getOrDefault(uuid, null);
	}
	
	/**
	 * Creates a new scoreboard to player.
	 *
	 * @param player Player object.
	 */
	@Override
	public void create(Player player) {
		ScoreboardCreateEvent event = new ScoreboardCreateEvent(player);
		plugin.getServer()
			.getPluginManager()
			.callEvent(event);
		if (!event.isCancelled()) {
			if (configurationHandler.condition("", "config.yml", "config.scoreboard.allow")) {
				
				for (String worldName : configurationHandler.textList("", "config.yml", "config.scoreboard.worlds", false)) {
					if (!player.getWorld().getName().equals(worldName)) continue;
					
					SimpleBoard board = new SimpleBoard(player);
					UUID playerId = player.getUniqueId();
					scoreboards.put(playerId, board);
					
					if (configurationHandler.condition("", "config.yml", "config.scoreboard.allow-animated-title")) {
						titleTasks.put(playerId,
							 new TitleUpdateTask(configurationHandler, board).runTaskTimerAsynchronously(
								  plugin,
								  0L,
								  configurationHandler.number("", "config.yml", "config.scoreboard.title.update-rate")
							 )
						);
						
						board.updateTitle(PlaceholderUtils.parse(player, board.getTitle()));
					} else {
						board.updateTitle(PlaceholderUtils.parse(player, configurationHandler.text("", "config.yml", "config.scoreboard.title.default", true)));
					}
					
					boardTasks.put(playerId,
						 Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
							 board.updateLines(PlaceholderUtils.parse(player, configurationHandler.text("", "config.yml", "config.scoreboard.body.lines", true))
								  .split("\n"));
						 }, 0L, configurationHandler.number("", "config.yml", "config.scoreboard.body.update-rate")));
				}
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
		if (!scoreboards.containsKey(uuid) || !boardTasks.containsKey(uuid)) return;
		
		SimpleBoard board = scoreboards.remove(uuid);
		if (!board.isDeleted()) {
			if (configurationHandler.condition("", "config.yml", "config.scoreboard.allow-animated-title")) titleTasks.remove(uuid).cancel();
			
			boardTasks.remove(uuid).cancel();
			board.delete();
		}
	}
	
	@Override
	public void clean() {
		if (configurationHandler.condition("", "config.yml", "config.scoreboard.allow-animated-title")) titleTasks.clear();
		
		boardTasks.clear();
		scoreboards.clear();
	}
}
