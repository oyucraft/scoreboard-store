package net.kigawa.oyu.scoreboardstore.player

import net.kigawa.oyu.scoreboardstore.status.StatusDatabase
import net.kigawa.oyu.scoreboardstore.status.StatusType
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor

class PlayerSession(
  val player: Player,
  statusDatabase: StatusDatabase,
) {
  private val hp = statusDatabase.getStatus(StatusType.HP)
  private val maxHp = statusDatabase.getStatus(StatusType.MAX_HP)
  private val random = Random()
  fun damage(damage: Double) {
    if (hp.value < 0) return

    var newHp = hp.value - if (random.nextBoolean()) floor(damage).toInt() else ceil(damage).toInt()
    if (newHp < 0) newHp = 0

    hp.updateValue(newHp)
    applyStatus()
  }

  fun applyStatus() {
    if (hp.value < 0) return
    if (player.health == 0.0) return

    val hpValue = hp.value.toDouble() / maxHp.value * 20
    player.health = hpValue

    if (hpValue <= 0) hp.updateValue(maxHp.value)
  }
}