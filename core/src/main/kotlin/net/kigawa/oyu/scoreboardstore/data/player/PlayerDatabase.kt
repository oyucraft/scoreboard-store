package net.kigawa.oyu.scoreboardstore.data.player

import net.kigawa.kutil.unitapi.annotation.Kunit
import net.kigawa.oyu.scoreboardstore.data.Connections
import net.kigawa.oyu.scoreboardstore.util.concurrent.Coroutines
import org.bukkit.entity.Player
import java.sql.Connection
import java.sql.Statement
import java.util.*

@Kunit
class PlayerDatabase(
    private val connections: Connections,
    private val coroutines: Coroutines
) {
    init {
        connections.get().use { con ->
            con.prepareStatement(
                "CREATE TABLE IF NOT EXISTS player(" +
                        "id INT auto_increment PRIMARY KEY," +
                        "uuid VARCHAR(40)" +
                        ")"
            ).use {
                it.execute()
            }
        }
    }

    suspend fun getPlayer(player: Player) = coroutines.withIo {
        connections.get().use con@{ con ->
            con.byPlayer(player) ?: con.insert(player)
        }
    }

    private fun Connection.byPlayer(player: Player) =
        prepareStatement("SELECT id,uuid FROM player WHERE uuid = ?").use {
            it.setString(1, player.uniqueId.toString())
            val result = it.executeQuery()
            return@use if (result.next()) {
                PlayerModel(
                    result.getInt("id"),
                    player
                )
            } else null
        }

    private fun Connection.insert(player: Player) = prepareStatement(
        "INSERT INTO player (uuid) VALUES (?)",
        Statement.RETURN_GENERATED_KEYS
    ).use st@{ st ->
        st.setString(1, player.uniqueId.toString())
        st.executeUpdate()
        st.generatedKeys.use {
            it.next()
            return@st PlayerModel(it.getInt(1), player)
        }
    }
}