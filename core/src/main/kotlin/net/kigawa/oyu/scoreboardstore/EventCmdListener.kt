package net.kigawa.oyu.scoreboardstore

import net.kigawa.kutil.unitapi.annotation.Kunit
import net.kigawa.oyu.scoreboardstore.config.ScoreStoreConfig
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

@Kunit
class EventCmdListener(
    private val scoreboardStoreConfig: ScoreStoreConfig
) : Listener {
    @EventHandler(priority = EventPriority.LOW)
    fun join(event: PlayerJoinEvent) {
        scoreboardStoreConfig.cmd.onJoin.forEach {
            event.player.performCommand(it)
        }
    }

    @EventHandler
    fun quit(event: PlayerQuitEvent) {
        scoreboardStoreConfig.cmd.onQuit.forEach {
            event.player.performCommand(it)
        }
    }
}