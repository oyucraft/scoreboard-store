package net.kigawa.oyu.scoreboardstore.status

import org.bukkit.entity.Player
import org.bukkit.scoreboard.Score
import org.bukkit.scoreboard.ScoreboardManager

class StatusEntry(
  private val scoreboardManager: ScoreboardManager,
  val statusType: StatusType,
  private val player: Player,
) {
  private val score: Score
  var value: Int = -1
    private set

  init {
    val obj = scoreboardManager.mainScoreboard.getObjective(statusType.name)
      ?: scoreboardManager.mainScoreboard.registerNewObjective(statusType.name, "dummy", statusType.name)
    score = obj.getScore(player.name)
    score.score = -1
  }

  fun updateScore(newScore: Int) {
    score.score = newScore
    value = newScore
  }

  fun setDefault() {
    value = statusType.defaultValue
    score.score = statusType.defaultValue
  }
}