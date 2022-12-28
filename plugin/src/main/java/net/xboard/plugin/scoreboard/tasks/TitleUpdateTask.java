package net.xboard.plugin.scoreboard.tasks;

import net.xboard.api.scoreboard.SimpleBoard;
import net.xboard.plugin.utils.PlaceholderUtils;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;

public final class TitleUpdateTask
extends BukkitRunnable {
	private final SimpleBoard board;
	private final List<String> lines;
	
	private int rate;
	
	public TitleUpdateTask(SimpleBoard board, List<String> lines, int rate) {
		this.board = Objects.requireNonNull(board, "The FastBoard object is null.");
		this.lines = Objects.requireNonNull(lines, "The lines list is null.");
		this.rate = rate;
		
		if (rate > lines.size()) {
			throw new IndexOutOfBoundsException("The update-rate at the title is major than the size of the title list.\n"
				+ "Set the update-rate minor than the list size or increment the list size.");
		}
	}
	
	@Override
	public void run() {
		if (rate == lines.size() - 1) rate = 0;
		rate++;
		
		if (board.isDeleted()) return;
		
		board.updateTitle(PlaceholderUtils.parse(board.getPlayer(), lines.get(rate)));
	}
}
