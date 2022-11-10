package me.zodd.strax.modules.ban

import com.google.auto.service.AutoService
import me.zodd.strax.core.commands.AbstractStraxCommand
import me.zodd.strax.core.commands.CommonCommandFlags
import me.zodd.strax.core.commands.CommonCommandParameters
import me.zodd.strax.core.commands.StraxCommand
import me.zodd.strax.core.service.StraxCommandService
import me.zodd.strax.core.utils.Notify
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.Command
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.parameter.CommandContext
import org.spongepowered.api.command.parameter.CommonParameters
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.entity.living.player.server.ServerPlayer
import org.spongepowered.api.profile.GameProfile
import org.spongepowered.api.profile.property.ProfileProperty
import org.spongepowered.api.service.ban.Ban
import org.spongepowered.api.service.ban.BanTypes
import org.spongepowered.api.service.permission.Subject
import java.time.Duration
import java.time.Instant
import java.time.temporal.TemporalAmount
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.jvm.optionals.getOrDefault
import kotlin.math.exp

@AutoService(StraxCommandService::class)
class BanCommand : AbstractStraxCommand() {

    private val banConfig = config.modules.banConfig

    private val banUser = StraxCommand("ban").builder { cmd ->
        shortDescription(Component.text("Bans the user from the server"))
        addFlag(cmd.commandFlags.force)
        addParameters(CommonCommandParameters.userOrPlayer, CommonCommandParameters.optionalMessage)
        permission("${cmd.permission}.base")
        executor {
            processBan(it, cmd)
        }
    }

    private val tempBan = StraxCommand("tempban").builder { cmd ->
        shortDescription(Component.text("Temporarily bans a user from the server"))
        addFlag(cmd.commandFlags.force)
        addParameters(
            CommonCommandParameters.userOrPlayer,
            CommonCommandParameters.duration,
            CommonCommandParameters.optionalMessage
        )
        permission("${cmd.permission}.base")
        executor {
            processBan(it, cmd)
        }
    }

    private val checkBan = StraxCommand("checkban").builder { cmd ->
        shortDescription(Component.text("Look up a ban for a user"))
        addParameters(CommonCommandParameters.userOrUuid)
        permission("${cmd.permission}.base")
        executor { context ->

            val root = context.cause().root()
            val sender =
                if (root is Audience) root else return@executor CommandResult.error(Component.text("This wont work for you"))

            val id = CommonCommandParameters.uuidFromUserOrUuid(context)

            Sponge.server().gameProfileManager().profile(id).thenCompose { profile ->
                Sponge.server().serviceProvider().banService().find(profile).thenApply { ban ->
                    if (ban.isPresent) {
                        //TODO : build info page
                        sender.sendMessage(Component.text("${ban.get().profile().name()} for ${ban.get().reason()}"))
                        return@thenApply CommandResult.success()
                    } else {
                        return@thenApply CommandResult.error(Component.text("User was not banned!"))
                    }
                }
            }

            return@executor CommandResult.error(Component.text("Something went wrong!"))
        }
    }

    private val unban = StraxCommand("unban").builder { cmd ->
        shortDescription(Component.text("Unban a user"))
        addParameters(CommonCommandParameters.userOrUuid)
        permission("${cmd.permission}.base")
        executor { context ->
            val id = CommonCommandParameters.uuidFromUserOrUuid(context)

            Sponge.server().gameProfileManager().profile(id).thenCompose { profile ->
                removeBan(profile).thenApply {
                    if (it) {
                        Notify.notifyPlayers(
                            "${cmd.permission}.notify", Component.text("${profile.name()} has been unbanned!")
                        )
                        return@thenApply CommandResult.success()
                    } else {
                        return@thenApply CommandResult.error(Component.text("They were not banned"))
                    }
                }
            }

            return@executor CommandResult.error(Component.text("Something went wrong"))
        }
    }

    override val commandMap: Map<Command.Parameterized, Array<String>> = mapOf(
        banUser to arrayOf("ban"),
        tempBan to arrayOf("tempban"),
        checkBan to arrayOf("checkban"),
        unban to arrayOf("unban", "pardon")
    )

    private fun processBan(ctx: CommandContext, cmd: StraxCommand): CommandResult {
        val root = ctx.cause().root()
        val source = Component.text(if (root is ServerPlayer) root.name() else "console")

        val reason = Component.text(ctx.one(CommonCommandParameters.optionalMessage).orElse(banConfig.banMessage))


        val ban = Ban.builder().type(BanTypes.PROFILE).source(source).reason(reason)

        if (ctx.hasAny(CommonCommandParameters.duration)) {
            val startDate = Instant.now()
            val duration = ctx.requireOne(CommonCommandParameters.duration).toMillis()

            if (duration > banConfig.maxTempBan) {
                return CommandResult.error(Component.text("Maximum duration exceeded"))
            }

            val expiration = startDate.plusMillis(duration)

            ban.startDate(Instant.now()).expirationDate(expiration)
        }

        if (ctx.hasAny(CommonParameters.PLAYER)) {
            val player = ctx.requireOne(CommonParameters.PLAYER)

            if (cmd.commandFlags.isExempt(ctx, player)) {
                return CommandResult.error(Component.text("User is exempt"))
            }

            addBan(ban, player.profile())
            player.kick(reason)
            Notify.notifyPlayers(cmd.permission, Component.text("${player.name()} has been banned!"))
            return CommandResult.success()
        }

        if (root is Subject && !root.hasPermission("${cmd.permission}.offline")) {
            return CommandResult.error(Component.text("You do not have permission to ban offline users!"))
        }

        val id = CommonCommandParameters.uuidFromUserOrUuid(ctx)

        Sponge.server().gameProfileManager().profile(id).thenCompose {
            Notify.notifyPlayers(cmd.permission, Component.text("${it.name()} was banned"))
            addBan(ban, it)
        }.thenApply {
            return@thenApply CommandResult.success()
        }

        return CommandResult.error(Component.text("Something went wrong!"))
    }

    private fun addBan(ban: Ban.Builder, profile: GameProfile): CompletableFuture<Optional<out Ban>>? {
        return Sponge.server().serviceProvider().banService().add(ban.profile(profile).build())
    }

    private fun removeBan(profile: GameProfile): CompletableFuture<Boolean> {
        return Sponge.server().serviceProvider().banService().pardon(profile)
    }
}