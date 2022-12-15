package me.zodd.strax.modules.nickname

import com.google.auto.service.AutoService
import me.zodd.strax.core.commands.AbstractStraxCommand
import me.zodd.strax.core.commands.StraxCommand
import me.zodd.strax.core.service.StraxCommandService
import me.zodd.strax.core.storage.NicknameDatabase
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.parameter.CommandContext
import org.spongepowered.api.command.parameter.CommonParameters
import org.spongepowered.api.data.Keys
import org.spongepowered.api.entity.living.player.server.ServerPlayer
import org.spongepowered.api.service.permission.Subject
import java.util.*
import kotlin.jvm.optionals.getOrNull

@AutoService(StraxCommandService::class)
class NicknameCommand : AbstractStraxCommand() {

    private val nicknameConfig = config.modules.nicknameConfig

    private val nickname = StraxCommand("nickname").builder { cmd ->
        shortDescription(Component.text("Add a nickname to a player"))
        addParameters(
            CommonParameters.PLAYER_OPTIONAL, CommonParameters.MESSAGE
        )
        executor { ctx ->

            val nickname = ctx.requireOne(CommonParameters.MESSAGE)

            if (!nicknameConfig.validNickname(nickname)) {
                return@executor CommandResult.error(Component.text("Invalid nickname"))
            }

            val targetPlayer = targetPlayer(ctx) ?: return@executor CommandResult.error(
                Component.text("Command must be run by a player or target another player!")
            )

            NicknameStorage(targetPlayer.uniqueId()).updateNick(nickname)
            targetPlayer.offer(Keys.CUSTOM_NAME, minimessage.deserialize(nickname))

            CommandResult.success()
        }
    }


    private val delnick = StraxCommand("delnick").builder { cmd ->
        shortDescription(Component.text("Remove a nickname from a player"))
        addParameters(
            CommonParameters.PLAYER_OPTIONAL
        )
        executor { ctx ->

            val targetPlayer = targetPlayer(ctx) ?: return@executor CommandResult.error(
                Component.text("Command must be run by a player or target another player!")
            )

            NicknameStorage(targetPlayer.uniqueId()).updateNick("")
            targetPlayer.remove(Keys.CUSTOM_NAME)

            CommandResult.success()
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private val realname = StraxCommand("realname").builder { cmd ->
        shortDescription(Component.text("Find the real name of a player"))
        addParameters(
            CommonParameters.MESSAGE
        )
        executor { ctx ->

            val root = ctx.cause().root() as Audience

            val msg = ctx.requireOne(CommonParameters.MESSAGE)

            val uuid = NicknameDatabase.Nickname.find {
                NicknameDatabase.Nicknames.literalNickname eq msg
            }.firstOrNull()?.user?.first()?.userId

            val player = Sponge.server().player(uuid).getOrNull()

            val name = player?.name() ?: return@executor CommandResult.error(
                Component.text("No player by that name was found!")
            )

            root.sendMessage(Component.text("The players real name is $name"))

            CommandResult.success()
        }
    }

    private fun targetPlayer(ctx: CommandContext): ServerPlayer? {
        val root = ctx.cause().root() as Subject
        return if (root.hasPermission(NicknamePermissions.nickTargetOthers) && ctx.hasAny(CommonParameters.PLAYER_OPTIONAL)) {
            ctx.requireOne(CommonParameters.PLAYER_OPTIONAL)
        } else {
            if (ctx.cause().root() is ServerPlayer) {
                ctx.cause().root() as ServerPlayer
            } else {
                null
            }
        }
    }

    override val commandMap = mapOf(
        nickname to arrayOf("nickname", "nick"),
        delnick to arrayOf("delnick", "delnickname", "deletenick"),
        realname to arrayOf("realname")
    )
}