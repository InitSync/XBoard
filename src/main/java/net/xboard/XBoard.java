package net.xboard;

import net.xboard.listeners.ScoreboardListener;
import net.xboard.scoreboard.ScoreboardHandler;
import net.xboard.services.HandlerService;
import net.xboard.utils.LogPrinter;
import net.xconfig.bukkit.config.BukkitConfigurationHandler;
import net.xconfig.bukkit.config.BukkitConfigurationModel;
import net.xconfig.services.ConfigurationService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Main Class.
 *
 * @author InitSync
 * @version 1.0.0
 * @since 1.0.0
 * @see org.bukkit.plugin.java.JavaPlugin
 */
public final class XBoard extends JavaPlugin {
	private static XBoard instance;
	
	public final String release = this.getDescription().getVersion();
	
	private BukkitConfigurationModel configurationManager;
	private BukkitConfigurationHandler configurationHandler;
	private ScoreboardHandler scoreboardHandler;

	public XBoard() {
		instance = this;
		
		configurationManager = ConfigurationService.bukkitManager(this);
		configurationHandler = ConfigurationService.bukkitHandler(configurationManager);
		scoreboardHandler = HandlerService.scoreboardHandler(this, configurationHandler);
	}
	
	public static @NotNull XBoard instance() {
		if (instance == null) {
			throw new IllegalStateException("Cannot access to the XBoard instance.");
		}
		return instance;
	}
	
	/**
	 * Returns the BukkitConfigurationModel object if this isn't null, overwise, will be throws an
	 * IllegalStateException.
	 *
	 * @return A BukkitConfigurationModel object.
	 */
	public @NotNull BukkitConfigurationModel configurationManager() {
		if (configurationManager == null) {
			throw new IllegalStateException("Cannot access to the BukkitConfigurationModel object.");
		}
		return configurationManager;
	}
	
	/**
	 * If the BukkitConfigurationHandler object is null will be throws an IllegalStateException,
	 * overwise, return it.
	 *
	 * @return A BukkitConfigurationHandler object.
	 */
	public @NotNull BukkitConfigurationHandler configurationHandler() {
		if (configurationHandler == null) {
			throw new IllegalStateException("Cannot access to the BukkitConfigurationHandler object.");
		}
		return configurationHandler;
	}
	
	/**
	 * Returns the ScoreboardHandler object, if is null, will be return null.
	 *
	 * @return A ScoreboardHandler object.
	 */
	public @NotNull ScoreboardHandler scoreboardHandler() {
		if (scoreboardHandler == null) {
			throw new IllegalStateException("Cannot get the ScoreboardHandler object because is null.");
		}
		return scoreboardHandler;
	}
	
	@Override
	public void onEnable() {
		final long startTime = System.currentTimeMillis();
		
		configurationManager.create("",
			 "config.yml",
			 "messages.yml");
		configurationManager.load("config.yml", "messages.yml");
		
		getServer().getPluginManager().registerEvents(new ScoreboardListener(scoreboardHandler), this);
		
		LogPrinter.info("Started plugin successfully in '" + (System.currentTimeMillis() - startTime) + "'ms.",
			 "Running with [" + Bukkit.getVersion() + "]",
			 "Developed by InitSync. Using latest version: " + release);
	}
	
	@Override
	public void onDisable() {
		LogPrinter.info("Disabling plugin.", "Developed by InitSync. Using latest version: " + release);
		
		if (configurationManager != null) configurationManager = null;
		if (configurationHandler != null) configurationHandler = null;
		
		if (scoreboardHandler != null) scoreboardHandler = null;
		
		if (instance != null) instance = null;
	}
}
