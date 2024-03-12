package net.kigawa.oyu.scoreboardstore.config

import net.kigawa.oyu.scoreboardstore.util.config.annotation.ConfigValue

class GuiConfig {
  @ConfigValue
  val name = "guiName"

  @ConfigValue
  val items = mutableListOf(GuiItem())
}