package me.zodd.strax.modules.fly

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class FlyConfig(
    val requirePermission : Boolean = false,
    val saveFlystate : Boolean = false,
)
