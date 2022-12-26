package net.xboard.plugin;

import net.xboard.api.handlers.ScoreboardHandler;
import net.xboard.plugin.commands.MainCommand;
import net.xboard.plugin.commands.ScoreboardCommand;
import net.xboard.plugin.commands.completers.MainCommandCompleter;
import net.xboard.plugin.commands.completers.ScoreboardCommandCompleter;
import net.xboard.plugin.listeners.ScoreboardListener;
import net.xboard.plugin.loaders.CommandHandler;
import net.xboard.plugin.utils.LogUtils;
import net.xboard.plugin.services.HandlerService;
import net.xconfig.bukkit.XConfigBukkit;
import net.xconfig.bukkit.config.BukkitConfigurationHandler;
import net.xconfig.bukkit.config.BukkitConfigurationModel;
import net.xtitle.api.AdaptManager;
import net.xtitle.api.TitleManager;
import net.xtitle.lib.XTitle;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class XBoard
extends JavaPlugin {
	public final String release = getDescription().getVersion();
	
	private static XBoard plugin;
	
	private BukkitConfigurationModel configurationManager;
	private BukkitConfigurationHandler configurationHandler;
	private AdaptManager adaptManager;
	private TitleManager titleManager;
	private ScoreboardHandler scoreboardHandler;

	@Override
	public void onLoad() {
		plugin = this;
		
		new Metrics(plugin, 16809);
		
		configurationManager = XConfigBukkit.manager(plugin);
		configurationHandler = XConfigBukkit.handler(configurationManager);
		adaptManager = XTitle.newAdaptManager();
		adaptManager.findAdapt();
		titleManager = XTitle.newTitleManager(adaptManager.getAdapt());
		scoreboardHandler = HandlerService.newScoreboardHandler(plugin, configurationHandler);
	}
	
	/**
	 * Returns the 'plugin' static field, if it is null, will be throws an IllegalStateException.
	 *
	 * @return A XBoard instance.
	 */
	public static XBoard getPlugin() {
		if (plugin == null) throw new IllegalStateException("Cannot access to the XBoard instance.");
		
		return plugin;
	}
	
	public TitleManager getTitleManager() {
		return titleManager;
	}
	
	@Override
	public void onEnable() {
		long startTime = System.currentTimeMillis();
		
		configurationManager.build("", "config.yml", "messages.yml");
		
		getServer().getPluginManager().registerEvents(new ScoreboardListener(scoreboardHandler), plugin);
		
		CommandHandler.Builder commandLoader = HandlerService.newCommandLoader(plugin);
		commandLoader.command("xboard")
			.executor(new MainCommand(configurationManager, configurationHandler, scoreboardHandler))
			.completer(new MainCommandCompleter())
			.build();
		
		commandLoader.command("scoreboard")
			.executor(new ScoreboardCommand(configurationHandler, scoreboardHandler))
			.completer(new ScoreboardCommandCompleter())
			.build();
		
		LogUtils.info(
			 "Started plugin successfully in '" + (System.currentTimeMillis() - startTime) + "'ms.",
			 "Running with [" + Bukkit.getVersion() + "]",
			 "Developed by InitSync. Using latest version: " + release
		);
		
		if (configurationHandler.condition("", "config.yml", "config.notify")) {
			HandlerService.newUpdateChecker(106173).version(latestRelease -> {
				if (release.equals(latestRelease)) LogUtils.info("There is not a new update available.");
				else LogUtils.warn("There is a new update available: " + latestRelease);
			});
		}
	}
	
	@Override
	public void onDisable() {
		LogUtils.info("Disabling plugin.", "Developed by InitSync. Using latest version: " + release);
		
		if (configurationManager != null) configurationManager = null;
		if (configurationHandler != null) configurationHandler = null;
		
		if (scoreboardHandler != null) scoreboardHandler = null;
		
		if (titleManager != null) titleManager = null;
		if (adaptManager != null) adaptManager = null;
		
		if (plugin != null) plugin = null;
	}
}
