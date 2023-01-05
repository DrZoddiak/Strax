package me.zodd.strax.core.commands

import me.zodd.strax.core.service.StraxCommandService
import org.spongepowered.api.command.Command
import org.spongepowered.api.command.parameter.CommandContext
import org.spongepowered.api.command.parameter.CommonParameters
import org.spongepowered.api.entity.living.player.server.ServerPlayer

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

    fun rootPlayer(ctx: CommandContext): ServerPlayer? {
        val root = ctx.cause().root()
        return if (root is ServerPlayer)
            root
        else null
    }

    fun targetPlayer(ctx: CommandContext): ServerPlayer? {
        val root = rootPlayer(ctx) ?: return null
        return if (root.hasPermission(targetOthers) && ctx.hasAny(CommonParameters.PLAYER)) {
            ctx.requireOne(CommonParameters.PLAYER)
        } else {
            null
        }
    }

    //Returns command runner or target player if applicable
    fun targetPlayerOrRoot(ctx: CommandContext): ServerPlayer? {
        val root = rootPlayer(ctx) ?: return null
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