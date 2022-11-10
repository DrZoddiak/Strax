package me.zodd.strax.modules.chat

import com.google.auto.service.AutoService
import me.zodd.strax.core.PermissionOptions
import me.zodd.strax.core.service.StraxListenerService
import me.zodd.strax.modules.chat.formatter.StraxDefaultFormatter
import me.zodd.strax.modules.chat.processor.StraxChatProcessor
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.spongepowered.api.entity.living.player.server.ServerPlayer
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.Order
import org.spongepowered.api.event.filter.cause.Root
import org.spongepowered.api.event.message.PlayerChatEvent

@AutoService(StraxListenerService::class)
class ChatListener : StraxListenerService() {
    private val mm = MiniMessage.miniMessage()

    private val chatProcessor = StraxChatProcessor

    @Listener(order = Order.EARLY)
    fun onPlayerChatEarly(event: PlayerChatEvent, @Root player: ServerPlayer) {
        playerChatEvent(event, player)
    }

    @Listener(order = Order.LATE)
    fun onPlayerChatLate(event: PlayerChatEvent, @Root player: ServerPlayer) {
        //TODO : Check config for late or early formatting
    }

    private fun playerChatEvent(event: PlayerChatEvent, player: ServerPlayer) {
        val chatOptions = PermissionOptions.Chat.deserializableOptions.joinToString("") {
            player.option(it).orElse("")
        }

        //When deserializing a string without literal text it won't properly store a Components style
        val randomString = "Oranges"

        val defaultChatStyle = mm.deserialize("$chatOptions$randomString")
        val msg = chatProcessor.parseContent(player,event.message())

        event.setMessage(msg.applyFallbackStyle(defaultChatStyle.style()))
        event.setChatFormatter(StraxDefaultFormatter(player))
    }
}