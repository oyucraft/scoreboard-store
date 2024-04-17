package net.kigawa.oyu.scoreboardstore.config

import net.kigawa.oyu.scoreboardstore.util.config.ConfigObject
import net.kigawa.oyu.scoreboardstore.util.config.annotation.ConfigValue

class DatabaseConfig : ConfigObject {
  @ConfigValue
  val host = "localhost"

  @ConfigValue
  val port = 3306

  @ConfigValue
  val user = "root"

  @ConfigValue
  val pass = "root"

  @ConfigValue
  val name = "score_store"
}