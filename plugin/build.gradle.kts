plugins {
	id("com.github.johnrengelman.shadow") version("7.1.2")
	id("net.minecrell.plugin-yml.bukkit") version("0.5.2")
	`java-library`
}

val directory = property("group") as String
val release = property("version") as String
val description = "A simple and easy to use Scoreboard plugin for your Minecraft server."

repositories {
	mavenLocal()
	maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
	maven("https://jitpack.io/")
	maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
	mavenCentral()
}

dependencies {
	compileOnly("org.spigotmc:spigot-api:1.19.2-R0.1-SNAPSHOT")
	compileOnly("me.clip:placeholderapi:2.11.2")
	
	implementation(project(":api"))
	
	implementation("com.github.InitSync.XConfig:bukkit:1.1.3")
	implementation("com.github.cryptomorin:XSeries:9.2.0")
	implementation("com.github.InitSync:XTitle:1.0.1")
}

bukkit {
	name = "XBoard"
	main = "$group.plugin.XBoard"
	authors = listOf("InitSync")
	
	version = release
	apiVersion = "1.13"
	
	softDepend = listOf("PlaceholderAPI")
	
	permissions {
		register("xboard.*") {
			children = listOf(
				 "xboard.help",
				 "xboard.reload",
				 "xboard.toggle"
			)
		}
		register("xboard.help")
		register("xboard.reload")
		register("xboard.toggle")
	}
	
	commands {
		register("xboard") {
			description = "-> Command to handle the plugin."
			aliases = listOf("xb")
		}
		register("scoreboard") {
			description = "-> Command for toggle the Scoreboard visibility."
			aliases = listOf("sb")
		}
	}
}

tasks {
	shadowJar {
		archiveFileName.set("XBoard-$release.jar")
		destinationDirectory.set(file("$rootDir/bin/"))
		minimize()

		relocate("net.xconfig.bukkit", "net.xboard.libraries.xconfig")
		relocate("com.cryptomorin.xseries", "net.xboard.libraries.xseries")
	}
	
	withType<JavaCompile> {
		options.encoding = "UTF-8"
	}
	
	clean {
		delete("$rootDir/bin/")
	}
}