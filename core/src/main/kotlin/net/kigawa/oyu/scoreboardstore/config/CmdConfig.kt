package net.kigawa.oyu.scoreboardstore.config

import net.kigawa.oyu.scoreboardstore.util.config.ConfigObject
import net.kigawa.oyu.scoreboardstore.util.config.annotation.ConfigValue

class CmdConfig : ConfigObject {

  @ConfigValue
  val onJoin = listOf<String>()

  @ConfigValue
  val onQuit = listOf<String>()

  @ConfigValue
  val onReady = listOf<String>()

}