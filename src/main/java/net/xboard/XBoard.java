package net.xboard;

import net.xboard.commands.MainCommand;
import net.xboard.commands.ScoreboardCommand;
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
	
	@Override
	public void onEnable() {
		final long startTime = System.currentTimeMillis();
		
		configurationManager.create("",
			 "config.yml",
			 "messages.yml");
		configurationManager.load("config.yml", "messages.yml");
		
		getServer().getPluginManager().registerEvents(new ScoreboardListener(scoreboardHandler), this);
		
		HandlerService.commandLoader(this)
			.command("xboard")
			.executor(new MainCommand(configurationHandler, scoreboardHandler))
			.command("scoreboard")
			.executor(new ScoreboardCommand(configurationHandler, scoreboardHandler))
			.register();
		
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
