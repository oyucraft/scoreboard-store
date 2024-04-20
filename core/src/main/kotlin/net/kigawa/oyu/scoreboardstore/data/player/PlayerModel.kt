package net.kigawa.oyu.scoreboardstore.data.player

import org.bukkit.entity.Player

data class PlayerModel(
    val id: Int,
    val player: Player
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlayerModel

        if (id != other.id) return false
        if (player != other.player) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + player.hashCode()
        return result
    }
}