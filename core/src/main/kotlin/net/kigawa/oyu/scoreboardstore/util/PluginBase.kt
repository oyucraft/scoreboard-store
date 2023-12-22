package net.kigawa.oyu.scoreboardstore.util

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import net.kigawa.kutil.unitapi.component.InitializedFilterComponent
import net.kigawa.kutil.unitapi.component.UnitFinderComponent
import net.kigawa.kutil.unitapi.component.UnitLoggerComponent
import net.kigawa.kutil.unitapi.component.container.UnitContainer
import net.kigawa.kutil.unitapi.registrar.ClassRegistrar
import net.kigawa.kutil.unitapi.registrar.InstanceRegistrar
import net.kigawa.kutil.unitapi.registrar.ResourceRegistrar
import net.kigawa.oyu.scoreboardstore.util.concurrent.Coroutines
import net.kigawa.oyu.scoreboardstore.util.config.Config
import net.kigawa.oyu.scoreboardstore.util.config.ConfigInitializedFilter
import net.kigawa.oyu.scoreboardstore.util.config.ConfigManager
import net.kigawa.oyu.scoreboardstore.util.config.ConfigUtil
import net.kigawa.oyu.scoreboardstore.util.unit.ListenerFilter
import net.kigawa.oyu.scoreboardstore.util.unit.PluginUnitLogger
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

abstract class PluginBase : JavaPlugin() {
  private val container = UnitContainer.create()
  val pluginClassLoader: ClassLoader
    get() = super.getClassLoader()

  override fun onLoad() {
    CommandAPI.onLoad(CommandAPIBukkitConfig(this))
  }

  override fun onEnable() {
    container.getUnit(InstanceRegistrar::class.java).apply {
      register(this@PluginBase)
      register(logger)
      register(server)
      server.scoreboardManager?.let { register(it) }
      register(Coroutines(logger))
    }
    container.getUnit(ResourceRegistrar::class.java).apply {
      register(javaClass)
    }
    container.getUnit(ClassRegistrar::class.java).apply {
      register(ConfigUtil::class.java)
      register(ConfigManager::class.java)
    }
    container.getUnit(UnitLoggerComponent::class.java).add(PluginUnitLogger::class.java)
    container.getUnit(InitializedFilterComponent::class.java).apply {
      add(ConfigInitializedFilter::class.java)
      add(ListenerFilter::class.java)
    }
    container.getUnit(UnitFinderComponent::class.java).add(PluginFinder::class.java)

    container.getUnit(ResourceRegistrar::class.java).register(javaClass)

    CommandAPI.onEnable()

    Bukkit.getScheduler().runTaskTimerAsynchronously(this, Runnable {
      container.getUnitList(Config::class.java).forEach {
        it.save()
      }
    }, 5 * 60 * 1000, 5 * 60 * 1000)
  }
}