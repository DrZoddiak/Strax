package me.zodd.strax.modules.nickname

import com.google.auto.service.AutoService
import me.zodd.strax.core.StraxDeserializer
import me.zodd.strax.core.service.StraxListenerService
import org.spongepowered.api.data.Keys
import org.spongepowered.api.entity.living.player.server.ServerPlayer
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.filter.cause.Root
import org.spongepowered.api.event.network.ServerSideConnectionEvent

@AutoService(StraxListenerService::class)
class NicknameListener : StraxListenerService() {

    private val deserializer = StraxDeserializer

    @Listener
    fun playerJoinEvent(event: ServerSideConnectionEvent.Join, @Root player: ServerPlayer) {
        val nick = NicknameStorage(player.uniqueId()).moduleData.formattedName

        if (nick.isBlank()) return

        player.offer(Keys.CUSTOM_NAME, deserializer.minimessage.deserialize(nick))
    }

}