package me.zodd.strax.modules.chat

import com.google.auto.service.AutoService
import me.zodd.strax.core.StraxDeserializer
import me.zodd.strax.core.service.StraxListenerService
import me.zodd.strax.modules.chat.formatter.StraxDefaultFormatter
import me.zodd.strax.modules.chat.processor.StraxChatProcessor
import org.spongepowered.api.entity.living.player.server.ServerPlayer
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.Order
import org.spongepowered.api.event.filter.cause.Root
import org.spongepowered.api.event.message.PlayerChatEvent

@AutoService(StraxListenerService::class)
class ChatListener : StraxListenerService() {

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
        event.setMessage(chatProcessor.parseContent(player,event.message()))
        event.setChatFormatter(StraxDefaultFormatter(player))
    }
}