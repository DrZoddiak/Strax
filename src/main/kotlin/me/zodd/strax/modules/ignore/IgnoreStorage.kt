package me.zodd.strax.modules.ignore


import me.zodd.strax.core.storage.AbstractModuleStorage
import me.zodd.strax.core.storage.StorageSerializable
import java.util.UUID

class IgnoreStorage(id: UUID) : AbstractModuleStorage<Ignore>(id) {

    override val moduleData get() = getUserData().ignoreList
    fun addEntry(entry: UUID) {
        val updatedList = moduleData.ignoredUsers.toMutableList()
        updatedList.add(entry)
        update(Ignore(updatedList.toList()))
    }

    fun removeEntry(entry: UUID) {
        val updatedList = moduleData.ignoredUsers.toMutableList()
        if(updatedList.remove(entry))
        update(Ignore(updatedList.toList()))
    }

    override fun update(data: Ignore) {
        val copy = getUserData().copy(ignoreList = data)
        updateData(copy)
    }
}
data class Ignore(val ignoredUsers: List<UUID> = listOf()) : StorageSerializable
