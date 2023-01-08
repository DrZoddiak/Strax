package me.zodd.strax.core.commands

import org.spongepowered.api.command.parameter.CommandContext
import org.spongepowered.api.command.parameter.CommonParameters
import org.spongepowered.api.command.parameter.Parameter
import java.util.UUID

object CommonCommandParameters {
    val optionalMessage = Parameter.remainingJoinedStrings().key("optionalMessage").optional().build()

    val duration = Parameter.duration().key("duration")
        .build()

    val uuidParameter = Parameter.uuid().key("uuid").build()
    val userParameter = Parameter.user().key("user").build()
    val nameParameter = Parameter.string().key("name").build()

    val userOrUuid = Parameter
        .firstOfBuilder(userParameter)
        .or(uuidParameter)
        .build()

    val userOrPlayer = Parameter
        .firstOfBuilder(CommonParameters.PLAYER)
        .or(userParameter)
        .or(uuidParameter)
        .build()


    fun uuidFromUserOrUuid(ctx: CommandContext): UUID {
        return if (ctx.hasAny(userParameter)) {
            ctx.requireOne(userParameter)
        } else {
            ctx.requireOne(uuidParameter)
        }
    }
}