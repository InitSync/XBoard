package net.xboard.commands;

import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.messages.Titles;
import fr.mrmicky.fastboard.FastBoard;
import net.xboard.enums.Permission;
import net.xboard.scoreboard.ScoreboardHandler;
import net.xboard.utils.PlaceholderUtils;
import net.xconfig.bukkit.config.BukkitConfigurationHandler;
import net.xconfig.bukkit.utils.TextUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public final class ScoreboardCommand implements CommandExecutor {
	private final BukkitConfigurationHandler configurationHandler;
	private final ScoreboardHandler scoreboardHandler;
	
	public ScoreboardCommand(BukkitConfigurationHandler configurationHandler, ScoreboardHandler scoreboardHandler) {
		this.configurationHandler = Objects.requireNonNull(configurationHandler, "The BukkitConfigurationHandler object is null.");
		this.scoreboardHandler = Objects.requireNonNull(scoreboardHandler, "The ScoreboardHandler object is null.");
	}
	
	@Override
	public boolean onCommand(
		CommandSender sender,
		Command command,
		String label,
		String[] args
	) {
		final String prefix = configurationHandler.text("config.yml", "config.prefix");
		
		if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender)) return false;
		
		if (sender instanceof ConsoleCommandSender) {
			sender.sendMessage(TextUtils.colorize(
				configurationHandler.text("messages.yml", "messages.no-console")
					.replace("<prefix>", prefix)));
			return false;
		}
		
		final Player player = (Player) sender;
		
		if (player.hasPermission(Permission.SCOREBOARD_CMD.getPerm())) {
			if (args.length == 0) {
				player.sendMessage(TextUtils.colorize(
					configurationHandler.text("messages.yml", "messages.scoreboard-usage")
						.replace("<prefix>", prefix)));
				return false;
			}
			
			switch (args[0]) {
				default:
					player.sendMessage(TextUtils.colorize(
						configurationHandler.text("messages.yml", "messages.no-command")
							.replace("<prefix>", prefix)));
					break;
				case "toggle":
					if (scoreboardHandler.toggle(player)) {
						player.playSound(player.getLocation(),
							XSound.matchXSound(configurationHandler.text("config.yml", "config.sounds.scoreboard"))
								.get()
								.parseSound(),
							configurationHandler.number("config.yml", "config.sounds.volume-level"),
							configurationHandler.number("config.yml", "config.sounds.volume-level"));
						player.sendMessage(TextUtils.colorize(
							configurationHandler.text("messages.yml", "messages.scoreboard-on")
								.replace("<prefix>", prefix)));
						
						Titles.sendTitle(player,
							configurationHandler.number("config.yml", "config.titles.fade-in"),
							configurationHandler.number("config.yml", "config.titles.stay"),
							configurationHandler.number("config.yml", "config.titles.fade-out"),
							PlaceholderUtils.parse(player, configurationHandler.text("messages.yml", "messages.scoreboard-title")
								.replace("<status>", configurationHandler.text("messages.yml", "messages.enabled"))),
							PlaceholderUtils.parse(player, configurationHandler.text("messages.yml", "messages.scoreboard-subtitle")
								.replace("<status>", configurationHandler.text("messages.yml", "messages.enabled"))));
					} else {
						player.playSound(
							player.getLocation(),
							XSound.matchXSound(configurationHandler.text("config.yml", "config.sounds.scoreboard"))
								.get()
								.parseSound(),
							configurationHandler.number("config.yml", "config.sounds.volume-level"),
							configurationHandler.number("config.yml", "config.sounds.volume-level"));
						player.sendMessage(TextUtils.colorize(
							configurationHandler.text("messages.yml", "messages.scoreboard-off")
								.replace("<prefix>", prefix)));
						
						Titles.sendTitle(player,
							configurationHandler.number("config.yml", "config.titles.fade-in"),
							configurationHandler.number("config.yml", "config.titles.stay"),
							configurationHandler.number("config.yml", "config.titles.fade-out"),
							PlaceholderUtils.parse(player, configurationHandler.text("messages.yml", "messages.scoreboard-title")
								.replace("<status>", configurationHandler.text("messages.yml", "messages.disabled"))),
							PlaceholderUtils.parse(player, configurationHandler.text("messages.yml", "messages.scoreboard-subtitle")
								.replace("<status>", configurationHandler.text("messages.yml", "messages.disabled")))
						);
					}
					break;
				case "title":
					if (configurationHandler.condition("config.yml", "config.scoreboard.allow-animated-title")) {
						player.sendMessage(TextUtils.colorize(
							configurationHandler.text("messages.yml", "messages.scoreboard-title-error")
								.replace("<prefix>", prefix)));
						return false;
					}
					
					if (args.length == 1) {
						player.sendMessage(TextUtils.colorize(
							configurationHandler.text("messages.yml", "messages.scoreboard-title-usage")
								.replace("<prefix>", prefix)));
						return false;
					}
					
					final FastBoard playerScoreboard = scoreboardHandler.getByUuid(player.getUniqueId());
					if (playerScoreboard == null) return false;
					
					final String newTitle = PlaceholderUtils.parse(player, args[1]);
					playerScoreboard.updateTitle(newTitle);
					
					player.sendMessage(TextUtils.colorize(
						configurationHandler.text("messages.yml", "messages.scoreboard-title-changed")
							.replace("<prefix>", prefix)
							.replace("<new_title>", newTitle)));
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
		return false;
	}
}
