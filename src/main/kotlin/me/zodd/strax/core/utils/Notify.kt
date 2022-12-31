package me.zodd.strax.core.utils

import net.kyori.adventure.text.TextComponent
import org.spongepowered.api.Sponge

object Notify {
    fun notifyPlayers(permission : String, message : TextComponent) {
        Sponge.server().onlinePlayers()
            .filter { it.hasPermission("$permission.notify") }
            .forEach { it.sendMessage(message) }
    }
}