package me.zodd.strax.core.commands

import me.zodd.strax.core.StraxDeserializer
import me.zodd.strax.core.service.StraxCommandService
import me.zodd.strax.core.utils.StraxConfigurationReference
import org.spongepowered.api.command.Command

abstract class AbstractStraxCommand : StraxCommandService(), StraxDeserializer {
    val config = StraxConfigurationReference.straxConfig
}

data class StraxCommand(
    val name : String,
) {
    val permission = "strax.$name"
    val commandFlags = CommonCommandFlags(name)

    fun builder(builder : Command.Builder.(StraxCommand) -> Unit) : Command.Parameterized {
        val cmd = Command.builder()
        cmd.builder(this)
        return cmd.build()
    }
}