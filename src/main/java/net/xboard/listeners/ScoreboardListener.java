package net.xboard.listeners;

import net.xboard.scoreboard.ScoreboardHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class ScoreboardListener implements Listener {
	private final ScoreboardHandler scoreboardHandler;
	
	public ScoreboardListener(@NotNull ScoreboardHandler scoreboardHandler) {
		this.scoreboardHandler = Objects.requireNonNull(scoreboardHandler, "The ScoreboardHandler object is null.");
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		// Creates the scoreboard.
		scoreboardHandler.create(event.getPlayer());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		// Removes the scoreboard.
		scoreboardHandler.remove(event.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		// Gets the player.
		final Player player = event.getPlayer();
		
		// Removes the scoreboard.
		scoreboardHandler.remove(player.getUniqueId());
		// Creates the scoreboard.
		scoreboardHandler.create(player);
	}
}
