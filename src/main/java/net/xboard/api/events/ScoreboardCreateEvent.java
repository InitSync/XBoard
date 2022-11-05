package net.xboard.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Event for handle when the Scoreboard is created to player.
 *
 * @author InitSync
 * @version 1.0.0
 * @since 1.0.0
 * @see org.bukkit.event.Event
 * @see org.bukkit.event.Cancellable
 */
public final class ScoreboardCreateEvent extends Event implements Cancellable {
	private final HandlerList handlers;
	private final Player player;
	
	private boolean cancelled;
	
	public ScoreboardCreateEvent(@NotNull Player player) {
		handlers = new HandlerList();
		this.player = Objects.requireNonNull(player, "The player is null.");
	}
	
	/**
	 * Returns the Player object of this event.
	 *
	 * @return A Player object.
	 */
	public @NotNull Player player() {
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
	public @NotNull HandlerList getHandlers() {
		return handlers;
	}
}
