package me.zodd.strax.modules.core

import com.google.auto.service.AutoService
import me.zodd.strax.core.commands.AbstractStraxCommand
import me.zodd.strax.core.commands.StraxCommand
import me.zodd.strax.core.service.StraxCommandService
import net.kyori.adventure.text.Component
import org.spongepowered.api.command.Command
import org.spongepowered.api.command.CommandResult

@AutoService(StraxCommandService::class)
class CoreCommand : AbstractStraxCommand() {

    private val coreConfig = config

    private val straxBaseCommand = StraxCommand("strax").builder { cmd ->
        shortDescription(Component.text("Base command for the Strax plugin"))
        permission("${cmd.permission}.base")
        executor { context ->
            return@executor CommandResult.success()
        }
    }

    override val commandMap = mapOf(
        straxBaseCommand to arrayOf("strax")
    )
}