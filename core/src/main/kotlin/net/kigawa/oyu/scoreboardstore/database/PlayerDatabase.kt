package net.kigawa.oyu.scoreboardstore.database

import org.bukkit.entity.Player
import org.bukkit.scoreboard.ScoreboardManager
import java.sql.Statement

class PlayerDatabase(
  val player: Player,
  private val connections: Connections,
  private val scoreboardManager: ScoreboardManager,
) : AutoCloseable {
  private val playerId: Int
  private val scores = mutableSetOf<PlayerScore>()

  init {
    connections.get().use con@{ con ->
      val resultId = con.prepareStatement("SELECT id FROM player WHERE uuid = ?").use {
        it.setString(1, player.uniqueId.toString())
        val result = it.executeQuery()
        return@use if (result.next()) {
          result.getInt("id")
        } else null
      }
      if (resultId != null) {
        playerId = resultId
      } else con.prepareStatement(
        "INSERT INTO player (uuid) VALUES (?)",
        Statement.RETURN_GENERATED_KEYS
      ).use { st ->
        st.setString(1, player.uniqueId.toString())
        st.executeUpdate()
        st.generatedKeys.use {
          it.next()
          playerId = it.getInt(1)
        }
      }

      con.prepareStatement(
        "SELECT player_id,`key`,value FROM score " +
            "WHERE player_id = ?"
      ).use {
        it.setInt(1, playerId)
        val result = it.executeQuery()
        synchronized(scores) {
          while (result.next()) {
            scores.add(
              PlayerScore(
                key = result.getString("key"),
                value = result.getInt("value")
              )
            )
          }
        }
      }

    }
  }

  override fun close() {
    connections.get().use { con ->
      synchronized(scores) {
        scores.forEach { playerScore ->
          con.prepareStatement(
            "INSERT INTO score (player_id,`key`,value) VALUES (?,?,?) " +
                "ON DUPLICATE KEY UPDATE " +
                "value = ?"
          ).use {
            it.setInt(1, playerId)
            it.setString(2, playerScore.key)
            it.setInt(3, playerScore.value)
            it.setInt(4, playerScore.value)
            it.executeUpdate()
          }
        }
      }
    }

  }

  fun save(key: String) {
    synchronized(scores) {
      scores.removeIf {
        it.key == key
      }
      scores.add(
        PlayerScore(
          key, scoreboardManager.mainScoreboard.getScores(player.name).first { it.objective.name == key }.score
        )
      )
    }
  }

  fun load(key: String) {
    synchronized(scores) {
      val score = scores.first {
        it.key == key
      }
      scoreboardManager.mainScoreboard.getScores(player.name).first { it.objective.name == key }.score = score.value
    }
  }


}