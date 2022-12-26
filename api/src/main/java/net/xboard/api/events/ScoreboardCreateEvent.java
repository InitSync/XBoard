package net.xboard.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Objects;

public class ScoreboardCreateEvent
extends Event
implements Cancellable {
	private static final HandlerList HANDLER_LIST = new HandlerList();
	
	private final Player player;
	
	private boolean cancelled;
	
	public ScoreboardCreateEvent(Player player) {
		this.player = Objects.requireNonNull(player, "The player is null.");
	}
	
	public Player getPlayer() {
		return player;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}
	
	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}
}
