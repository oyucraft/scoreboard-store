package net.kigawa.oyu.scoreboardstore.config

import net.kigawa.kutil.unitapi.annotation.Kunit
import net.kigawa.oyu.scoreboardstore.util.config.ConfigParent
import net.kigawa.oyu.scoreboardstore.util.config.annotation.ConfigValue

@Kunit
class ScoreStoreConfig : ConfigParent() {
  @ConfigValue
  val db = DatabaseConfig()

  @ConfigValue
  val saveScoreboard = listOf<String>()

  @ConfigValue
  val cmd = CmdConfig()

  @ConfigValue
  val gui = mutableListOf(GuiConfig())
}