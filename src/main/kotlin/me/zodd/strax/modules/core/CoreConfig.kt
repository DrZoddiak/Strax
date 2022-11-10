package me.zodd.strax.modules.core

import me.zodd.strax.modules.ban.BanConfig
import me.zodd.strax.modules.chat.ChatConfig
import me.zodd.strax.modules.connection.ConnectionConfig
import me.zodd.strax.modules.kick.KickConfig
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class CoreConfig(
    val modules: Modules = Modules(),
)

@ConfigSerializable
data class Modules(
    val banConfig: BanConfig = BanConfig(),
    val chatConfig : ChatConfig = ChatConfig(),
    val kickConfig: KickConfig = KickConfig(),
    val connectionConfig : ConnectionConfig = ConnectionConfig(),
)
