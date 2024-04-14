package net.kigawa.oyu.scoreboardstore

import net.kigawa.kutil.unitapi.annotation.Kunit
import net.kigawa.oyu.scoreboardstore.config.ScoreboardStoreConfig
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

@Kunit
class EventListener(
  private val scoreboardStoreConfig: ScoreboardStoreConfig,
) : Listener {
  @EventHandler
  fun join(event: PlayerJoinEvent) {
    scoreboardStoreConfig.onJoin.forEach {
      event.player.performCommand(it)
    }
  }

  @EventHandler
  fun quit(event: PlayerQuitEvent) {
    scoreboardStoreConfig.onQuit.forEach {
      event.player.performCommand(it)
    }
  }

}