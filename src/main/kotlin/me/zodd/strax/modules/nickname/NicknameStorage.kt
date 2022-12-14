package me.zodd.strax.modules.nickname

import me.zodd.strax.core.StraxDeserializer
import me.zodd.strax.core.storage.AbstractStraxStorage
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class NicknameStorage(uuid: UUID) : AbstractStraxStorage(uuid), StraxDeserializer {
    fun updateNick(nickname: String) = transaction {
        val ref = userStorage.getOrCreateUser().nickRef
        ref.formattedNickname = nickname
        ref.literalNickname = minimessage.stripTags(nickname)
        ref
    }
}
