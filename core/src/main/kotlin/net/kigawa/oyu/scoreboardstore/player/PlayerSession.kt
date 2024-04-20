package net.kigawa.oyu.scoreboardstore.player

import net.kigawa.oyu.scoreboardstore.DbugLogger
import net.kigawa.oyu.scoreboardstore.status.StatusDatabase
import net.kigawa.oyu.scoreboardstore.status.StatusType
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor

class PlayerSession(
    val player: Player,
    statusDatabase: StatusDatabase,
    private val debugLogger: DbugLogger
) {
    private val hp = statusDatabase.getStatus(StatusType.HP)
    private val maxHp = statusDatabase.getStatus(StatusType.MAX_HP)
    private val random = Random()
    fun damage(event: EntityDamageEvent) {
        debugLogger.log("on damage: ${player}")
        if (hp.value < 0) return

        val damage = event.finalDamage
        val newHp = hp.value - if (random.nextBoolean()) floor(damage).toInt() else ceil(damage).toInt()

        if (newHp <= 0) {
            debugLogger.log("set hp: ${maxHp.value}")
            hp.updateValue(maxHp.value)
        } else {
            debugLogger.log("set hp: $newHp")
            hp.updateValue(newHp)
            event.damage = 0.0
        }
    }

    fun regen(value: Double) {
        debugLogger.log("on regen: ${player}")
        if (hp.value < 0) return

        val newHp = hp.value + if (random.nextBoolean()) floor(value).toInt() else ceil(value).toInt()

        if (newHp > maxHp.value) {
            hp.updateValue(maxHp.value)
            debugLogger.log("set hp: ${maxHp.value}")
        } else {
            debugLogger.log("set hp: ${newHp}")
            hp.updateValue(newHp)
        }
    }

    fun applyStatus() {
        if (player.health == 0.0) return

        var hpValue = hp.value.toDouble() / maxHp.value * 20

        if (hpValue < 0) hpValue = 0.0
        if (hpValue > 20) hpValue = 20.0

        player.health = hpValue
    }
}