package net.xboard.commands.completers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * TabCompleter for the '/xboard' command.
 *
 * @author InitSync
 * @version 1.0.0
 * @since 1.0.0
 * @see org.bukkit.command.TabCompleter
 */
public final class MainCompleter implements TabCompleter {
	private final List<String> commandArgs;
	private final List<String> reloadArgs;
	
	public MainCompleter() {
		commandArgs = new ArrayList<>();
		reloadArgs = new ArrayList<>();
	}
	
	@Override
	public @Nullable List<String> onTabComplete(
		@NotNull CommandSender sender,
		@NotNull Command command,
		@NotNull String label,
		@NotNull String[] args
	) {
		if (commandArgs.isEmpty()) {
			commandArgs.add("help");
			commandArgs.add("reload");
		}
		
		if (reloadArgs.isEmpty()) {
			reloadArgs.add("config");
			reloadArgs.add("messages");
		}
		
		final List<String> results = new ArrayList<>();
		
		if (args.length == 1) {
			for (String result : commandArgs) {
				if (result.toLowerCase().startsWith(args[0].toLowerCase())) results.add(result);
			}
			return results;
		}
		
		if (args[0].equals("reload") && args.length == 2) {
			for (String result : reloadArgs) {
				if (result.toLowerCase().startsWith(args[1].toLowerCase())) results.add(result);
			}
			return results;
		}
		return null;
	}
}
