package net.kigawa.oyu.scoreboardstore.data.player

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import net.kigawa.kutil.unitapi.annotation.Kunit
import org.bukkit.entity.Player

@Kunit
class PlayerManager(
    private val playerDatabase: PlayerDatabase
) {
    private val players = MutableStateFlow(listOf<PlayerModel>())

    suspend fun load(player: Player) = playerDatabase.getPlayer(player).also { model ->
        players.update {
            it.plus(model)
        }
    }

    fun unload(player: Player) = players.value.first { it.player == player }.also { model ->
        players.update { list ->
            list.minus(model)
        }
    }
}