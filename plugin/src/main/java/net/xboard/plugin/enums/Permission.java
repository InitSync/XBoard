package net.xboard.plugin.enums;

public enum Permission {
	HELP_CMD ("xboard.help"),
	RELOAD_CMD ("xboard.reload"),
	SCOREBOARD_CMD ("xboard.scoreboard");
	
	private final String perm;
	
	Permission(String perm) {
		this.perm = perm;
	}
	
	public String getPerm() {
		return perm;
	}
}
