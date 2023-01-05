package me.zodd.strax.modules.core

import me.zodd.strax.modules.ban.BanConfig
import me.zodd.strax.modules.chat.ChatConfig
import me.zodd.strax.modules.connection.ConnectionConfig
import me.zodd.strax.modules.fly.FlyConfig
import me.zodd.strax.modules.kick.KickConfig
import me.zodd.strax.modules.nameban.NamebanConfig
import me.zodd.strax.modules.nickname.NicknameConfig
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class CoreConfig(
    val modules: Modules = Modules(),
    val storage: StorageDetails = StorageDetails(),
)

@ConfigSerializable
data class Modules(
    val banConfig: BanConfig = BanConfig(),
    val chatConfig: ChatConfig = ChatConfig(),
    val kickConfig: KickConfig = KickConfig(),
    val connectionConfig: ConnectionConfig = ConnectionConfig(),
    val nicknameConfig: NicknameConfig = NicknameConfig(),
    val flyConfig: FlyConfig = FlyConfig(),
    val namebanConfig : NamebanConfig = NamebanConfig(),

)

@ConfigSerializable
data class StorageDetails(
    val url: String = "",
    val user: String = "",
    val password: String = "",
)
