package me.zodd.strax.modules.home

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class HomeConfig(
    val requireSameDimension: Boolean = false,
    val allowHomeCountOverhang: Boolean = false,
    val respawnAtHome: Boolean = false,
    val useSafeWarp: Boolean = true,
)