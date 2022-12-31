package me.zodd.strax.core.commands

import me.zodd.strax.core.service.StraxCommandService
import org.spongepowered.api.command.Command
import org.spongepowered.api.command.parameter.CommandContext
import org.spongepowered.api.command.parameter.CommonParameters
import org.spongepowered.api.entity.living.player.server.ServerPlayer
import org.spongepowered.api.service.permission.Subject

abstract class AbstractStraxCommand : StraxCommandService()

data class StraxCommand(
    val name: String,
) {
    val permission = "strax.$name"
    private val basePermission = "$permission.base"
    private val targetOthers = "$permission.others"
    val commandFlags = CommonCommandFlags(name)

    fun builder(builder: Command.Builder.(StraxCommand) -> Unit): Command.Parameterized {
        val cmd = Command.builder()
        cmd.permission(basePermission)
        cmd.builder(this)
        return cmd.build()
    }

    //Returns command runner or target player if applicable
    fun targetPlayer(ctx: CommandContext): ServerPlayer? {
        val root = ctx.cause().root() as Subject
        return if (root.hasPermission(targetOthers) && ctx.hasAny(CommonParameters.PLAYER_OPTIONAL)) {
            ctx.requireOne(CommonParameters.PLAYER_OPTIONAL)
        } else {
            if (ctx.cause().root() is ServerPlayer) {
                ctx.cause().root() as ServerPlayer
            } else {
                null
            }
        }
    }
}