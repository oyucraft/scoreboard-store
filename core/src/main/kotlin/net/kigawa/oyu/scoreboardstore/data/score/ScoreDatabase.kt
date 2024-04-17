package net.kigawa.oyu.scoreboardstore.data.score

import kotlinx.coroutines.runBlocking
import net.kigawa.kutil.unitapi.annotation.Kunit
import net.kigawa.oyu.scoreboardstore.data.Connections
import net.kigawa.oyu.scoreboardstore.data.player.PlayerModel
import net.kigawa.oyu.scoreboardstore.util.concurrent.Coroutines
import java.sql.Connection

@Kunit
class ScoreDatabase(
    private val connections: Connections,
    private val coroutines: Coroutines,
) {

    suspend fun getScores(playerModel: PlayerModel) = coroutines.withIo {
        connections.get().use {
            it.byPlayer(playerModel)
        }
    }

    private fun Connection.byPlayer(playerModel: PlayerModel) = prepareStatement(
        "SELECT player_id,`key`,value FROM score " +
                "WHERE player_id = ?"
    ).use { con ->
        con.setInt(1, playerModel.id)
        val result = con.executeQuery()
        mutableListOf<ScoreModel>()
            .apply {
                while (result.next()) {
                    add(
                        ScoreModel(
                            key = result.getString("key"),
                            value = result.getInt("value")
                        )
                    )
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
        ).use {
            it.setInt(1, scoreModels.playerModel.id)
            it.setString(2, score.key)
            it.setInt(3, score.value)
            it.setInt(4, score.value)
            it.executeUpdate()
        }
    }


    fun save(key: String) {
        coroutines.launchIo {
            synchronized(scores) {
                val scores = runBlocking { scores.await() }
                scores.removeIf {
                    it.key == key
                }
                scores.add(
                    ScoreModel(
                        key,
                        scoreboardManager.mainScoreboard.getScores(player.name).first { it.objective.name == key }.score
                    )
                )
            }
        }
    }

    fun load(key: String, defaultValue: Int) {
        scoreboardManager.mainScoreboard.getScores(player.name).first { it.objective.name == key }.score = defaultValue
        coroutines.launchIo {
            synchronized(scores) {
                val score = runBlocking { scores.await() }.first {
                    it.key == key
                }
                scoreboardManager.mainScoreboard.getScores(player.name).first { it.objective.name == key }.score =
                    score.value
            }
        }
    }


}