package net.xboard.plugin.loaders;

import com.google.common.base.Preconditions;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class CommandHandler {
	private CommandHandler() {}
	
	public static class Builder {
		private final JavaPlugin plugin;
		
		private String commandName;
		private CommandExecutor executor;
		private TabCompleter completer;
		
		public Builder(JavaPlugin plugin) {
			this.plugin = Objects.requireNonNull(plugin, "The JavaPlugin instance is null.");
		}
		
		public Builder command(String commandName) {
			this.commandName = Objects.requireNonNull(commandName, "The command name is null.");
			Preconditions.checkArgument(!commandName.isEmpty(), "The command name is empty.");
			return this;
		}
		
		public Builder executor(CommandExecutor executor) {
			this.executor = Objects.requireNonNull(executor, "The command executor is null.");
			return this;
		}
		
		public Builder completer(TabCompleter completer) {
			this.completer = Objects.requireNonNull(completer, "The tab completer is null.");
			return this;
		}
		
		public void build() {
			if (commandName == null) throw new IllegalArgumentException("The command name is empty and can't be used.");
			
			if (executor == null) throw new IllegalArgumentException("The executor is null and can't be used.");
			
			PluginCommand command = plugin.getCommand(commandName);
			if (command == null) return;
			
			command.setExecutor(executor);
			
			if (completer != null) command.setTabCompleter(completer);
		}
	}
}
