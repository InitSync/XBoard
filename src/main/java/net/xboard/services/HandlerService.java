package net.xboard.services;

import net.xboard.XBoard;
import net.xboard.loaders.CommandHandler;
import net.xboard.scoreboard.ScoreboardHandlerImpl;
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
	static ScoreboardHandlerImpl scoreboardHandler(XBoard plugin, BukkitConfigurationHandler configurationHandler) {
		return new ScoreboardHandlerImpl(plugin, configurationHandler);
	}
	
	/**
	 * Returns a new instance of CommandHandler.Builder object.
	 *
	 * @param plugin A JavaPlugin instance.
	 * @return A CommandHandler.Builder object.
	 */
	static CommandHandler.Builder commandLoader(JavaPlugin plugin) {
		return new CommandHandler.Builder(plugin);
	}
}
