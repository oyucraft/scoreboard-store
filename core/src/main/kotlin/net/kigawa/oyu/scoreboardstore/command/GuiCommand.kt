package net.kigawa.oyu.scoreboardstore.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.CommandExecutor
import net.kigawa.kutil.unitapi.annotation.Kunit
import net.kigawa.oyu.scoreboardstore.config.ScoreStoreConfig

@Kunit
class GuiCommand(
    private val config: ScoreStoreConfig,
) {
  fun cmd(): CommandAPICommand = CommandAPICommand("gui")
    .withArguments(
      StringArgument("action").replaceSuggestions(ArgumentSuggestions.strings("open"))
    ).withArguments(
      StringArgument("gui name")
        .replaceSuggestions(ArgumentSuggestions.strings { config.gui.map { it.name }.toTypedArray() })
    ).executes(CommandExecutor { commandSender, commandArguments ->

    })

}
