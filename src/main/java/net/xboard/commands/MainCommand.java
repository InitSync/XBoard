package net.xboard.commands;

import com.cryptomorin.xseries.XSound;
import net.xboard.XBoard;
import net.xboard.enums.Permission;
import net.xboard.scoreboard.ScoreboardHandler;
import net.xboard.utils.TextUtils;
import net.xconfig.bukkit.config.BukkitConfigurationHandler;
import net.xconfig.enums.Action;
import net.xconfig.enums.File;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class MainCommand implements CommandExecutor {
	private final BukkitConfigurationHandler configurationHandler;
	private final ScoreboardHandler scoreboardHandler;
	
	public MainCommand(
		@NotNull BukkitConfigurationHandler configurationHandler,
		@NotNull ScoreboardHandler scoreboardHandler
	) {
		this.configurationHandler = Objects.requireNonNull(configurationHandler, "The BukkitConfigurationHandler object is null.");
		this.scoreboardHandler = Objects.requireNonNull(scoreboardHandler, "The ScoreboardHandler object is null.");
	}
	
	@Override
	public boolean onCommand(
		@NotNull CommandSender sender,
		@NotNull Command command,
		@NotNull String label,
		@NotNull String[] args
	) {
		final String prefix = configurationHandler.text(
			File.CONFIG,
			"config.prefix",
			null);
		final String version = XBoard.instance().release;
		
		if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender)) return false;
		
		if (sender instanceof ConsoleCommandSender) {
			if (args.length == 0) {
				sender.sendMessage(TextUtils.colorize(prefix + "&f Running with &8[&b" + Bukkit.getVersion() + "&8]"));
				sender.sendMessage(TextUtils.colorize(prefix + "&f Developed by &aInitSync&f. Using latest version: " + version));
				return false;
			}
			
			switch (args[0]) {
				default:
					sender.sendMessage(TextUtils.colorize(configurationHandler.text(File.CUSTOM,
						"messages.no-command",
						"messages.yml")
						.replace("<prefix>", prefix)));
					break;
				case "help":
					configurationHandler.textList(File.CUSTOM,
						"messages.help",
						"messages.yml")
						.forEach(msg -> sender.sendMessage(TextUtils.colorize(msg.replace("<release>", version))));
					break;
				case "reload":
					if (args.length == 1) {
						scoreboardHandler.tasks().clear();
						scoreboardHandler.scoreboards().clear();
						
						configurationHandler.doSomething(File.CONFIG,
							Action.RELOAD,
							null,
							null,
							null);
						configurationHandler.doSomething(File.CUSTOM,
							Action.RELOAD,
							null,
							null,
							"messages.yml");
						
						sender.sendMessage(TextUtils.colorize(configurationHandler.text(File.CUSTOM,
							"messages.reload-all",
							"messages.yml")
							.replace("<prefix>", prefix)));
						break;
					}
					
					switch (args[1]) {
						default:
							sender.sendMessage(TextUtils.colorize(configurationHandler.text(File.CUSTOM,
								"messages.no-file",
								"messages.yml")
								.replace("<prefix>", prefix)));
							break;
						case "config":
							scoreboardHandler.tasks().clear();
							scoreboardHandler.scoreboards().clear();
							
							configurationHandler.doSomething(File.CONFIG,
								Action.RELOAD,
								null,
								null,
								null);
							
							sender.sendMessage(TextUtils.colorize(configurationHandler.text(File.CUSTOM,
								"messages.reload-config",
								"messages.yml")
								.replace("<prefix>", prefix)));
							break;
						case "messages":
							configurationHandler.doSomething(File.CUSTOM,
								Action.RELOAD,
								null,
								null,
								"messages.yml");
							
							sender.sendMessage(TextUtils.colorize(configurationHandler.text(File.CUSTOM,
								"messages.reload-messages",
								"messages.yml")
								.replace("<prefix>", prefix)));
							break;
					}
					break;
			}
			return false;
		}
		
		final Player player = (Player) sender;
		
		if (args.length == 0) {
			sender.sendMessage(TextUtils.colorize(prefix + "&f Running with &8[&b" + Bukkit.getVersion() + "&8]"));
			sender.sendMessage(TextUtils.colorize(prefix + "&f Developed by &aInitSync&f. Using latest version: " + version));
			return false;
		}
		
		switch (args[0]) {
			default:
				player.sendMessage(TextUtils.colorize(configurationHandler.text(File.CUSTOM,
					"messages.no-command",
					"messages.yml")
					.replace("<prefix>", prefix)));
				break;
			case "help":
				if (player.hasPermission(Permission.HELP_CMD.getPerm())) {
					configurationHandler.textList(File.CUSTOM,
						"messages.help",
						"messages.yml")
						.forEach(msg -> sender.sendMessage(TextUtils.colorize(msg.replace("<release>", version))));
				} else {
					player.playSound(player.getLocation(),
						XSound.matchXSound(configurationHandler.text(File.CONFIG,
							"config.sounds.no-perm",
							null)).get().parseSound(),
						configurationHandler.number(File.CONFIG,
							"config.sounds.volume-level",
							null),
						configurationHandler.number(File.CONFIG,
							"config.sounds.volume-level",
							null));
					player.sendMessage(TextUtils.colorize(configurationHandler.text(File.CUSTOM,
						"messages.no-perm",
						"messages.yml")
						.replace("<prefix>", prefix)));
				}
				break;
			case "reload":
				if (player.hasPermission(Permission.RELOAD_CMD.getPerm())) {
					if (args.length == 1) {
						scoreboardHandler.tasks().clear();
						scoreboardHandler.scoreboards().clear();
						
						configurationHandler.doSomething(File.CONFIG,
							Action.RELOAD,
							null,
							null,
							null);
						configurationHandler.doSomething(File.CUSTOM,
							Action.RELOAD,
							null,
							null,
							"messages.yml");
						
						sender.sendMessage(TextUtils.colorize(configurationHandler.text(File.CUSTOM,
							"messages.reload-all",
							"messages.yml")
							.replace("<prefix>", prefix)));
						break;
					}
					
					switch (args[1]) {
						default:
							sender.sendMessage(TextUtils.colorize(configurationHandler.text(File.CUSTOM,
								"messages.no-file",
								"messages.yml")
								.replace("<prefix>", prefix)));
							break;
						case "config":
							scoreboardHandler.tasks().clear();
							scoreboardHandler.scoreboards().clear();
							
							configurationHandler.doSomething(File.CONFIG,
								Action.RELOAD,
								null,
								null,
								null);
							
							sender.sendMessage(TextUtils.colorize(configurationHandler.text(File.CUSTOM,
								"messages.reload-config",
								"messages.yml")
								.replace("<prefix>", prefix)));
							break;
						case "messages":
							configurationHandler.doSomething(File.CUSTOM,
								Action.RELOAD,
								null,
								null,
								"messages.yml");
							
							sender.sendMessage(TextUtils.colorize(configurationHandler.text(File.CUSTOM,
								"messages.reload-messages",
								"messages.yml")
								.replace("<prefix>", prefix)));
							break;
					}
				} else {
					player.playSound(player.getLocation(),
						XSound.matchXSound(configurationHandler.text(File.CONFIG,
							"config.sounds.no-perm",
							null)).get().parseSound(),
						configurationHandler.number(File.CONFIG,
							"config.sounds.volume-level",
							null),
						configurationHandler.number(File.CONFIG,
							"config.sounds.volume-level",
							null));
					player.sendMessage(TextUtils.colorize(configurationHandler.text(File.CUSTOM,
						"messages.no-perm",
						"messages.yml")
						.replace("<prefix>", prefix)));
				}
				break;
		}
		return false;
	}
}
