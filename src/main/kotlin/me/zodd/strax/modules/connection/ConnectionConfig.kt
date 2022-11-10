package me.zodd.strax.modules.connection

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment
import org.spongepowered.configurate.objectmapping.meta.Setting

@ConfigSerializable
data class ConnectionConfig(
    @field:Setting("reserved-slots")
    @field:Comment("Maximum reserved slots to be used. Set to -1 for unlimited")
    val reservedSlots : Int = -1,
    @field:Setting("whitelist-message")
    @field:Comment("Message to send to players if the server is whitelisted")
    val whitelistMessage : String = "",
    @field:Setting("server-full-message")
    @field:Comment("Message to send to players when the server is full")
    val serverFullMessage : String = "",
)