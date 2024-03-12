package net.kigawa.oyu.scoreboardstore.config

import net.kigawa.kutil.unitapi.annotation.Kunit
import net.kigawa.oyu.scoreboardstore.util.config.Config
import net.kigawa.oyu.scoreboardstore.util.config.annotation.ConfigValue

@Kunit
class ScoreboardStoreConfig : Config() {
  @ConfigValue
  val dbHost = "localhost"

  @ConfigValue
  val dbPort = 3306

  @ConfigValue
  val dbUser = "root"

  @ConfigValue
  val dbPass = "root"

  @ConfigValue
  val dbName = "score_store"

  @ConfigValue
  val onJoin = listOf<String>()

  @ConfigValue
  val onQuit = listOf<String>()

  @ConfigValue
  val gui = mutableListOf(GuiConfig())
}