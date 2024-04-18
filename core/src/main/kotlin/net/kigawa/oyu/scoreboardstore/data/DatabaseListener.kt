package net.kigawa.oyu.scoreboardstore.data

import net.kigawa.kutil.unitapi.annotation.Kunit
import net.kigawa.oyu.scoreboardstore.data.player.PlayerManager
import net.kigawa.oyu.scoreboardstore.data.score.ScoreManager
import net.kigawa.oyu.scoreboardstore.status.StatusDatabase
import net.kigawa.oyu.scoreboardstore.util.concurrent.Coroutines
import org.bukkit.entity.Player
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
    private val scoreManager: ScoreManager
) : Listener {
    private val statusDatabases = mutableListOf<StatusDatabase>()

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
            synchronized(statusDatabases) {
                statusDatabases.add(
                    StatusDatabase(
                        playerJoinEvent.player,
                        coroutines,
                        connections,
                        scoreboardManager,
                        playerModel
                    )
                )
            }
        }
    }

    @EventHandler
    fun quit(playerQuitEvent: PlayerQuitEvent) {
        val player = playerQuitEvent.player
        coroutines.launchDefault {
            val playerModel = playerManager.unload(player)
            val scores = scoreManager.unload(playerModel)
            synchronized(statusDatabases) {
                statusDatabases.filter {
                    playerQuitEvent.player == it.player
                }.forEach {
                    statusDatabases.remove(it)
                    it.close()
                }
            }
        }
    }


    fun getStatusDatabase(player: Player): StatusDatabase {
        return synchronized(statusDatabases) {
            statusDatabases.first {
                it.player == player
            }
        }
    }
}