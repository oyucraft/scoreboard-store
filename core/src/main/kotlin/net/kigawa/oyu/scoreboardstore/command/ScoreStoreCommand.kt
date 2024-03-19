package net.kigawa.oyu.scoreboardstore.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.CommandExecutor
import net.kigawa.kutil.kutil.reflection.KutilReflect
import net.kigawa.kutil.unitapi.annotation.Kunit
import net.kigawa.oyu.scoreboardstore.database.Database
import net.kigawa.oyu.scoreboardstore.pluginmessage.PluginMessages
import net.kigawa.oyu.scoreboardstore.util.command.SubCommand
import net.kigawa.oyu.scoreboardstore.util.concurrent.Coroutines
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scoreboard.ScoreboardManager
import java.util.concurrent.CompletableFuture

@Kunit
class ScoreStoreCommand(
  private val scoreboardManager: ScoreboardManager,
  private val database: Database,
  private val pluginMessages: PluginMessages,
  private val coroutines: Coroutines,
  private val guiCommand: GuiCommand,
) {
  init {
    val commandAPICommand =
      CommandAPICommand("score-store")
        .withPermission(CommandPermission.OP)
    KutilReflect.getAllExitMethod(javaClass).forEach {
      it.getAnnotation(SubCommand::class.java) ?: return@forEach
      if (it.returnType != CommandAPICommand::class.java) return@forEach

      it.isAccessible = true

      commandAPICommand.withSubcommand(it.invoke(this) as CommandAPICommand)
    }

    commandAPICommand.register()
  }

  @SubCommand
  fun save(): CommandAPICommand = CommandAPICommand("save")
    .withArguments(PlayerArgument("player"))
    .withArguments(StringArgument("scoreboard name").replaceSuggestions(ArgumentSuggestions.stringsAsync {
      CompletableFuture.supplyAsync {
        return@supplyAsync scoreboardManager.mainScoreboard.objectives.map {
          it.name
        }.toTypedArray()
      }
    }))
    .executes(CommandExecutor { _: CommandSender, commandArguments: CommandArguments ->
      val player = commandArguments.get("player") as Player
      val key = commandArguments.get("scoreboard name") as String
      database.getPlayerDatabase(player).save(key)
    })

  @SubCommand
  fun load(): CommandAPICommand = CommandAPICommand("load")
    .withArguments(PlayerArgument("player"))
    .withArguments(IntegerArgument("default value"))
    .withArguments(StringArgument("scoreboard name").replaceSuggestions(ArgumentSuggestions.stringsAsync {
      CompletableFuture.supplyAsync {
        return@supplyAsync scoreboardManager.mainScoreboard.objectives.map {
          it.name
        }.toTypedArray()
      }
    }))
    .executes(CommandExecutor { _: CommandSender, commandArguments: CommandArguments ->
      val player = commandArguments.get("player") as Player
      val key = commandArguments.get("scoreboard name") as String
      val defaultValue = commandArguments.get("default value") as Int
      database.getPlayerDatabase(player).load(key, defaultValue)
    })

  @SubCommand
  fun playerCount(): CommandAPICommand = CommandAPICommand("player-count")
    .withArguments(PlayerArgument("player"))
    .withArguments(StringArgument("scoreboard name").replaceSuggestions(ArgumentSuggestions.stringsAsync {
      CompletableFuture.supplyAsync {
        return@supplyAsync scoreboardManager.mainScoreboard.objectives.map {
          it.name
        }.toTypedArray()
      }
    }))
    .withArguments(StringArgument("server name"))
    .executes(CommandExecutor { _, commandArguments ->
      val scoreBoard = commandArguments.get("scoreboard name") as String
      val serverName = commandArguments.get("server name") as String
      val player = commandArguments.get("player") as Player

      scoreboardManager.mainScoreboard.getScores(player.name).first { it.objective.name == scoreBoard }.score = -1
      coroutines.launchDefault {
        val count = pluginMessages.getPlayerCount(player, serverName).await()
        println(count)
        scoreboardManager.mainScoreboard.getScores(player.name).first { it.objective.name == scoreBoard }.score = count
      }
    })

  @SubCommand
  fun gui() = guiCommand.cmd()
}