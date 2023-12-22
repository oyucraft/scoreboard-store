package net.kigawa.oyu.scoreboardstore.pluginmessage

import org.bukkit.entity.Player

data class PluginMessage(val channel: String, val player: Player, val message: ByteArray) {
}