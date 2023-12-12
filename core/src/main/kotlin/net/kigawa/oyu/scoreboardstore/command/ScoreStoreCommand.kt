package net.kigawa.oyu.scoreboardstore.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.CommandExecutor
import net.kigawa.kutil.unitapi.annotation.Kunit
import net.kigawa.oyu.scoreboardstore.database.Database
import net.kigawa.oyu.scoreboardstore.util.command.AbstractCommand
import net.kigawa.oyu.scoreboardstore.util.command.SubCommand
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scoreboard.ScoreboardManager
import java.util.concurrent.CompletableFuture

@Kunit
class ScoreStoreCommand(
  private val scoreboardManager: ScoreboardManager,
  private val database: Database,
) : AbstractCommand(
  CommandAPICommand("score-store")
    .withPermission(CommandPermission.OP)
) {

  @SubCommand
  fun save() = CommandAPICommand("save")
    .withArguments(PlayerArgument("player"))
    .withArguments(StringArgument("scoreboard name").replaceSuggestions(ArgumentSuggestions.stringsAsync {
      CompletableFuture.supplyAsync {
        return@supplyAsync scoreboardManager.mainScoreboard.objectives.map {
          it.name
        }.toTypedArray()
      }
    }))
    .executes(CommandExecutor { commandSender: CommandSender, commandArguments: CommandArguments ->
      val player = commandArguments.get("player") as Player
      val key = commandArguments.get("scoreboard name") as String
      database.getPlayerDatabase(player).save(key)
    })
  @SubCommand
  fun load() = CommandAPICommand("load")
    .withArguments(PlayerArgument("player"))
    .withArguments(StringArgument("scoreboard name").replaceSuggestions(ArgumentSuggestions.stringsAsync {
      CompletableFuture.supplyAsync {
        return@supplyAsync scoreboardManager.mainScoreboard.objectives.map {
          it.name
        }.toTypedArray()
      }
    }))
    .executes(CommandExecutor { commandSender: CommandSender, commandArguments: CommandArguments ->
      val player = commandArguments.get("player") as Player
      val key = commandArguments.get("scoreboard name") as String
      database.getPlayerDatabase(player).load(key)
    })
}