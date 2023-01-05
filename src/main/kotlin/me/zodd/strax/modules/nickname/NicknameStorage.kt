package me.zodd.strax.modules.nickname

import me.zodd.strax.core.storage.AbstractModuleStorage
import me.zodd.strax.core.storage.StorageSerializable
import net.kyori.adventure.text.minimessage.MiniMessage
import java.util.UUID

class NicknameStorage(id: UUID) : AbstractModuleStorage<Nickname>(id) {

    override val moduleData get() = getUserData().nickname
    fun update(nickname: String) = update(Nickname(nickname))

    override fun update(data: Nickname) {
        val copy = getUserData().copy(nickname = data)
        updateData(copy)
    }
}

data class Nickname(val formattedName: String = "") : StorageSerializable {
    val literalName = MiniMessage.miniMessage().stripTags(formattedName)

    override fun toString(): String {
        return "Nickname(formattedName=$formattedName,literalName=$literalName)"
    }
}