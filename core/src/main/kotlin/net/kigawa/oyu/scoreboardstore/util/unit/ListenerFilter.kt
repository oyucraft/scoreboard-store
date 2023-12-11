package net.kigawa.oyu.scoreboardstore.util.unit

import net.kigawa.kutil.unitapi.component.InitStack
import net.kigawa.kutil.unitapi.extention.InitializedFilter
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class ListenerFilter(
  private val plugin: JavaPlugin,
) : InitializedFilter {

  override fun <T : Any> filter(obj: T, stack: InitStack): T {
    if (obj !is Listener) return obj

    Bukkit.getServer().pluginManager.registerEvents(obj, plugin)
    return obj
  }
}