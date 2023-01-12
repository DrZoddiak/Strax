package me.zodd.strax.modules.home

import com.google.auto.service.AutoService
import me.zodd.strax.core.PermissionOptions
import me.zodd.strax.core.PermissionOptions.optionOrEmpty
import me.zodd.strax.core.commands.AbstractStraxCommand
import me.zodd.strax.core.commands.CommonCommandFlags
import me.zodd.strax.core.commands.CommonCommandParameters
import me.zodd.strax.core.commands.StraxCommand
import me.zodd.strax.core.service.StraxCommandService
import net.kyori.adventure.text.Component
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.parameter.CommonParameters
import org.spongepowered.api.entity.living.player.server.ServerPlayer
import org.spongepowered.api.world.server.ServerLocation
import java.util.UUID

@AutoService(StraxCommandService::class)
class HomeCommand : AbstractStraxCommand() {

    private val homedelete = StraxCommand("homedelete").builder { cmd ->
        shortDescription(Component.text("Deletes a personal warp"))
        addParameters(
            CommonCommandParameters.nameParameter
        )
        executor { ctx ->
            val targetPlayer = cmd.rootPlayer(ctx) ?: return@executor requirePlayer()
            val homeName = ctx.requireOne(CommonCommandParameters.nameParameter)

            removeHome(targetPlayer.uniqueId(), homeName)
        }
    }

    private val deleteother = StraxCommand("deleteother").builder { cmd ->
        shortDescription(Component.text("Deletes the home of another player"))
        addParameters(
            CommonParameters.PLAYER,
            CommonCommandParameters.nameParameter
        )
        executor { ctx ->
            val targetPlayer = cmd.targetPlayer(ctx) ?: return@executor requirePlayer()
            val homeName = ctx.requireOne(CommonCommandParameters.nameParameter)

            removeHome(targetPlayer.uniqueId(), homeName)
        }
    }

    private fun removeHome(uuid: UUID, name: String): CommandResult {
        val acknowledged = HomeStorage(uuid).removeEntry(name)

        return if (acknowledged) {
            CommandResult.success()
        } else {
            CommandResult.error(Component.text("Failed to delete home $name"))
        }
    }

    private val homelimit = StraxCommand("homelimit").builder { cmd ->
        shortDescription(Component.text("Checks the limit of homes a player can set"))
        addParameters(
            CommonParameters.PLAYER_OPTIONAL
        )
        executor { ctx ->
            val root = cmd.rootPlayer(ctx) ?: return@executor requirePlayer()
            val targetPlayer = cmd.targetPlayerOrRoot(ctx) ?: return@executor requirePlayer()


            val limit = if (targetPlayer.hasPermission("strax.home.set.unlimited")) {
                "unlimited"
            } else {
                targetPlayer.optionOrEmpty(PermissionOptions.Home.limit)
            }

            root.sendMessage(Component.text("Users home limit is $limit"))

            return@executor CommandResult.success()
        }

    }

    private val homelist = StraxCommand("homelist").builder { cmd ->
        shortDescription(Component.text("Lists the home a player has"))
        addParameters(CommonParameters.PLAYER_OPTIONAL)
        executor { ctx ->
            val rootPlayer = cmd.rootPlayer(ctx) ?: return@executor requirePlayer()
            val targetPlayer = cmd.targetPlayerOrRoot(ctx) ?: return@executor requirePlayer()

            val homes = HomeStorage(targetPlayer.uniqueId()).getHomes()

            rootPlayer.sendMessage(Component.text(homes.joinToString()))

            CommandResult.success()
        }
    }

    private val sethome = StraxCommand("sethome").builder { cmd ->
        shortDescription(Component.text("Sets a players home"))
        addFlag(CommonCommandFlags("home").overwrite)
        addParameters(CommonCommandParameters.nameParameter)
        executor { ctx ->
            val rootPlayer = cmd.rootPlayer(ctx) ?: return@executor CommandResult.error(
                Component.text("Command must be run by a player!")
            )
            val location = rootPlayer.serverLocation()
            val locationName = ctx.requireOne(CommonCommandParameters.nameParameter)

            HomeStorage(rootPlayer.uniqueId()).addEntry(locationName, location)

            CommandResult.success()
        }

    }

    private val homeother = StraxCommand("homeother").builder { cmd ->
        shortDescription(Component.text("Teleports to another players home"))
        addParameters(CommonParameters.PLAYER)
        executor { ctx ->
            val rootPlayer = cmd.targetPlayer(ctx) ?: return@executor requirePlayer()
            val homeName = ctx.one(CommonCommandParameters.optionalNameParameter).orElse("home")

            val entry = HomeStorage(rootPlayer.uniqueId()).getEntry(homeName) ?: return@executor CommandResult.error(
                Component.text("Unable to find home with name $homeName"))
            val location = entry.home.second.asServerLocation() ?: return@executor CommandResult.error(
                Component.text("Error retrieving location")
            )

            teleportToHome(rootPlayer,location)

            return@executor CommandResult.success()
        }

    }

    private val home = StraxCommand("home").builder { cmd ->
        shortDescription(Component.text("A personal warp for a player"))
        addParameters(CommonCommandParameters.optionalNameParameter)
        addChildren(
            mapOf(
                mutableListOf("delete","del") to homedelete,
                mutableListOf("deleteother", "delother") to deleteother,
                mutableListOf("limit") to homelimit,
                mutableListOf("homelist") to homelist,
                mutableListOf("set","homeset","sethome") to sethome,
                mutableListOf("homeother") to homeother,
            )
        )
        executor { ctx ->
            val rootPlayer = cmd.rootPlayer(ctx) ?: return@executor requirePlayer()
            val homeName = ctx.one(CommonCommandParameters.optionalNameParameter).orElse("home")

            val entry = HomeStorage(rootPlayer.uniqueId()).getEntry(homeName) ?: return@executor CommandResult.error(
                Component.text("Unable to find home with name $homeName"))
            val location = entry.home.second.asServerLocation() ?: return@executor CommandResult.error(
                Component.text("Error retrieving location")
            )

            teleportToHome(rootPlayer,location)

            return@executor CommandResult.success()
        }
    }

    private fun teleportToHome(player : ServerPlayer, location : ServerLocation): Boolean {
        return player.setLocation(location)
    }

    private fun requirePlayer(): CommandResult {
        return CommandResult.error(Component.text("Must be run by a player!"))
    }

    override val commandMap = mapOf(
        home to arrayOf("home"),
        homedelete to arrayOf("deletehome","delhome"),
        deleteother to arrayOf("deletehomeother","delhomeother"),
        homelist to arrayOf("homes","listhomes"),
        homeother to arrayOf("homeother"),
    )
}
