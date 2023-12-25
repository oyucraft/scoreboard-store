package net.kigawa.oyu.scoreboardstore.database

import net.kigawa.kutil.unitapi.annotation.Kunit
import net.kigawa.oyu.scoreboardstore.util.concurrent.Coroutines
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scoreboard.ScoreboardManager

@Kunit
class Database(
  private val connections: Connections,
  private val scoreboardManager: ScoreboardManager,
  private val coroutines: Coroutines
) : Listener {
  private val playerDatabases = mutableListOf<PlayerDatabase>()

  init {
    connections.hikari.connection.use { con ->
      con.prepareStatement(
        "CREATE TABLE IF NOT EXISTS player(" +
            "id INT auto_increment PRIMARY KEY," +
            "uuid VARCHAR(40)" +
            ")"
      ).use {
        it.execute()
      }
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
    }
  }

  @EventHandler
  fun join(playerJoinEvent: PlayerJoinEvent) {
    synchronized(playerDatabases) {
      playerDatabases.add(
        PlayerDatabase(
          playerJoinEvent.player,
          connections,
          scoreboardManager = scoreboardManager,
          coroutines = coroutines
        )
      )
    }
  }

  @EventHandler
  fun quit(playerQuitEvent: PlayerQuitEvent) {
    synchronized(playerDatabases) {
      playerDatabases.filter {
        playerQuitEvent.player == it.player
      }.forEach {
        playerDatabases.remove(it)
        it.close()
      }
    }
  }

  fun getPlayerDatabase(player: Player): PlayerDatabase {
    return playerDatabases.first {
      it.player == player
    }
  }
}