package net.xboard.plugin.commands;

import net.xboard.api.handlers.ScoreboardHandler;
import net.xboard.api.scoreboard.SimpleBoard;
import net.xboard.api.utils.XSound;
import net.xboard.plugin.enums.Permission;
import net.xboard.plugin.utils.PlaceholderUtils;
import net.xconfig.bukkit.config.BukkitConfigurationHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public final class ScoreboardCommand
implements CommandExecutor {
	private final BukkitConfigurationHandler configurationHandler;
	private final ScoreboardHandler scoreboardHandler;
	
	public ScoreboardCommand(BukkitConfigurationHandler configurationHandler, ScoreboardHandler scoreboardHandler) {
		this.configurationHandler = Objects.requireNonNull(configurationHandler, "The BukkitConfigurationHandler object is null.");
		this.scoreboardHandler = Objects.requireNonNull(scoreboardHandler, "The ScoreboardHandler object is null.");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String prefix = configurationHandler.text("", "config.yml", "config.prefix", false);
		
		if (!(sender instanceof Player)) {
			sender.sendMessage(configurationHandler.text("", "messages.yml", "messages.no-console", true).replace("<prefix>", prefix));
			return false;
		}
		
		Player player = (Player) sender;
		
		if (player.hasPermission(Permission.SCOREBOARD_CMD.getPerm())) {
			if (args.length == 0) {
				player.sendMessage(configurationHandler.text("", "messages.yml", "messages.scoreboard-usage", true).replace("<prefix>", prefix));
				return false;
			}
			
			switch (args[0]) {
				default:
					player.sendMessage(configurationHandler.text("", "messages.yml", "messages.no-command", true).replace("<prefix>", prefix));
					break;
				case "toggle":
					if (scoreboardHandler.getScoreboard(player.getUniqueId()).isDeleted()) {
						player.playSound(
							 player.getLocation(),
							 XSound.matchXSound(configurationHandler.text("", "config.yml", "config.sounds.scoreboard", false)).get().parseSound(),
							 configurationHandler.number("", "config.yml", "config.sounds.volume-level"),
							 configurationHandler.number("", "config.yml", "config.sounds.volume-level")
						);
						player.sendMessage(configurationHandler.text("", "messages.yml", "messages.scoreboard-on", true).replace("<prefix>", prefix));
						return false;
					}
					
					player.playSound(
						 player.getLocation(),
						 XSound.matchXSound(configurationHandler.text("", "config.yml", "config.sounds.scoreboard", false)).get().parseSound(),
						 configurationHandler.number("", "config.yml", "config.sounds.volume-level"),
						 configurationHandler.number("", "config.yml", "config.sounds.volume-level")
					);
					player.sendMessage(configurationHandler.text("", "messages.yml", "messages.scoreboard-off", true).replace("<prefix>", prefix));
					break;
				case "title":
					if (configurationHandler.condition("", "config.yml", "config.scoreboard.allow-animated-title")) {
						player.sendMessage(configurationHandler.text("", "messages.yml", "messages.scoreboard-title-error", true).replace("<prefix>", prefix));
						return false;
					}
					
					if (args.length == 1) {
						player.sendMessage(configurationHandler.text("", "messages.yml", "messages.scoreboard-title-usage", true).replace("<prefix>", prefix));
						return false;
					}
					
					SimpleBoard board = scoreboardHandler.getScoreboard(player.getUniqueId());
					if (board == null) return false;
					
					String newTitle = PlaceholderUtils.parse(player, args[1]);
					board.updateTitle(newTitle);
					
					player.sendMessage(configurationHandler.text("", "messages.yml", "messages.scoreboard-title-changed", true)
						 .replace("<prefix>", prefix)
						 .replace("<new_title>", newTitle)
					);
					break;
			}
		} else {
			player.playSound(
				 player.getLocation(),
				 XSound.matchXSound(configurationHandler.text("", "config.yml", "config.sounds.no-perm", false)).get().parseSound(),
				 configurationHandler.number("", "config.yml", "config.sounds.volume-level"),
				 configurationHandler.number("", "config.yml", "config.sounds.volume-level")
			);
			player.sendMessage(configurationHandler.text("", "messages.yml", "messages.no-perm", true).replace("<prefix>", prefix));
		}
		return false;
	}
}
