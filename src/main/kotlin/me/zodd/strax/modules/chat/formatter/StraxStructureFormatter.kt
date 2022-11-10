package me.zodd.strax.modules.chat.formatter

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import org.spongepowered.api.entity.living.player.server.ServerPlayer
import org.spongepowered.api.service.permission.Subject
import java.util.*

class StraxStructureFormatter(subject: Subject) : StraxFormatter(subject) {
    override fun format(
        player: ServerPlayer,
        target: Audience,
        message: Component,
        originalMessage: Component
    ): Optional<Component> {
        return Optional.of(message)
    }
}