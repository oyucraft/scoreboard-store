package net.kigawa.oyu.scoreboardstore.util.config

import net.kigawa.kutil.unitapi.component.InitStack
import net.kigawa.kutil.unitapi.extention.InitializedFilter

class ConfigInitializedFilter(
  private val configManager: ConfigManager,
): InitializedFilter {
  
  
  override fun <T: Any> filter(obj: T, stack: InitStack): T {
    if (obj !is ConfigParent) return obj
    
    configManager.load(obj)
    configManager.save(obj)
    
    return obj
  }
}