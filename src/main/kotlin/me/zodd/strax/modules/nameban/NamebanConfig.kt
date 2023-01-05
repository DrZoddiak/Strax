package me.zodd.strax.modules.nameban

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class NamebanConfig(
    val reason: String =
        "Your Minecraft username is not appropriate for this server. Please change it before attempting to access this server."
)