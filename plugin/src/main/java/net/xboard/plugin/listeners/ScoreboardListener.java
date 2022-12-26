package net.xboard.plugin.listeners;

import net.xboard.api.handlers.ScoreboardHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;

public final class ScoreboardListener
implements Listener {
	private final ScoreboardHandler scoreboardHandler;
	
	public ScoreboardListener(ScoreboardHandler scoreboardHandler) {
		this.scoreboardHandler = Objects.requireNonNull(scoreboardHandler, "The ScoreboardHandler object is null.");
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		scoreboardHandler.create(event.getPlayer());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		scoreboardHandler.remove(event.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		
		scoreboardHandler.remove(player.getUniqueId());
		scoreboardHandler.create(player);
	}
}
