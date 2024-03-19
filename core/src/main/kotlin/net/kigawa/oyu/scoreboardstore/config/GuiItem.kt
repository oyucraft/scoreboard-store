package net.kigawa.oyu.scoreboardstore.config

import net.kigawa.oyu.scoreboardstore.util.config.ConfigObject
import net.kigawa.oyu.scoreboardstore.util.config.annotation.ConfigValue

class GuiItem : ConfigObject {
  @ConfigValue
  var id = ""

  @ConfigValue
  var cmd: String? = null

  @ConfigValue
  var column = 0

  @ConfigValue
  var row = 0
}