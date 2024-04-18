package net.kigawa.oyu.scoreboardstore.data.score

import net.kigawa.kutil.kutil.list.contains
import net.kigawa.kutil.unitapi.annotation.Kunit
import net.kigawa.oyu.scoreboardstore.config.ScoreStoreConfig
import net.kigawa.oyu.scoreboardstore.data.Connections
import net.kigawa.oyu.scoreboardstore.data.player.PlayerModel
import net.kigawa.oyu.scoreboardstore.util.concurrent.Coroutines
import org.bukkit.scoreboard.ScoreboardManager
import java.sql.Connection

@Kunit
class ScoreDatabase(
    private val connections: Connections,
    private val coroutines: Coroutines,
    private val scoreboardManager: ScoreboardManager,
    private val config: ScoreStoreConfig
) {

    suspend fun getScores(playerModel: PlayerModel) = coroutines.withIo {
        connections.get().use {
            it.byPlayer(playerModel)
        }
    }

    private fun Connection.byPlayer(playerModel: PlayerModel) = prepareStatement(
        "SELECT `key`,value FROM score " +
                "WHERE player_id = ?"
    ).use { con ->
        con.setInt(1, playerModel.id)
        val result = con.executeQuery()
        mutableListOf<ScoreModel>()
            .apply {
                while (result.next()) {
                    val key = result.getString("key")
                    val value = result.getInt("value")
                    add(ScoreModel(key))
                    scoreboardManager.mainScoreboard.getScores(playerModel.player.name)
                        .first { it.objective.name == key }.score = value
                }
            }
            .apply {
                config.saveScoreboard
                    .filter { configKey -> !contains { it.key == configKey } }
                    .map { ScoreModel(it) }
                    .also { addAll(it) }
                    .forEach {
                        val obj = scoreboardManager.mainScoreboard.getObjective(it.key)
                            ?: scoreboardManager.mainScoreboard.registerNewObjective(it.key, "dummy", it.key)
                        obj.getScore(playerModel.player.name).score = 0
                    }
            }
            .let { ScoreModels(playerModel, it) }
    }

    suspend fun saveScores(scoreModels: ScoreModels) = coroutines.withIo {
        connections.get().use {
            it.insertOrUpdate(scoreModels)
        }
    }

    private fun Connection.insertOrUpdate(scoreModels: ScoreModels) = scoreModels.scores.forEach { score ->
        prepareStatement(
            """
            INSERT INTO score (player_id,`key`,value) VALUES (?,?,?)
            ON DUPLICATE KEY UPDATE
            value = ?
            """
        ).use { st ->
            val value = scoreboardManager.mainScoreboard.getScores(scoreModels.playerModel.player.name)
                .first { it.objective.name == score.key }.score
            st.setInt(1, scoreModels.playerModel.id)
            st.setString(2, score.key)
            st.setInt(3, value)
            st.setInt(4, value)
            st.executeUpdate()
        }
    }
}