package me.zodd.strax.modules.nickname

import com.google.auto.service.AutoService
import me.zodd.strax.core.StraxDeserializer
import me.zodd.strax.core.service.StraxListenerService
import me.zodd.strax.modules.core.StraxUserStorage
import org.jetbrains.exposed.sql.transactions.transaction
import org.spongepowered.api.data.Keys
import org.spongepowered.api.entity.living.player.server.ServerPlayer
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.filter.cause.Root
import org.spongepowered.api.event.network.ServerSideConnectionEvent

@AutoService(StraxListenerService::class)
class NicknameListener : StraxListenerService(), StraxDeserializer {

    @Listener
    fun playerJoinEvent(event: ServerSideConnectionEvent.Join, @Root player: ServerPlayer) {
        val nick = transaction {
            StraxUserStorage(player.uniqueId()).getOrCreateUser().nickRef.formattedNickname
        }

        if (nick.isBlank()) return

        player.offer(Keys.CUSTOM_NAME, minimessage.deserialize(nick))
    }

}