package me.zodd.strax.modules.ignore

import com.google.auto.service.AutoService
import me.zodd.strax.core.commands.AbstractStraxCommand
import me.zodd.strax.core.commands.StraxCommand
import me.zodd.strax.core.service.StraxCommandService
import net.kyori.adventure.text.Component
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.parameter.CommonParameters

@AutoService(StraxCommandService::class)
class IgnoreCommand : AbstractStraxCommand() {

    private val ignore = StraxCommand("ignore").builder {
        shortDescription(Component.text("Ignore another players messages"))
        addParameters(
            CommonParameters.PLAYER
        )
        executor { ctx ->

            val root = it.rootPlayer(ctx)?.uniqueId() ?: return@executor CommandResult.error(
                Component.text("Command must be run by a player")
            )

            val targetPlayer = it.targetPlayerOrRoot(ctx)?.uniqueId() ?: return@executor CommandResult.error(
                Component.text("Command must be run by a player or target another player!")
            )

            val storage = IgnoreStorage(root)

            if (storage.moduleData.ignoredUsers.contains(targetPlayer)) {
                storage.removeEntry(targetPlayer)
            } else {
                storage.addEntry(targetPlayer)
            }

            CommandResult.success()
        }

    }

    private val ignoreList = StraxCommand("ignorelist").builder {
        shortDescription(Component.text("Lists players you're ignoring"))
        addParameters(
            CommonParameters.PLAYER_OPTIONAL
        )
        executor { ctx ->

            val targetPlayer = it.targetPlayerOrRoot(ctx) ?: return@executor CommandResult.error(
                Component.text("Command must be run by a player or target another player!")
            )


            val users = IgnoreStorage(targetPlayer.uniqueId()).moduleData.ignoredUsers.joinToString { uuid ->
                Sponge.server().userManager().load(uuid).thenApply { it.get().name() }.join()
            }

            targetPlayer.sendMessage(Component.text(users))

            CommandResult.success()
        }

    }

    override val commandMap = mapOf(
        ignore to arrayOf("ignore"),
        ignoreList to arrayOf("ignorelist", "ignored", "listignore")
    )
}