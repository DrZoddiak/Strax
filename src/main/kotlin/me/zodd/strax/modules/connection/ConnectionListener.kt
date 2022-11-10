package me.zodd.strax.modules.connection

import com.google.auto.service.AutoService
import me.zodd.strax.core.service.StraxListenerService
import net.kyori.adventure.text.Component
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.Order
import org.spongepowered.api.event.filter.Getter
import org.spongepowered.api.event.filter.IsCancelled
import org.spongepowered.api.event.network.ServerSideConnectionEvent
import org.spongepowered.api.profile.GameProfile
import org.spongepowered.api.service.ban.Ban
import org.spongepowered.api.service.ban.Ban.IP
import org.spongepowered.api.service.whitelist.WhitelistService
import org.spongepowered.api.util.Tristate
import java.util.*

@AutoService(StraxListenerService::class)
class ConnectionListener : StraxListenerService() {

    private val connectionConfig = config.connectionConfig

    @Listener(order = Order.FIRST)
    @IsCancelled(Tristate.TRUE)
    fun onPlayerConnect(
        event: ServerSideConnectionEvent.Login,
        @Getter("user") user: User,
        @Getter("profile") profile: GameProfile
    ) {

        val banService = Sponge.server().serviceProvider().banService()
        val profileBan: Optional<Ban.Profile> = banService.find(profile).join()
        if (profileBan.isPresent) {
            return
        }

        val ipBan: Optional<IP> = banService.find(event.connection().address().address).join()
        if (ipBan.isPresent) {
            return
        }

        val whitelistService: WhitelistService = Sponge.server().serviceProvider().whitelistService()
        val whitelistEnabled = Sponge.server().isWhitelistEnabled

        if (whitelistEnabled && !whitelistService.isWhitelisted(profile).join()) {
            if (connectionConfig.whitelistMessage.isNotEmpty()) {
                event.setMessage(Component.text(connectionConfig.whitelistMessage))
                event.isCancelled = true
            }
            return
        }

        val slotsLeft = Sponge.server().maxPlayers() - Sponge.server().onlinePlayers().size
        if (slotsLeft <= 0) {
            if (user.hasPermission(ConnectionPermissions.fullserver)) {
                if (connectionConfig.reservedSlots <= -1 || -slotsLeft < connectionConfig.reservedSlots) {
                    event.isCancelled = false
                    return
                }
            }

            if (connectionConfig.serverFullMessage.isNotEmpty()) {
                event.setMessage(Component.text(connectionConfig.serverFullMessage))
            }
        }
    }
}