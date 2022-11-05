package net.xboard.services;

import net.xboard.scoreboard.ScoreboardHandlerImpl;
import net.xconfig.bukkit.config.BukkitConfigurationHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface HandlerService {
	/**
	 * Returns a new instance of ScoreboardHandlerImpl object.
	 *
	 * @param configurationHandler A BukkitConfigurationHandler object.
	 * @return A ScoreboardHandlerImpl instance.
	 */
	@Contract ("_ -> new")
	static @NotNull ScoreboardHandlerImpl scoreboardHandler(@NotNull BukkitConfigurationHandler configurationHandler) {
		return new ScoreboardHandlerImpl(configurationHandler);
	}
}
