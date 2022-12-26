package net.xboard.plugin.commands.completers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public final class MainCommandCompleter
implements TabCompleter {
	private final List<String> commandArgs;
	private final List<String> reloadArgs;
	
	public MainCommandCompleter() {
		commandArgs = new ArrayList<>();
		reloadArgs = new ArrayList<>();
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (commandArgs.isEmpty()) {
			commandArgs.add("help");
			commandArgs.add("reload");
		}
		
		if (reloadArgs.isEmpty()) {
			reloadArgs.add("config");
			reloadArgs.add("messages");
		}
		
		List<String> results = new ArrayList<>();
		
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
