package net.kigawa.oyu.scoreboardstore.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import net.kigawa.kutil.unitapi.annotation.Kunit
import net.kigawa.oyu.scoreboardstore.EventCmdListener
import net.kigawa.oyu.scoreboardstore.data.player.PlayerManager
import net.kigawa.oyu.scoreboardstore.data.score.ScoreManager
import net.kigawa.oyu.scoreboardstore.player.StatusManager
import net.kigawa.oyu.scoreboardstore.status.StatusDatabase
import net.kigawa.oyu.scoreboardstore.util.Async
import net.kigawa.oyu.scoreboardstore.util.concurrent.Coroutines
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scoreboard.ScoreboardManager

@Kunit
class DatabaseListener(
    private val connections: Connections,
    private val scoreboardManager: ScoreboardManager,
    private val coroutines: Coroutines,
    private val playerManager: PlayerManager,
    private val scoreManager: ScoreManager,
    private val statusManager: StatusManager,
    private val eventCmdListener: EventCmdListener,
    private val async: Async
) : Listener {
    private val statusDatabases = MutableStateFlow(listOf<StatusDatabase>())

    init {
        connections.hikari.connection.use { con ->
            con.prepareStatement(
                "CREATE TABLE IF NOT EXISTS score( " +
                        "player_id INT, " +
                        "`key` varchar(64), " +
                        "value INT, " +
                        "FOREIGN KEY score(player_id) REFERENCES player(id), " +
                        "PRIMARY KEY(player_id,`key`)" +
                        ")"
            ).use {
                it.execute()
            }
            con.prepareStatement(
                "CREATE TABLE IF NOT EXISTS status( " +
                        "player_id INT, " +
                        "type INT, " +
                        "value INT, " +
                        "FOREIGN KEY status(player_id) REFERENCES player(id), " +
                        "PRIMARY KEY(player_id,type)" +
                        ")"
            ).use {
                it.execute()
            }
        }
    }

    @EventHandler
    fun join(playerJoinEvent: PlayerJoinEvent) {
        val player = playerJoinEvent.player
        coroutines.launchDefault {
            val playerModel = playerManager.load(player)
            val scores = scoreManager.load(playerModel)
            val statusDatabase = StatusDatabase.create(
                playerJoinEvent.player,
                coroutines,
                connections,
                scoreboardManager,
                playerModel
            )
            statusDatabases.update {
                it.plus(statusDatabase)
            }
            statusManager.load(player, statusDatabase)
            async.runTaskSync { eventCmdListener.ready(player) }
        }
    }

    @EventHandler
    fun quit(playerQuitEvent: PlayerQuitEvent) {
        val player = playerQuitEvent.player
        coroutines.launchDefault {
            val playerModel = playerManager.unload(player)
            val scores = scoreManager.unload(playerModel)
            statusManager.unload(player)
            statusDatabases.value.filter {
                playerQuitEvent.player == it.player
            }.also { toRm ->
                statusDatabases.update { it.minus(toRm.toSet()) }
            }.forEach {
                it.close()
            }
        }
    }


}