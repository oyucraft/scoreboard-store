package net.kigawa.oyu.scoreboardstore.util

import net.kigawa.kutil.unitapi.UnitIdentify
import net.kigawa.kutil.unitapi.extention.UnitFinder
import net.kigawa.kutil.unitapi.options.FindOptions
import org.bukkit.Server
import org.bukkit.scoreboard.ScoreboardManager

class PluginFinder(
  private val server: Server,
) : UnitFinder {
  override fun <T : Any> findUnits(identify: UnitIdentify<T>, findOptions: FindOptions): List<T>? {
    return null
  }

  @Suppress("UNCHECKED_CAST")
  private fun <TARGET : Any, IDENTIFY : Any> cast(
    clazz: Class<TARGET>, identify: UnitIdentify<IDENTIFY>, block: () -> TARGET?,
  ): List<IDENTIFY>? {
    if (identify.instanceOf(clazz)) return block()?.let { listOf(it as IDENTIFY) }
    return null
  }
}