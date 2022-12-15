package me.zodd.strax.modules.fly

import com.google.auto.service.AutoService
import me.zodd.strax.core.service.StraxListenerService
import org.spongepowered.api.entity.living.player.server.ServerPlayer
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.filter.Getter
import org.spongepowered.api.event.network.ServerSideConnectionEvent

@AutoService(StraxListenerService::class)
class FlyListener : StraxListenerService() {

    @Listener
    fun playerJoinEvent(event: ServerSideConnectionEvent.Join, @Getter("player") player: ServerPlayer) {

        //TODO: Assign fly when player joins if they had it before logging out
    }

}
