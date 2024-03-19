package net.kigawa.oyu.scoreboardstore.util.config

import net.kigawa.kutil.unitapi.annotation.Inject


abstract class ConfigParent : ConfigObject {

  @Inject
  private lateinit var configManager: ConfigManager
  fun save() {
    configManager.save(this)
  }
}