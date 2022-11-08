package net.xboard.utils;

import net.xboard.XBoard;

import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Class for send logs easy and rapidly.
 *
 * @author InitSync
 * @version 1.0.0
 * @since 1.0.0
 */
public final class LogPrinter {
	private static final Logger LOGGER = XBoard.instance().getLogger();
	
	private LogPrinter() {}
	
	public static void info(String... logs) {
		Arrays.asList(logs).forEach(LOGGER::info);
	}
	
	public static void warn(String... logs) {
		Arrays.asList(logs).forEach(LOGGER::warning);
	}
	
	public static void error(String... logs) {
		Arrays.asList(logs).forEach(LOGGER::severe);
	}
}
