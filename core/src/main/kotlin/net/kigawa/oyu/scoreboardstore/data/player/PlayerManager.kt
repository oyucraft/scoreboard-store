package net.kigawa.oyu.scoreboardstore.data.player

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.updateAndGet
import net.kigawa.kutil.unitapi.annotation.Kunit
import org.bukkit.entity.Player

@Kunit
class PlayerManager(
    private val playerDatabase: PlayerDatabase
) {
    private val players = MutableStateFlow(listOf<PlayerModel>())

    suspend fun load(player: Player) = playerDatabase.getPlayer(player).await().let {
        players.updateAndGet {
            it.plus(it)
        }
    }
    fun unload(player: Player)  = players.updateAndGet { list ->
        list.filter { it.uuid != player.uniqueId }
    }
}