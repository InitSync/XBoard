package net.xboard.plugin.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public final class UpdateHandler {
	private final int resourceNumber;
	
	public UpdateHandler(int resourceNumber) {
		this.resourceNumber = resourceNumber;
	}
	
	public void version(Consumer<String> version) {
		try (InputStream stream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceNumber).openStream()) {
			Scanner scanner = new Scanner(stream);
			if (scanner.hasNext()) version.accept(scanner.next());
		} catch (IOException exception) {
			LogUtils.error("Unable to check for updates available.");
			exception.printStackTrace();
		}
	}
}
