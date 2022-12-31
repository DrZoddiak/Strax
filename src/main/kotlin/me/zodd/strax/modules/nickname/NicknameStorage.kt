package me.zodd.strax.modules.nickname

import me.zodd.strax.core.StraxDeserializer
import me.zodd.strax.core.storage.database.AbstractStraxStorage
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class NicknameStorage(uuid: UUID) : AbstractStraxStorage(uuid) {
    fun updateNick(nickname: String) = transaction {
        val ref = userStorage.getOrCreateUser().nickRef
        ref.formattedNickname = nickname
        ref.literalNickname = StraxDeserializer.minimessage.stripTags(nickname)
        ref
    }
}
