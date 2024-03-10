package net.kigawa.oyu.scoreboardstore.player

import net.kigawa.kutil.unitapi.annotation.Kunit
import net.kigawa.oyu.scoreboardstore.ScoreboardStore
import net.kigawa.oyu.scoreboardstore.database.Database
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitScheduler

@Kunit
class PlayerManager(
  private val database: Database,
  private val scoreboardStore: ScoreboardStore,
  private val scheduler: BukkitScheduler,
) : Listener {
  private val sessions = mutableListOf<PlayerSession>()

  init {
    scheduler.runTaskTimer(scoreboardStore, Runnable {
      synchronized(sessions) {
        sessions.toMutableList()
      }.forEach(PlayerSession::applyStatus)
    }, 20, 20)
  }

  @EventHandler
  fun onJoin(event: PlayerJoinEvent) {
    val database = database.getStatusDatabase(event.player)
    synchronized(sessions) {
      sessions.add(
        PlayerSession(
          event.player, database,
        )
      )
    }
  }

  @EventHandler
  fun onQuit(event: PlayerQuitEvent) {
    synchronized(sessions) {
      sessions.removeIf { event.player == it.player }
    }
  }

  @EventHandler
  fun onDamage(event: EntityDamageEvent) {
    val entity = event.entity
    if (entity !is Player) return
    synchronized(sessions) {
      sessions.filter { it.player == entity }
    }.forEach {
      it.damage(event.damage)
    }
    event.damage = 0.0
  }
}