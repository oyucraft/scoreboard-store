plugins {
  id("net.kigawa.java-conventions")
  id("application")
  id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
  @Suppress("VulnerableLibrariesLocal", "RedundantSuppression")
  api("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")
  implementation("net.kigawa.kutil:kutil-unit:4.4.0")
  implementation("dev.jorel:commandapi-bukkit-shade:9.3.0")
  implementation("com.zaxxer:HikariCP:5.0.1")
  // https://mvnrepository.com/artifact/org.yaml/snakeyaml
  implementation("org.yaml:snakeyaml:2.2")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
  implementation(kotlin("reflect"))
}
kotlin {

  tasks {
    shadowJar {
      archiveFileName.set("${project.parent?.name ?: project.name}.jar")
      project.setProperty("mainClassName", "")
    }
  }
}

