package net.kigawa.oyu.scoreboardstore.config

import net.kigawa.oyu.scoreboardstore.util.config.ConfigObject
import net.kigawa.oyu.scoreboardstore.util.config.annotation.ConfigValue

class GuiConfig : ConfigObject{
  @ConfigValue
  var name = "guiName"
  @ConfigValue
  var items = mutableListOf(GuiItem())
}