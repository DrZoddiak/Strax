package me.zodd.strax.core.commands

import org.spongepowered.api.command.parameter.CommandContext
import org.spongepowered.api.command.parameter.managed.Flag
import org.spongepowered.api.service.permission.Subject

class CommonCommandFlags internal constructor(private val module: String) {
    val force: Flag = Flag.builder().aliases("force", "f").setPermission("strax.$module.force").build()

    val overwrite: Flag = Flag.builder().aliases("overwrite", "o").setPermission("strax.#$module.overwrite").build()

    fun isExempt(ctx: CommandContext, subject: Subject): Boolean {
        return if (!ctx.hasFlag(force)) {
            subject.hasPermission("strax.$module.exempt.target")
        } else false
    }
}