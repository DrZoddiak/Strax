package me.zodd.strax.modules.home

import me.zodd.strax.core.storage.AbstractUserModuleStorage
import org.litote.kmongo.pull
import org.litote.kmongo.push
import org.spongepowered.api.ResourceKey
import org.spongepowered.api.Sponge
import org.spongepowered.api.world.server.ServerLocation
import org.spongepowered.math.vector.Vector3d
import java.util.UUID

class HomeStorage(id: UUID) : AbstractUserModuleStorage(id) {
    private fun addEntry(home: Pair<String, SerializableServerLocation>, overwrite: Boolean = false) {
        if (overwrite) {
            removeEntry(home.first)
        }

        user.updateOne(
            userFilter,
            push(Homes::homes, PlayerHome(home))
        )
    }

    fun addEntry(name: String, location: ServerLocation, overwrite: Boolean = false) {
        addEntry(name to location.serialize(), overwrite)
    }

    fun removeEntry(home: String): Boolean {
        val entry = getEntry(home)
        user.updateOne(
            userFilter,
            pull(Homes::homes, entry)
        ).takeIf { it.wasAcknowledged() } ?: return false
        return true
    }

    fun getEntry(home: String): PlayerHome? {
        return getHomes().firstOrNull { it.home.first == home }
    }

    fun getHomes(): List<PlayerHome> {
        return getUserData().homes
    }

    private fun ServerLocation.serialize(): SerializableServerLocation {
        return SerializableServerLocation(
            this.worldKey().formatted(),
            this.x(),
            this.y(),
            this.z()
        )
    }
}

data class Homes(
    val homes: List<PlayerHome> = listOf()
)

data class PlayerHome(
    val home: Pair<String, SerializableServerLocation> = Pair("", SerializableServerLocation())
) {
    override fun toString(): String {
        return home.first
    }
}

data class SerializableServerLocation(
    val worldKey: String = "null",
    val x: Double = 0.0,
    val y: Double = 0.0,
    val z: Double = 0.0,
) {
    fun asServerLocation(): ServerLocation? {
        val world = Sponge.server().worldManager().world(ResourceKey.resolve(worldKey)).get()
        val location = Vector3d.from(x, y, z)
        return ServerLocation.of(world, location)
    }
}

