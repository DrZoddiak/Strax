package me.zodd.strax.modules.ban

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Setting

@ConfigSerializable
class BanConfig {
    @Setting("default-ban-message")
    val banMessage = "You have been banned"

    @Setting("maximum-tempban-length")
    val maxTempBan = 604800
}