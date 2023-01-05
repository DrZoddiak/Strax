package me.zodd.strax.modules.nameban

import com.google.auto.service.AutoService
import me.zodd.strax.core.commands.AbstractStraxCommand
import me.zodd.strax.core.commands.CommonCommandParameters
import me.zodd.strax.core.commands.StraxCommand
import me.zodd.strax.core.service.StraxCommandService
import net.kyori.adventure.text.Component
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.parameter.CommonParameters

@AutoService(StraxCommandService::class)
class NamebanCommand : AbstractStraxCommand() {
    val storage = NamebanStorage()

    private val nameban = StraxCommand("nameban").builder { cmd ->
        shortDescription(Component.text("Bans a username from joining the server"))
        addParameters(
            CommonParameters.MESSAGE, CommonCommandParameters.optionalMessage
        )
        executor { ctx ->

            val msg = ctx.requireOne(CommonParameters.MESSAGE)

            val reason =
                if (ctx.hasAny(CommonCommandParameters.optionalMessage))
                    ctx.requireOne(CommonCommandParameters.optionalMessage)
                else {
                    null
                }

            if (reason == null)
                storage.addEntry(msg)
            else
                storage.addEntry(msg, reason)

            CommandResult.success()
        }
    }

    private val nameunban = StraxCommand("nameunban").builder { cmd ->
        shortDescription(Component.text("Unbans a username from joining the server"))
        addParameters(
            CommonParameters.MESSAGE
        )
        executor { ctx ->

            val msg = ctx.requireOne(CommonParameters.MESSAGE)

            storage.removeEntry(msg)

            CommandResult.success()
        }
    }

    override val commandMap = mapOf(
        nameban to arrayOf("nameban"),
        nameunban to arrayOf("nameunban")
    )
}