package net.kigawa.oyu.scoreboardstore

import net.kigawa.kutil.unitapi.annotation.Kunit
import org.bukkit.Server
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

@Kunit
class EventListener(
  private val scoreboardStoreConfig: ScoreboardStoreConfig,
  private val server: Server,
) : Listener {
  @EventHandler
  fun join(event: PlayerJoinEvent) {
    scoreboardStoreConfig.onJoin.forEach {
      server.dispatchCommand(event.player, it)
    }
  }

  @EventHandler
  fun quit(event: PlayerQuitEvent) {
    scoreboardStoreConfig.onQuit.forEach {
      server.dispatchCommand(event.player, it)
    }
  }
}