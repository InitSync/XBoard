package net.xboard.services;

import net.xboard.XBoard;
import net.xboard.scoreboard.ScoreboardHandlerImpl;
import net.xconfig.bukkit.config.BukkitConfigurationHandler;
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
}
