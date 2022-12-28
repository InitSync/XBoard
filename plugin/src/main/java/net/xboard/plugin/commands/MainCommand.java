package net.xboard.plugin.commands;

import net.xboard.api.handlers.ScoreboardHandler;
import net.xboard.api.utils.XSound;
import net.xboard.plugin.XBoard;
import net.xboard.plugin.enums.Permission;
import net.xconfig.bukkit.config.BukkitConfigurationHandler;
import net.xconfig.bukkit.config.BukkitConfigurationModel;
import net.xconfig.bukkit.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public final class MainCommand
implements CommandExecutor {
	private final BukkitConfigurationModel configurationManager;
	private final BukkitConfigurationHandler configurationHandler;
	private final ScoreboardHandler scoreboardHandler;
	
	public MainCommand(BukkitConfigurationModel configurationManager, BukkitConfigurationHandler configurationHandler, ScoreboardHandler scoreboardHandler) {
		this.configurationManager = Objects.requireNonNull(configurationManager, "The BukkitConfigurationModel object is null.");
		this.configurationHandler = Objects.requireNonNull(configurationHandler, "The BukkitConfigurationHandler object is null.");
		this.scoreboardHandler = Objects.requireNonNull(scoreboardHandler, "The ScoreboardHandler object is null.");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String prefix = configurationHandler.text("", "config.yml", "config.prefix", true);
		String version = XBoard.getPlugin().release;
		
		if (!(sender instanceof Player)) {
			if (args.length == 0) {
				sender.sendMessage(TextUtils.colorize(prefix + "&f Running with &8[&b" + Bukkit.getVersion() + "&8]"));
				sender.sendMessage(TextUtils.colorize(prefix + "&f Developed by &aInitSync&f. Using latest version: " + version));
				return false;
			}
			
			switch (args[0]) {
				default:
					sender.sendMessage(configurationHandler.text("", "messages.yml", "messages.no-command", true).replace("<prefix>", prefix));
					break;
				case "help":
					configurationHandler.textList("", "messages.yml", "messages.help", true).forEach(sender::sendMessage);
					break;
				case "reload":
					if (args.length == 1) {
						configurationManager.reload("", "messages.yml");
						scoreboardHandler.reload();
						
						sender.sendMessage(configurationHandler.text("", "messages.yml", "messages.reload-all", true).replace("<prefix>", prefix));
						break;
					}
					
					switch (args[1]) {
						default:
							sender.sendMessage(configurationHandler.text("", "messages.yml", "messages.no-file", true).replace("<prefix>", prefix));
							break;
						case "config":
							scoreboardHandler.reload();
							
							sender.sendMessage(configurationHandler.text("", "messages.yml", "messages.reload-config", true).replace("<prefix>", prefix));
							break;
						case "messages":
							configurationManager.reload("", "messages.yml");
							
							sender.sendMessage(configurationHandler.text("", "messages.yml", "messages.reload-messages", true).replace("<prefix>", prefix));
					}
					break;
			}
			return false;
		}
		
		Player player = (Player) sender;
		
		if (args.length == 0) {
			player.sendMessage(TextUtils.colorize(prefix + "&f Running with &8[&b" + Bukkit.getVersion() + "&8]"));
			player.sendMessage(TextUtils.colorize(prefix + "&f Developed by &aInitSync&f. Using latest version: " + version));
			return false;
		}
		
		switch (args[0]) {
			default:
				player.sendMessage(configurationHandler.text("", "messages.yml", "messages.no-command", true).replace("<prefix>", prefix));
				break;
			case "help":
				if (!player.hasPermission(Permission.HELP_CMD.getPerm())) {
					player.playSound(
						 player.getLocation(),
						 XSound.matchXSound(configurationHandler.text("", "config.yml", "config.sounds.no-perm", false)).get().parseSound(),
						 configurationHandler.number("", "config.yml", "config.sounds.volume-level"),
						 configurationHandler.number("", "config.yml", "config.sounds.volume-level")
					);
					player.sendMessage(configurationHandler.text("", "messages.yml", "messages.no-perm", true).replace("<prefix>", prefix));
					return false;
				}
				
				configurationHandler.textList("", "messages.yml", "messages.help", true).forEach(player::sendMessage);
				break;
			case "reload":
				if (!player.hasPermission(Permission.RELOAD_CMD.getPerm())) {
					player.playSound(
						 player.getLocation(),
						 XSound.matchXSound(configurationHandler.text("", "config.yml", "config.sounds.no-perm", false)).get().parseSound(),
						 configurationHandler.number("", "config.yml", "config.sounds.volume-level"),
						 configurationHandler.number("", "config.yml", "config.sounds.volume-level")
					);
					player.sendMessage(configurationHandler.text("", "messages.yml", "messages.no-perm", true).replace("<prefix>", prefix));
					return false;
				}
				
				if (args.length == 1) {
					configurationManager.reload("", "messages.yml");
					scoreboardHandler.reload();
					
					player.playSound(
						 player.getLocation(),
						 XSound.matchXSound(configurationHandler.text("", "config.yml", "config.sounds.reload", false)).get().parseSound(),
						 configurationHandler.number("", "config.yml", "config.sounds.volume-level"),
						 configurationHandler.number("", "config.yml", "config.sounds.volume-level")
					);
					player.sendMessage(configurationHandler.text("", "messages.yml", "messages.reload-all", true).replace("<prefix>", prefix));
					break;
				}
				
				switch (args[1]) {
					default:
						player.sendMessage(configurationHandler.text("", "messages.yml", "messages.no-file", true).replace("<prefix>", prefix));
						break;
					case "config":
						scoreboardHandler.reload();
						
						player.playSound(
							 player.getLocation(),
							 XSound.matchXSound(configurationHandler.text("", "config.yml", "config.sounds.reload", false)).get().parseSound(),
							 configurationHandler.number("", "config.yml", "config.sounds.volume-level"),
							 configurationHandler.number("", "config.yml", "config.sounds.volume-level")
						);
						player.sendMessage(configurationHandler.text("", "messages.yml", "messages.reload-config", true).replace("<prefix>", prefix));
						break;
					case "messages":
						configurationManager.reload("", "messages.yml");
						
						player.playSound(
							 player.getLocation(),
							 XSound.matchXSound(configurationHandler.text("", "config.yml", "config.sounds.reload", false)).get().parseSound(),
							 configurationHandler.number("", "config.yml", "config.sounds.volume-level"),
							 configurationHandler.number("", "config.yml", "config.sounds.volume-level")
						);
						player.sendMessage(configurationHandler.text("", "messages.yml", "messages.reload-messages", true).replace("<prefix>", prefix));
						break;
				}
				break;
		}
		return false;
	}
}
