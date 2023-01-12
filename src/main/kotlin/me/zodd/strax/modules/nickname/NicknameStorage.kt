package me.zodd.strax.modules.nickname

import me.zodd.strax.core.storage.AbstractUserModuleStorage
import net.kyori.adventure.text.minimessage.MiniMessage
import org.litote.kmongo.setValue
import java.util.UUID

class NicknameStorage(id: UUID) : AbstractUserModuleStorage(id) {

    fun updateNickname(nickname: String) {
        user.updateOne(
            userFilter,
            setValue(Nickname::formattedName, nickname)
        )
    }

    fun getNickname(): Nickname {
        return getUserData().nickname
    }

}

data class Nickname(val formattedName: String = "") {
    val literalName = MiniMessage.miniMessage().stripTags(formattedName)

    override fun toString(): String {
        return "Nickname(formattedName=$formattedName,literalName=$literalName)"
    }
}