package net.kigawa.oyu.scoreboardstore.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.CommandExecutor
import net.kigawa.oyu.scoreboardstore.util.command.AbstractCommand
import net.kigawa.oyu.scoreboardstore.util.command.SubCommand
import net.kigawa.kutil.unitapi.annotation.Kunit
import org.bukkit.command.CommandSender
import org.bukkit.scoreboard.ScoreboardManager
import java.util.concurrent.CompletableFuture

@Kunit
class ScoreStoreCommand(
  private val scoreboardManager: ScoreboardManager,
) : AbstractCommand(
  CommandAPICommand("score-store")
    .withPermission(CommandPermission.OP)
) {

  @SubCommand
  fun save() = CommandAPICommand("save")
    .withArguments(PlayerArgument("player"))
    .withArguments(StringArgument("scoreboard name").replaceSuggestions(ArgumentSuggestions.stringsAsync {
      CompletableFuture.supplyAsync {
        return@supplyAsync arrayOf()
      }
    }))
    .executes(CommandExecutor { commandSender: CommandSender, commandArguments: CommandArguments ->

    })
}