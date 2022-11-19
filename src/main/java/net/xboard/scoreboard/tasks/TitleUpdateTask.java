package net.xboard.scoreboard.tasks;

import fr.mrmicky.fastboard.FastBoard;
import net.xboard.XBoard;
import net.xboard.utils.PlaceholderUtils;
import net.xconfig.bukkit.config.BukkitConfigurationHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;

/**
 * Task for the animated-title feature.
 *
 * @author InitSync
 * @version 1.0.1
 * @since 1.0.1
 */
public final class TitleUpdateTask extends BukkitRunnable {
	private final FastBoard board;
	private final List<String> lines;
	
	private int rate;
	
	public TitleUpdateTask(BukkitConfigurationHandler configurationHandler, FastBoard board) {
		this.board = Objects.requireNonNull(board, "The FastBoard object is null.");
		this.lines = configurationHandler.textList("config.yml", "config.scoreboard.title.lines");
		this.rate = configurationHandler.number("config.yml", "config.scoreboard.title.update-rate");
		
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
		
		board.updateTitle(PlaceholderUtils.parse(board.getPlayer(), lines.get(rate)
			.replace("<release>", XBoard.instance().release)));
	}
}
