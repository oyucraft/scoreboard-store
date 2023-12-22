package net.kigawa.oyu.scoreboardstore.pluginmessage

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.channels.Channel
import net.kigawa.kutil.unitapi.annotation.Kunit
import net.kigawa.oyu.scoreboardstore.util.PluginBase
import net.kigawa.oyu.scoreboardstore.util.concurrent.Coroutines
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream


@Kunit
class PluginMessages(
  private val server: Server,
  private val plugin: PluginBase,
  private val coroutines: Coroutines,
) : PluginMessageListener, AutoCloseable {
  private val receiveChannel = Channel<Int>(1)
  private val taskChannel = Channel<suspend () -> Unit>()

  init {
    server.messenger.registerOutgoingPluginChannel(plugin, "BungeeCord");
    server.messenger.registerIncomingPluginChannel(plugin, "BungeeCord", this);
    coroutines.launchDefault {
      for (task in taskChannel) {
        try {
          task.invoke()
        } catch (e: Exception) {
          e.printStackTrace()
        }
      }
      receiveChannel.close()
      server.messenger.unregisterOutgoingPluginChannel(plugin);
      server.messenger.unregisterIncomingPluginChannel(plugin);
    }
  }


  private suspend fun <T> runTask(
    player: Player,
    vararg send: String,
    receive: suspend () -> T,
  ): Deferred<T> {
    val res = coroutines.async(start = CoroutineStart.LAZY) {
      val byteOut = ByteArrayOutputStream()
      val out = DataOutputStream(byteOut)
      send.forEach {
        out.writeUTF(it)
      }
      player.sendPluginMessage(plugin, "BungeeCord", byteOut.toByteArray())

      receive.invoke()
    }
    taskChannel.send {
      res.start()
      res.await()
    }
    return res
  }

  override fun close() {
    taskChannel.close()
  }

  override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
    coroutines.launchDefault {
      var result = 0
      try {
        if (channel != "BungeeCord") {
          result = -2
          return@launchDefault
        }
        val byteIn = DataInputStream(ByteArrayInputStream(message))

        when (val sub = byteIn.readUTF()) {
          "PlayerCount" -> {
            byteIn.readUTF()
            result = byteIn.readInt()
          }

          else -> {
            println(sub)
            result = -3
          }
        }
      } finally {
        receiveChannel.send(result)
      }
    }
  }


  suspend fun getPlayerCount(player: Player, server: String) = runTask(player, "PlayerCount", server) {
    return@runTask receiveChannel.receive()
  }

}