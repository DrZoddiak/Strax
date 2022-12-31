package me.zodd.strax.core.storage.database

import com.google.auto.service.AutoService
import me.zodd.strax.core.service.StraxStorageService
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

@AutoService(StraxStorageService::class)
class NicknameDatabase : StraxStorageService(Nicknames) {

    object Nicknames : IntIdTable() {
        val formattedNickname = varchar("formattedname", 256)
        val literalNickname = varchar("literalname", 256)
    }

    class Nickname(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<Nickname>(Nicknames)

        var formattedNickname by Nicknames.formattedNickname
        var literalNickname by Nicknames.literalNickname
        val user by UserDatabase.User referrersOn UserDatabase.Users.nickname
    }
}