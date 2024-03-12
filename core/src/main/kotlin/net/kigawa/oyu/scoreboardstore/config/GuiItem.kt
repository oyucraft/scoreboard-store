package net.kigawa.oyu.scoreboardstore.config

import net.kigawa.oyu.scoreboardstore.util.config.annotation.ConfigValue

class GuiItem {
  @ConfigValue
  val id = ""

  @ConfigValue
  val cmd: String? = null

  @ConfigValue
  val column = 0

  @ConfigValue
  val row = 0
}