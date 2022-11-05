package net.xboard;

import net.xboard.utils.LogPrinter;
import net.xconfig.bukkit.config.BukkitConfigurationHandler;
import net.xconfig.bukkit.config.BukkitConfigurationModel;
import net.xconfig.services.ConfigurationService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class XBoard extends JavaPlugin {
	private static XBoard instance;
	
	public final String release = this.getDescription().getVersion();
	
	private BukkitConfigurationModel configurationManager;
	private BukkitConfigurationHandler configurationHandler;

	public XBoard() {
		instance = this;
		
		this.configurationManager = ConfigurationService.bukkitManager(this);
		this.configurationHandler = ConfigurationService.bukkitHandler(this.configurationManager);
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
		
		this.configurationManager.create("",
			 "config.yml",
			 "messages.yml");
		this.configurationManager.load("config.yml", "messages.yml");
		
		LogPrinter.info("Started plugin successfully in '" + (System.currentTimeMillis() - startTime) + "'ms.",
			 "Running with [" + Bukkit.getVersion() + "]",
			 "Developed by InitSync. Using latest version: " + this.release);
	}
	
	@Override
	public void onDisable() {
		LogPrinter.info("Disabling plugin.", "Developed by InitSync. Using latest version: " + this.release);
		
		if (this.configurationManager != null) this.configurationManager = null;
		if (this.configurationHandler != null) this.configurationHandler = null;
		
		if (instance != null) instance = null;
	}
}
