package net.kigawa.oyu.scoreboardstore.status

import kotlinx.coroutines.runBlocking
import net.kigawa.oyu.scoreboardstore.database.Connections
import net.kigawa.oyu.scoreboardstore.database.PlayerDatabase
import net.kigawa.oyu.scoreboardstore.util.concurrent.Coroutines
import org.bukkit.entity.Player
import org.bukkit.scoreboard.ScoreboardManager

class StatusDatabase(
  val player: Player,
  private val playerDatabase: PlayerDatabase,
  private val coroutines: Coroutines,
  private val connections: Connections,
  private val scoreboardManager: ScoreboardManager,
) : AutoCloseable {
  private val statuses = mutableListOf<StatusEntry>()

  init {
    StatusType.entries.forEach {
      statuses.add(StatusEntry(scoreboardManager, it, player))
    }

    coroutines.launchIo {

      connections.get().use con@{ con ->

        con.prepareStatement(
          "SELECT type,value FROM status " +
              "WHERE player_id = ?"
        ).use { st ->
          st.setInt(1, playerDatabase.playerId.await())
          val result = st.executeQuery()
          while (result.next()) {
            val type = StatusType.entries.first { it.key == result.getInt("type") }
            val value = result.getInt("value")
            synchronized(statuses) {
              statuses.first { it.statusType == type }.updateValue(value)
            }
          }
        }

      }
      statuses.filter { it.value == -1 }.forEach(StatusEntry::setDefault)

    }
  }


  override fun close() {
    coroutines.launchIo {
      connections.get().use { con ->
        synchronized(statuses) {
          runBlocking {
            statuses.forEach { entry ->
              con.prepareStatement(
                "INSERT INTO status (player_id,type,value) VALUES (?,?,?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "value = ?"
              ).use {
                it.setInt(1, playerDatabase.playerId.await())
                it.setInt(2, entry.statusType.key)
                it.setInt(3, entry.value)
                it.setInt(4, entry.value)
                it.executeUpdate()
              }
            }
          }
        }
      }
    }

  }

  fun getStatus(statusType: StatusType): StatusEntry {
    return statuses.first { it.statusType == statusType }
  }
}