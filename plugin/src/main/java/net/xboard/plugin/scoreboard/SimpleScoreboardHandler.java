package net.xboard.plugin.scoreboard;

import net.xboard.api.handlers.ScoreboardHandler;
import net.xboard.api.scoreboard.SimpleBoard;
import net.xboard.plugin.XBoard;
import net.xboard.plugin.scoreboard.tasks.TitleUpdateTask;
import net.xboard.plugin.utils.LogUtils;
import net.xboard.plugin.utils.PlaceholderUtils;
import net.xconfig.bukkit.config.BukkitConfigurationHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
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
	 * Creates a new scoreboard to player checking the mode specified at the configuration.
	 *
	 * @param player Player object.
	 */
	@Override
	public void create(Player player) {
		if (configurationHandler.condition("", "config.yml", "config.scoreboard.allow")) {
			String scoreboardMode = configurationHandler.text("", "config.yml", "config.scoreboard.mode", false);
			
			switch (scoreboardMode) {
				default:
					LogUtils.error("Cannot create the scoreboard to player because the mode specified isn't valid -> " + scoreboardMode);
					break;
				case "GLOBAL":
					createGlobal(player);
					break;
				case "WORLD": createByWorld(player);
			}
		}
	}
	
	/**
	 * Creates a new scoreboard to player using the world name specified.
	 *
	 * @param player    Player object.
	 */
	@Override
	public void createByWorld(Player player) {
		ConfigurationSection section = configurationHandler.configSection("", "config.yml", "config.scoreboard.types");

		SimpleBoard board;
		UUID playerId;
		int titleRate;
		
		for (String key : section.getKeys(false)) {
			section = configurationHandler.configSection("", "config.yml", "config.scoreboard.types." + key);
			
			if (!player.getWorld().getName().equals(section.getName())) continue;
			
			board = new SimpleBoard(player);
			playerId = player.getUniqueId();
			scoreboards.put(playerId, board);
			
			if (section.getBoolean("allow-animated-title")) {
				titleRate = section.getInt("title.update-rate");
				
				titleTasks.put(
					 playerId,
					 new TitleUpdateTask(board, section.getStringList("title.lines"), titleRate).runTaskTimerAsynchronously(plugin, 0L, titleRate)
				);
				
				board.updateTitle(board.getTitle());
			} else board.updateTitle(PlaceholderUtils.parse(player, section.getString("title.default")));
			
			// Final temp variables due to asynchronous process.
			ConfigurationSection finalSection = section;
			SimpleBoard finalBoard = board;
			
			boardTasks.put(
				 playerId,
				 Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
					 finalBoard.updateLines(PlaceholderUtils.parse(player, finalSection.getString("body.lines")).split("\n"));
				 }, 0L, section.getInt("body.update-rate")));
		}
	}
	
	/**
	 * Creates a new scoreboard global.
	 *
	 * @param player Player object.
	 */
	@Override
	public void createGlobal(Player player) {
		if (!configurationHandler.textList("", "config.yml", "config.scoreboard.worlds", false).contains(player.getWorld().getName())) return;
		
		SimpleBoard board = new SimpleBoard(player);
		UUID playerId = player.getUniqueId();
		scoreboards.put(playerId, board);
		
		if (configurationHandler.condition("", "config.yml", "config.scoreboard.global.allow-animated-title")) {
			int titleRate = configurationHandler.number("", "config.yml", "config.scoreboard.global.title.update-rate");
			
			titleTasks.put(
				 playerId,
				 new TitleUpdateTask(
					  board,
					  configurationHandler.textList("", "config.yml", "config.scoreboard.global.title.lines", true),
					  titleRate
				 ).runTaskTimerAsynchronously(plugin, 0L, titleRate)
			);
			
			board.updateTitle(board.getTitle());
		} else {
			board.updateTitle(PlaceholderUtils.parse(player, configurationHandler.text("", "config.yml", "config.scoreboard.title.default", true)));
		}
		
		boardTasks.put(playerId,
			 Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
				 board.updateLines(PlaceholderUtils.parse(player, configurationHandler.text("", "config.yml", "config.scoreboard.body.lines", true))
					  .split("\n"));
			 }, 0L, configurationHandler.number("", "config.yml", "config.scoreboard.body.update-rate")));
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
	
	/**
	 * Reloads all the enabled scoreboards.
	 */
	@Override
	public void reload() {
		if (configurationHandler.condition("", "config.yml", "config.scoreboard.allow-animated-title")) titleTasks.clear();
		
		boardTasks.clear();
		scoreboards.clear();
		
		plugin.getConfigurationManager().reload("", "config.yml");
		
		Bukkit.getOnlinePlayers().forEach(this::create);
	}
}
