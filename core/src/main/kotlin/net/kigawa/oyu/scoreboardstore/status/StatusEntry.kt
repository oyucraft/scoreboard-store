package net.kigawa.oyu.scoreboardstore.status

import org.bukkit.entity.Player
import org.bukkit.scoreboard.Score
import org.bukkit.scoreboard.ScoreboardManager

class StatusEntry(
  scoreboardManager: ScoreboardManager,
  val statusType: StatusType,
  player: Player,
) {
  private val score: Score
  val value: Int
    get() = score.score


  init {
    val obj = scoreboardManager.mainScoreboard.getObjective(statusType.label)
      ?: scoreboardManager.mainScoreboard.registerNewObjective(statusType.label, "dummy", statusType.label)
    score = obj.getScore(player.name)
    score.score = -1
  }

  fun updateValue(newScore: Int) {
    score.score = newScore
  }

  fun setDefault() {
    score.score = statusType.defaultValue
  }
}