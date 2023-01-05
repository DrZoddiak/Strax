package me.zodd.strax.modules.nameban

import com.google.auto.service.AutoService
import me.zodd.strax.core.service.StraxListenerService
import net.kyori.adventure.text.Component
import org.spongepowered.api.entity.living.player.server.ServerPlayer
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.filter.Getter
import org.spongepowered.api.event.network.ServerSideConnectionEvent

@AutoService(StraxListenerService::class)
class NamebanListener : StraxListenerService() {

    @Listener
    fun playerJoinEvent(event: ServerSideConnectionEvent.Join, @Getter("player") player: ServerPlayer) {
        val name = player.name()
        val namebanStorage = NamebanStorage()

        val reason = namebanStorage.getEntry(name)?.nameReasonMap?.second ?: return

        player.kick(Component.text(reason))
    }
}