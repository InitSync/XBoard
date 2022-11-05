package net.xboard.services;

import net.xboard.XBoard;
import net.xboard.loaders.CommandHandler;
import net.xboard.scoreboard.ScoreboardHandlerImpl;
import net.xconfig.bukkit.config.BukkitConfigurationHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface HandlerService {
	/**
	 * Returns a new instance of ScoreboardHandlerImpl object.
	 *
	 * @param plugin The XBoard instance.
	 * @param configurationHandler A BukkitConfigurationHandler object.
	 * @return A ScoreboardHandlerImpl instance.
	 */
	@Contract ("_, _ -> new")
	static @NotNull ScoreboardHandlerImpl scoreboardHandler(
		 @NotNull XBoard plugin,
		 @NotNull BukkitConfigurationHandler configurationHandler
	) {
		return new ScoreboardHandlerImpl(plugin, configurationHandler);
	}
	
	/**
	 * Returns a new instance of CommandHandler.Builder object.
	 *
	 * @param plugin A JavaPlugin instance.
	 * @return A CommandHandler.Builder object.
	 */
	@Contract (value = "_ -> new", pure = true)
	static CommandHandler.@NotNull Builder commandLoader(@NotNull JavaPlugin plugin) {
		return new CommandHandler.Builder(plugin);
	}
}
