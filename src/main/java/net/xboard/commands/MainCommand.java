package net.xboard.commands;

import com.cryptomorin.xseries.XSound;
import net.xboard.XBoard;
import net.xboard.enums.Permission;
import net.xboard.scoreboard.ScoreboardHandler;
import net.xboard.utils.TextUtils;
import net.xconfig.bukkit.config.BukkitConfigurationHandler;
import net.xconfig.enums.Action;
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
		final String prefix = configurationHandler.text("config.yml", "config.prefix");
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
					sender.sendMessage(TextUtils.colorize(
						configurationHandler.text("messages.yml", "messages.no-command")
							.replace("<prefix>", prefix)));
					break;
				case "help":
					configurationHandler.textList("messages.yml", "messages.help")
						.forEach(msg -> sender.sendMessage(TextUtils.colorize(msg.replace("<release>", version))));
					break;
				case "reload":
					if (args.length == 1) {
						scoreboardHandler.tasks().clear();
						scoreboardHandler.scoreboards().clear();
						
						configurationHandler.doSomething("config.yml",
							Action.RELOAD,
							null,
							null);
						configurationHandler.doSomething("messages.yml",
							Action.RELOAD,
							null,
							null);
						
						Bukkit.getOnlinePlayers().forEach(scoreboardHandler::create);
						
						sender.sendMessage(TextUtils.colorize(
							configurationHandler.text("messages.yml", "messages.reload-all")
								.replace("<prefix>", prefix)));
						break;
					}
					
					switch (args[1]) {
						default:
							sender.sendMessage(TextUtils.colorize(
								configurationHandler.text("messages.yml", "messages.no-file")
									.replace("<prefix>", prefix)));
							break;
						case "config":
							scoreboardHandler.tasks().clear();
							scoreboardHandler.scoreboards().clear();
							
							configurationHandler.doSomething("config.yml",
								Action.RELOAD,
								null,
								null);
							
							Bukkit.getOnlinePlayers().forEach(scoreboardHandler::create);
							
							sender.sendMessage(TextUtils.colorize(
								configurationHandler.text("messages.yml", "messages.reload-config")
									.replace("<prefix>", prefix)));
							break;
						case "messages":
							configurationHandler.doSomething("messages.yml",
								Action.RELOAD,
								null,
								null);
							
							sender.sendMessage(TextUtils.colorize(
								configurationHandler.text("messages.yml", "messages.reload-messages")
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
				player.sendMessage(TextUtils.colorize(
					configurationHandler.text("messages.yml", "messages.no-command")
						.replace("<prefix>", prefix)));
				break;
			case "help":
				if (player.hasPermission(Permission.HELP_CMD.getPerm())) {
					configurationHandler.textList("messages.yml", "messages.help")
						.forEach(msg -> sender.sendMessage(TextUtils.colorize(msg.replace("<release>", version))));
				} else {
					player.playSound(player.getLocation(),
						XSound.matchXSound(configurationHandler.text("config.yml", "config.sounds.no-perm"))
							.get()
							.parseSound(),
						configurationHandler.number("config.yml", "config.sounds.volume-level"),
						configurationHandler.number("config.yml", "config.sounds.volume-level"));
					player.sendMessage(TextUtils.colorize(
						configurationHandler.text("messages.yml", "messages.no-perm")
							.replace("<prefix>", prefix)));
				}
				break;
			case "reload":
				if (player.hasPermission(Permission.RELOAD_CMD.getPerm())) {
					if (args.length == 1) {
						scoreboardHandler.tasks().clear();
						scoreboardHandler.scoreboards().clear();
						
						configurationHandler.doSomething("config.yml",
							Action.RELOAD,
							null,
							null);
						configurationHandler.doSomething("messages.yml",
							Action.RELOAD,
							null,
							null);
						
						Bukkit.getOnlinePlayers().forEach(scoreboardHandler::create);
						
						sender.sendMessage(TextUtils.colorize(
							configurationHandler.text("messages.yml", "messages.reload-all")
								.replace("<prefix>", prefix)));
						break;
					}
					
					switch (args[1]) {
						default:
							sender.sendMessage(TextUtils.colorize(
								configurationHandler.text("messages.yml", "messages.no-file")
									.replace("<prefix>", prefix)));
							break;
						case "config":
							scoreboardHandler.tasks().clear();
							scoreboardHandler.scoreboards().clear();
							
							configurationHandler.doSomething("config.yml",
								Action.RELOAD,
								null,
								null);
							
							Bukkit.getOnlinePlayers().forEach(scoreboardHandler::create);
							
							sender.sendMessage(TextUtils.colorize(
								configurationHandler.text("messages.yml", "messages.reload-config")
									.replace("<prefix>", prefix)));
							break;
						case "messages":
							configurationHandler.doSomething("messages.yml",
								Action.RELOAD,
								null,
								null);
							
							sender.sendMessage(TextUtils.colorize(
								configurationHandler.text("messages.yml", "messages.reload-messages")
									.replace("<prefix>", prefix)));
							break;
					}
				} else {
					player.playSound(player.getLocation(),
						XSound.matchXSound(configurationHandler.text("config.yml", "config.sounds.no-perm"))
							.get()
							.parseSound(),
						configurationHandler.number("config.yml", "config.sounds.volume-level"),
						configurationHandler.number("config.yml", "config.sounds.volume-level"));
					player.sendMessage(TextUtils.colorize(
						configurationHandler.text("messages.yml", "messages.no-perm")
							.replace("<prefix>", prefix)));
				}
				break;
		}
		return false;
	}
}
