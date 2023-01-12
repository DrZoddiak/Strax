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
    private val namebanStorage = NamebanStorage()

    @Listener
    fun playerJoinEvent(event: ServerSideConnectionEvent.Join, @Getter("player") player: ServerPlayer) {
        val name = player.name()

        //If entry isn't found, we don't kick.
        val reason = namebanStorage.getEntry(name)?.second ?: return
        player.kick(Component.text(reason))
    }
}