plugins {
	id("com.github.johnrengelman.shadow") version("7.1.2")
	id("net.minecrell.plugin-yml.bukkit") version("0.5.2")
	`java-library`
	`maven-publish`
}

val directory = property("group") as String
val release = property("version") as String
val libs = property("libs") as String
val description = "A simple and easy to use Scoreboard plugin for your Minecraft server."

repositories {
	maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
	maven("https://jitpack.io/")
	maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
	mavenCentral()
}

dependencies {
	compileOnly("org.spigotmc:spigot-api:1.19.2-R0.1-SNAPSHOT")
	compileOnly("net.md-5:bungeecord-chat:1.16-R0.4")
	
	compileOnly("me.clip:placeholderapi:2.11.2")
	implementation("fr.mrmicky:fastboard:1.2.1")
	implementation("com.github.InitSync:XConfig:1.0.4")
	implementation("com.github.cryptomorin:XSeries:9.1.0")
	
	implementation("commons-lang:commons-lang:2.6")
}

bukkit {
	name = "XBoard"
	main = "$group.XBoard"
	authors = listOf("InitSync")
	
	version = release
	apiVersion = "1.13"
	
	softDepend = listOf("PlaceholderAPI")
	
	permissions {
		register("xboard.*") {
			children = listOf("xboard.help",
				 "xboard.reload",
				 "xboard.toggle")
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
		
		relocate("org.apache.commons", "$libs.commons")
		relocate("fr.mrmicky.fastboard", "$libs.fastboard")
		relocate("net.xconfig", "$libs.xconfig")
		relocate("com.cryptomorin.xseries", "$libs.xseries")
	}
	
	withType<JavaCompile> {
		options.encoding = "UTF-8"
	}
	
	clean {
		delete("$rootDir/bin/")
	}
}

publishing {
	publications {
		create<MavenPublication>("maven") {
			groupId = "net.xboard"
			artifactId = "XBoard"
			version = release
			
			from(components["java"])
		}
	}
}
