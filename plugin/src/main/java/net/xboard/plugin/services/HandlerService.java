package net.xboard.plugin.services;

import net.xboard.plugin.XBoard;
import net.xboard.plugin.loaders.CommandHandler;
import net.xboard.plugin.scoreboard.SimpleScoreboardHandler;
import net.xboard.plugin.utils.UpdateHandler;
import net.xconfig.bukkit.config.BukkitConfigurationHandler;
import org.bukkit.plugin.java.JavaPlugin;

public interface HandlerService {
	/**
	 * Returns a new instance of ScoreboardHandlerImpl object.
	 *
	 * @param plugin A XBoard instance.
	 * @param configurationHandler A BukkitConfigurationHandler/Implementation object.
	 * @return A ScoreboardHandlerImpl instance.
	 */
	static SimpleScoreboardHandler newScoreboardHandler(XBoard plugin, BukkitConfigurationHandler configurationHandler) {
		return new SimpleScoreboardHandler(plugin, configurationHandler);
	}
	
	/**
	 * Returns a new instance of CommandHandler.Builder object.
	 *
	 * @param plugin A JavaPlugin instance.
	 * @return A CommandHandler.Builder object.
	 */
	static CommandHandler.Builder newCommandLoader(JavaPlugin plugin) {
		return new CommandHandler.Builder(plugin);
	}
	
	static UpdateHandler newUpdateChecker(int resourceNumber) {
		return new UpdateHandler(resourceNumber);
	}
}
