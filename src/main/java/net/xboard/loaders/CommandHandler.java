package net.xboard.loaders;

import org.apache.commons.lang.Validate;
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
			Validate.notEmpty(commandName, "The command name is empty.");
			this.commandName = commandName;
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
		
		public Builder register() {
			if (commandName == null) {
				throw new NullPointerException("The command name is empty and can't be used.");
			}
			
			if (executor == null) {
				throw new NullPointerException("The executor is null and can't be used.");
			}
			
			final PluginCommand command = plugin.getCommand(commandName);
			assert command != null;
			command.setExecutor(executor);
			
			if (completer != null) {
				command.setTabCompleter(completer);
				completer = null;
			}
			
			commandName = null;
			executor = null;
			return this;
		}
	}
}
