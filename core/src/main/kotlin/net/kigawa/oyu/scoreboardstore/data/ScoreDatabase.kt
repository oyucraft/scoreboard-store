package net.kigawa.oyu.scoreboardstore.data

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.runBlocking
import net.kigawa.kutil.unitapi.annotation.Kunit
import net.kigawa.oyu.scoreboardstore.util.concurrent.Coroutines

@Kunit
class ScoreDatabase(
    private val connections: Connections,
    private val coroutines: Coroutines,
) : AutoCloseable {
    private val scores: Deferred<MutableSet<PlayerScore>> = coroutines.asyncIo {
        val scores = mutableSetOf<PlayerScore>()
        connections.get().use con@{ con ->

            con.prepareStatement(
                "SELECT player_id,`key`,value FROM score " +
                        "WHERE player_id = ?"
            ).use {
                it.setInt(1, playerId.await())
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
        return@asyncIo scores
    }

    override fun close() {
        coroutines.launchIo {
            connections.get().use { con ->
                synchronized(scores) {
                    runBlocking {
                        scores.await().forEach { playerScore ->
                            con.prepareStatement(
                                "INSERT INTO score (player_id,`key`,value) VALUES (?,?,?) " +
                                        "ON DUPLICATE KEY UPDATE " +
                                        "value = ?"
                            ).use {
                                it.setInt(1, playerId.await())
                                it.setString(2, playerScore.key)
                                it.setInt(3, playerScore.value)
                                it.setInt(4, playerScore.value)
                                it.executeUpdate()
                            }
                        }
                    }
                }
            }
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
                    PlayerScore(
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