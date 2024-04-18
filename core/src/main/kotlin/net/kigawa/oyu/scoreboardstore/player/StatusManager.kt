package net.kigawa.oyu.scoreboardstore.player

import net.kigawa.kutil.unitapi.annotation.Kunit
import net.kigawa.oyu.scoreboardstore.ScoreboardStore
import net.kigawa.oyu.scoreboardstore.data.DatabaseListener
import net.kigawa.oyu.scoreboardstore.status.StatusDatabase
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitScheduler

@Kunit
class StatusManager(
    scoreboardStore: ScoreboardStore,
    scheduler: BukkitScheduler,
) : Listener {
    private val sessions = mutableListOf<PlayerSession>()

    init {
        scheduler.runTaskTimer(scoreboardStore, Runnable {
            synchronized(sessions) {
                sessions.toMutableList()
            }.forEach(PlayerSession::applyStatus)
        }, 1, 1)
    }

    fun load(player: Player,statusDatabase: StatusDatabase) {
        synchronized(sessions) {
            sessions.add(
                PlayerSession(player, statusDatabase)
            )
        }
    }

    fun unload(player: Player) {
        synchronized(sessions) {
            sessions.removeIf { player == it.player }
        }
    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        val entity = event.entity
        if (entity !is Player) return
        synchronized(sessions) {
            sessions.firstOrNull() { it.player == entity }
        }?.damage(event)

    }

    @EventHandler
    fun onRegan(event: EntityRegainHealthEvent) {
        val entity = event.entity
        if (entity !is Player) return
        synchronized(sessions) {
            sessions.filter { it.player == entity }
        }.forEach {
            it.regen(event.amount)
        }
    }
}