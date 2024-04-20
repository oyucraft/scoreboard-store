package net.kigawa.oyu.scoreboardstore

import net.kigawa.kutil.unitapi.annotation.Kunit
import net.kigawa.oyu.scoreboardstore.config.ScoreStoreConfig
import org.bukkit.Bukkit

@Kunit
class DbugLogger(
    private val config: ScoreStoreConfig
) {
    fun log(message: String) {
        if (!config.debug) return
        println(message)
        Bukkit.getOnlinePlayers().filter { it.isOp }.forEach {
            it.sendMessage("score store debug: $message")
        }
    }
}