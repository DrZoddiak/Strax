package me.zodd.strax.modules.chat.formatter

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import org.spongepowered.api.entity.living.player.server.ServerPlayer
import org.spongepowered.api.service.permission.Subject
import java.util.*

class StraxDefaultFormatter(subject: Subject) : StraxFormatter(subject) {
    override fun format(
        player: ServerPlayer,
        target: Audience,
        message: Component,
        originalMessage: Component
    ): Optional<Component> {

        val name = player.displayName().get()

        val endResult = Component.text()
            .append(mm.deserialize(options.prefix))
            .append(name)
            .append(Component.space())
            .append(mm.deserialize(options.suffix))
            .append(Component.text(":"))
            .append(message)
            .build()

        return Optional.of(endResult)
    }
}
