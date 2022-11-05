package net.xboard.commands;

import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.messages.Titles;
import fr.mrmicky.fastboard.FastBoard;
import net.xboard.enums.Permission;
import net.xboard.scoreboard.ScoreboardHandler;
import net.xboard.utils.TextUtils;
import net.xconfig.bukkit.config.BukkitConfigurationHandler;
import net.xconfig.enums.File;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class ScoreboardCommand implements CommandExecutor {
	private final BukkitConfigurationHandler configurationHandler;
	private final ScoreboardHandler scoreboardHandler;
	
	public ScoreboardCommand(
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
		
		if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender)) return false;
		
		if (sender instanceof ConsoleCommandSender) {
			sender.sendMessage(TextUtils.colorize(configurationHandler.text(File.CUSTOM,
				"messages.no-console",
				"messages.yml")
				.replace("<prefix>", prefix)));
			return false;
		}
		
		final Player player = (Player) sender;
		
		if (player.hasPermission(Permission.SCOREBOARD_CMD.getPerm())) {
			if (args.length == 0) {
				player.sendMessage(TextUtils.colorize(configurationHandler.text(File.CUSTOM,
					"messages.scoreboard-usage",
					"messages.yml")
					.replace("<prefix>", prefix)));
				return false;
			}
			
			switch (args[0]) {
				default:
					player.sendMessage(TextUtils.colorize(configurationHandler.text(File.CUSTOM,
						"messages.no-command",
						"messages.yml")
						.replace("<prefix>", prefix)));
					break;
				case "toggle":
					if (scoreboardHandler.toggle(player)) {
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
							"messages.scoreboard-on",
							"messages.yml")
							.replace("<prefix>", prefix)));
						
						Titles.sendTitle(player,
							configurationHandler.number(File.CONFIG,
								"config.titles.fade-in",
								null),
							configurationHandler.number(File.CONFIG,
								"config.titles.stay",
								null),
							configurationHandler.number(File.CONFIG,
								"config.titles.fade-out",
								null),
							TextUtils.parse(player, configurationHandler.text(File.CUSTOM,
									"messages.scoreboard-title",
									"messages.yml")
								.replace("<status>", configurationHandler.text(File.CUSTOM,
									"messages.disabled",
									"messages.yml"))),
							TextUtils.parse(player, configurationHandler.text(File.CUSTOM,
									"messages.scoreboard-subtitle",
									"messages.yml")
								.replace("<status>", configurationHandler.text(File.CUSTOM,
									"messages.disabled",
									"messages.yml"))));
					} else {
						player.playSound(player.getLocation(),
							XSound.matchXSound(configurationHandler.text(File.CONFIG,
								"config.sounds.scoreboard",
								null)).get().parseSound(),
							configurationHandler.number(File.CONFIG,
								"config.sounds.volume-level",
								null),
							configurationHandler.number(File.CONFIG,
								"config.sounds.volume-level",
								null));
						player.sendMessage(TextUtils.colorize(configurationHandler.text(File.CUSTOM,
							"messages.scoreboard-off",
							"messages.yml")
							.replace("<prefix>", prefix)));
						
						Titles.sendTitle(player,
							configurationHandler.number(File.CONFIG,
								"config.titles.fade-in",
								null),
							configurationHandler.number(File.CONFIG,
								"config.titles.stay",
								null),
							configurationHandler.number(File.CONFIG,
								"config.titles.fade-out",
								null),
							TextUtils.parse(player, configurationHandler.text(File.CUSTOM,
								"messages.scoreboard-title",
								"messages.yml")
								.replace("<status>", configurationHandler.text(File.CUSTOM,
									"messages.enabled",
									"messages.yml"))),
							TextUtils.parse(player, configurationHandler.text(File.CUSTOM,
									"messages.scoreboard-subtitle",
									"messages.yml")
								.replace("<status>", configurationHandler.text(File.CUSTOM,
									"messages.enabled",
									"messages.yml"))));
					}
					break;
				case "title":
					if (args.length == 1) {
						player.sendMessage(TextUtils.colorize(configurationHandler.text(File.CUSTOM,
								"messages.scoreboard-title-usage",
								"messages.yml")
							.replace("<prefix>", prefix)));
						return false;
					}
					
					final FastBoard playerScoreboard = scoreboardHandler.getByUuid(player.getUniqueId());
					if (playerScoreboard == null) return false;
					
					final String newTitle = TextUtils.parse(player, args[1]);
					playerScoreboard.updateTitle(newTitle);
					
					player.sendMessage(TextUtils.colorize(configurationHandler.text(File.CUSTOM,
							"messages.scoreboard-title-changed",
							"messages.yml")
						.replace("<prefix>", prefix)
						.replace("<new_title>", newTitle)));
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
		return false;
	}
}
