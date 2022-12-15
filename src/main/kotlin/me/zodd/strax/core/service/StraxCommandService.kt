package me.zodd.strax.core.service

import org.spongepowered.api.command.Command

abstract class StraxCommandService : AbstractStraxService() {
    abstract val commandMap : Map<Command.Parameterized, Array<String>>
}