package io.github.oneservermc.onedatabase.util

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import io.github.oneservermc.onedatabase.util.config.Config
import io.github.oneservermc.onedatabase.util.config.ConfigInitializedFilter
import io.github.oneservermc.onedatabase.util.config.ConfigManager
import io.github.oneservermc.onedatabase.util.config.ConfigUtil
import io.github.oneservermc.onedatabase.util.unit.ListenerFilter
import io.github.oneservermc.onedatabase.util.unit.PluginUnitLogger
import net.kigawa.kutil.unitapi.component.InitializedFilterComponent
import net.kigawa.kutil.unitapi.component.UnitLoggerComponent
import net.kigawa.kutil.unitapi.component.container.UnitContainer
import net.kigawa.kutil.unitapi.registrar.ClassRegistrar
import net.kigawa.kutil.unitapi.registrar.InstanceRegistrar
import net.kigawa.kutil.unitapi.registrar.ResourceRegistrar
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

    container.getUnit(ResourceRegistrar::class.java).register(javaClass)

    CommandAPI.onEnable()

    Bukkit.getScheduler().runTaskTimerAsynchronously(this, Runnable {
      container.getUnitList(Config::class.java).forEach {
        it.save()
      }
    }, 5 * 60 * 1000, 5 * 60 * 1000)
  }
}