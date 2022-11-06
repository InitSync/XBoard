package net.xboard.commands.completers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * TabCompleter for the '/scoreboard' command.
 *
 * @author InitSync
 * @version 1.0.0
 * @since 1.0.0
 * @see org.bukkit.command.TabCompleter
 */
public final class ScoreboardCompleter implements TabCompleter {
	private final List<String> scoreboardArgs;
	
	public ScoreboardCompleter() {
		scoreboardArgs = new ArrayList<>();
	}
	
	@Override
	public @Nullable List<String> onTabComplete(
		@NotNull CommandSender sender,
		@NotNull Command command,
		@NotNull String label,
		@NotNull String[] args
	) {
		if (scoreboardArgs.isEmpty()) {
			scoreboardArgs.add("toggle");
			scoreboardArgs.add("title");
		}
		
		final List<String> results = new ArrayList<>();
		
		if (args.length == 1) {
			for (String result : scoreboardArgs) {
				if (result.toLowerCase().startsWith(args[0].toLowerCase())) results.add(result);
			}
			return results;
		}
		return null;
	}
}
