package net.kigawa.oyu.scoreboardstore.data.score

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import net.kigawa.kutil.unitapi.annotation.Kunit
import net.kigawa.oyu.scoreboardstore.data.player.PlayerModel

@Kunit
class ScoreManager(
    private val scoreDatabase: ScoreDatabase
) {
    private val scores = MutableStateFlow(listOf<ScoreModels>())
    suspend fun load(playerModel: PlayerModel) = scoreDatabase.getScores(playerModel).also { model ->
        scores.updateAndGet {
            it.plus(model)
        }
    }

    suspend fun unload(playerModel: PlayerModel) = scores.value
        .firstOrNull { it.playerModel == playerModel }?.also { score ->
            scores.update { it.minus(score) }
            scoreDatabase.saveScores(score)
        }
}