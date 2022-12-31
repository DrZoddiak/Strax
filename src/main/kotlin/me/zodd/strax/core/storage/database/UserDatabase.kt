package me.zodd.strax.core.storage.database

import com.google.auto.service.AutoService
import me.zodd.strax.core.service.StraxStorageService
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

@AutoService(StraxStorageService::class)
class UserDatabase : StraxStorageService(Users) {
    object Users : IntIdTable() {
        val userId = uuid("user_uuid").uniqueIndex()

        val nickname = reference(
            "nickname",
            NicknameDatabase.Nicknames,
            ReferenceOption.CASCADE
        )
    }

    class User(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<User>(Users)

        var userId by Users.userId
        var nickRef by NicknameDatabase.Nickname referencedOn Users.nickname
    }
}