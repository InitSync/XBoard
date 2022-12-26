[![Codacy Badge](https://app.codacy.com/project/badge/Grade/f28b1294865c4c8f9d4c7d5864722f68)](https://www.codacy.com/gh/InitSync/XBoard/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=InitSync/XBoard&amp;utm_campaign=Badge_Grade)
[![](https://jitpack.io/v/InitSync/XBoard.svg)](https://jitpack.io/#InitSync/XBoard)

**XBoard** is a simple and easy to use Scoreboard plugin for your Minecraft server that allows you create personalized scoreboards for your users without loss performance.

# 🛠️ | Import
If you're using a dependency manager such as Maven or Gradle. Or just import the library to BuildPath of your project.

To get the jar, either download it from [GitHub](https://github.com/InitSync/XBoard/releases) or [Spigot](https://www.spigotmc.org/resources/%E2%9C%A8xboard-customizable-and-asynchronous-scoreboards%E2%9C%A8-%E2%9C%85-1-8-1-19-2.106173/). Or just [Build it locally](https://github.com/InitSync/XBoard#--build)

Gradle (Kotlin DSL)
```Gradle
repositories {
  maven("https://jitpack.io")
  mavenCentral()
}

dependencies {
  compileOnly("com.github.InitSync:XBoard:1.0.1")
}
```

Maven
```Xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io/</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.github.InitSync</groupId>
    <artifactId>XBoard</artifactId>
    <version>1.0.1</version>
  </dependency>
</dependencies>
```

# ➕ | Contribute
Do you want contribute with the library?

* [Make a Pull Request](https://github.com/InitSync/XBoard/compare)
* [Issues](https://github.com/InitSync/XBoard/issues/new)

# ✅ | Build
If you want build the project locally, download it, you must be had Gradle and Java 8+ for this.

Now for build the project
```
git clone https://github.com/InitSync/XBoard
cd XBoard
./gradlew shadowJar
```

The file will be at ```bin/XBoard-[release].jar```.

# 🎫 | License
This project is licensed under the GNU General Public License v3.0 license, for more details see the file [License](LICENSE)
