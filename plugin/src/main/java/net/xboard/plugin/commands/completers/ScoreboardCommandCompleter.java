package net.xboard.plugin.commands.completers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public final class ScoreboardCommandCompleter implements TabCompleter {
	private final List<String> scoreboardArgs;
	
	public ScoreboardCommandCompleter() {
		scoreboardArgs = new ArrayList<>();
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (scoreboardArgs.isEmpty()) {
			scoreboardArgs.add("toggle");
			scoreboardArgs.add("title");
		}
		
		List<String> results = new ArrayList<>();
		
		if (args.length == 1) {
			for (String result : scoreboardArgs) {
				if (result.toLowerCase().startsWith(args[0].toLowerCase())) results.add(result);
			}
			return results;
		}
		
		return null;
	}
}
