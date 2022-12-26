plugins {
	id("com.github.johnrengelman.shadow") version("7.1.2")
	`java-library`
	`maven-publish`
}

val release = property("version") as String

repositories {
	mavenLocal()
	maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
	maven("https://jitpack.io/")
	maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
	mavenCentral()
}

dependencies {
	compileOnly("org.spigotmc:spigot-api:1.19.2-R0.1-SNAPSHOT")

	implementation("fr.mrmicky:fastboard:1.2.1")
}

tasks {
	shadowJar {
		archiveFileName.set("XBoardAPI-$release.jar")
		destinationDirectory.set(file("$rootDir/bin/"))
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
			groupId = "net.xboard.api"
			artifactId = "api"
			version = release
			
			from(components["java"])
		}
	}
}