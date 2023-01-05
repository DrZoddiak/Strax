package me.zodd.strax.modules.nickname

import com.google.auto.service.AutoService
import me.zodd.strax.core.StraxDeserializer
import me.zodd.strax.core.commands.AbstractStraxCommand
import me.zodd.strax.core.commands.StraxCommand
import me.zodd.strax.core.service.StraxCommandService
import me.zodd.strax.modules.core.UserStorage
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

    private val deserializer = StraxDeserializer

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

            val targetPlayer = cmd.targetPlayerOrRoot(ctx) ?: return@executor CommandResult.error(
                Component.text("Command must be run by a player or target another player!")
            )

            NicknameStorage(targetPlayer.uniqueId()).update(nickname)
            targetPlayer.offer(Keys.CUSTOM_NAME, deserializer.minimessage.deserialize(nickname))

            CommandResult.success()
        }
    }


    private val delnick = StraxCommand("delnick").builder { cmd ->
        shortDescription(Component.text("Remove a nickname from a player"))
        addParameters(
            CommonParameters.PLAYER_OPTIONAL
        )
        executor { ctx ->

            val targetPlayer = cmd.targetPlayerOrRoot(ctx) ?: return@executor CommandResult.error(
                Component.text("Command must be run by a player or target another player!")
            )

            NicknameStorage(targetPlayer.uniqueId()).update("")
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

            val uuid = UserStorage().findByNickname(msg)?.id ?: return@executor CommandResult.error(
                Component.text("No player by that name was found!")
            )

            //TODO: Allow offline-user lookup for usernames

            val player = Sponge.server().player(uuid).getOrNull() ?: return@executor CommandResult.error(
                Component.text("No player online by that name was found!")
            )

            root.sendMessage(Component.text("The players real name is ${player.name()}"))

            CommandResult.success()
        }
    }

    override val commandMap = mapOf(
        nickname to arrayOf("nickname", "nick"),
        delnick to arrayOf("delnick", "delnickname", "deletenick"),
        realname to arrayOf("realname")
    )
}