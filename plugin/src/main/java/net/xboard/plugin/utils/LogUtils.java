package net.xboard.plugin.utils;

import net.xboard.plugin.XBoard;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class LogUtils {
	private static final Logger LOGGER = XBoard.getPlugin().getLogger();
	
	private LogUtils() {}
	
	public static void info(String... logs) {
		for (String logMessage : logs) {
			LOGGER.log(Level.INFO, logMessage);
		}
	}
	
	public static void warn(String... logs) {
		for (String logMessage : logs) {
			LOGGER.log(Level.WARNING, logMessage);
		}
	}
	
	public static void error(String... logs) {
		for (String logMessage : logs) {
			LOGGER.log(Level.SEVERE, logMessage);
		}
	}
}
