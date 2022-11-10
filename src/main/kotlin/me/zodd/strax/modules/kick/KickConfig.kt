package me.zodd.strax.modules.kick

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Setting

@ConfigSerializable
data class KickConfig(
    @field:Setting("default-kick-message")
    val kickMessage: String = "You were kicked!",
)