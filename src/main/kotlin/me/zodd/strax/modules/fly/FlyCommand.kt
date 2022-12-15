package me.zodd.strax.modules.fly

import com.google.auto.service.AutoService
import me.zodd.strax.core.commands.AbstractStraxCommand
import me.zodd.strax.core.commands.StraxCommand
import me.zodd.strax.core.service.StraxCommandService
import net.kyori.adventure.text.Component
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.parameter.CommonParameters
import org.spongepowered.api.data.Keys

@AutoService(StraxCommandService::class)
class FlyCommand : AbstractStraxCommand() {

    private val fly = StraxCommand("fly").builder { cmd ->
        shortDescription(Component.text("Allow a player to fly"))
        addParameters(
            CommonParameters.PLAYER_OPTIONAL
        )
        executor { ctx ->
            val targetPlayer = cmd.targetPlayer(ctx) ?: return@executor CommandResult.error(
                Component.text("Command must be run by a player or target another player!")
            )

            val canFly = targetPlayer.get(Keys.CAN_FLY).orElse(false)
            val isFlying = targetPlayer.get(Keys.IS_FLYING).orElse(false)

            if (isFlying && canFly) {
                targetPlayer.offer(Keys.IS_FLYING, false)
            }

            targetPlayer.offer(Keys.CAN_FLY, !canFly).ifSuccessful {
                targetPlayer.sendMessage(Component.text("You can ${if (canFly) "no longer" else "now"} fly"))
            }

            CommandResult.success()
        }
    }


    override val commandMap = mapOf(fly to arrayOf("fly"))
}

