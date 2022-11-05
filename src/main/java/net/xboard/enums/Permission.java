package net.xboard.enums;

import org.jetbrains.annotations.NotNull;

public enum Permission {
	HELP_CMD ("xboard.help"),
	RELOAD_CMD ("xboard.reload"),
	SCOREBOARD_CMD ("xboard.scoreboard");
	
	private final String perm;
	
	Permission(String perm) {
		this.perm = perm;
	}
	
	public @NotNull String getPerm() {
		return perm;
	}
}
