package net.kigawa.oyu.scoreboardstore.data.score

import net.kigawa.oyu.scoreboardstore.data.player.PlayerModel

data class ScoreModels(
    val playerModel: PlayerModel,
    val scores: List<ScoreModel>
)