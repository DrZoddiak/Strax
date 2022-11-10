package me.zodd.strax.modules.kick

import com.google.auto.service.AutoService
import me.zodd.strax.core.commands.AbstractStraxCommand
import me.zodd.strax.core.service.StraxCommandService
import me.zodd.strax.core.commands.CommonCommandFlags
import me.zodd.strax.core.commands.CommonCommandParameters
import me.zodd.strax.core.commands.StraxCommand
import me.zodd.strax.core.utils.Notify
import net.kyori.adventure.text.Component
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.Command
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.parameter.CommonParameters

@AutoService(StraxCommandService::class)
class KickCommand : AbstractStraxCommand() {

    private val kickConfig = config.modules.kickConfig

    private val kickAll = StraxCommand("kickall").builder { cmd ->
        shortDescription(Component.text("Kicks all users from the game"))
        addFlag(cmd.commandFlags.force)
        addParameters(CommonCommandParameters.optionalMessage)
        permission("${cmd.permission}.base")
        executor { context ->
            var kickMessage = kickConfig.kickMessage

            if (context.hasAny(CommonCommandParameters.optionalMessage)) {
                kickMessage = context.requireOne(CommonCommandParameters.optionalMessage)
            }

            val onlinePlayers = Sponge.server().onlinePlayers()

            val targetPlayers = if (!context.hasFlag(cmd.commandFlags.force)) {
                onlinePlayers.filterNot { (it.hasPermission("strax.kick.exempt.target")) }
            } else {
                onlinePlayers.filter { it.hasPermission("strax.kick.exempt.target") }
            }

            targetPlayers.forEach {
                it.kick(Component.text(kickMessage))
                Notify.notifyPlayers("strax.kick.notify", Component.text("${it.name()} was kicked"))
            }

            return@executor CommandResult.success()
        }
    }

    private val kickUser = StraxCommand("kick").builder { cmd ->
        shortDescription(Component.text("Kicks a user from the game"))
        addFlag(cmd.commandFlags.force)
        addParameters(CommonParameters.PLAYER, CommonCommandParameters.optionalMessage)
        permission("${cmd.permission}.base")
        executor { context ->

            val targetPlayer = context.requireOne(CommonParameters.PLAYER)

            var kickMessage = kickConfig.kickMessage

            if (context.hasAny(CommonCommandParameters.optionalMessage)) {
                kickMessage = context.requireOne(CommonCommandParameters.optionalMessage)
            }

            if (!context.hasFlag(cmd.commandFlags.force)) {
                if (targetPlayer.hasPermission("strax.kick.exempt.target"))
                    return@executor CommandResult.error(Component.text("User was exempt from being kicked!"))
            }

            targetPlayer.kick(Component.text(kickMessage))

            Notify.notifyPlayers("strax.kick.notify", Component.text("${targetPlayer.name()} has been kicked"))

            return@executor CommandResult.success()
        }
    }

    override val commandMap = mapOf(
        kickAll to arrayOf("kickall"),
        kickUser to arrayOf("kick")
    )
}

