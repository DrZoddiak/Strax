package me.zodd.strax.modules.core

import me.zodd.strax.core.storage.NicknameDatabase
import me.zodd.strax.core.storage.UserDatabase
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class StraxUserStorage(private val uuid: UUID) {

    fun getOrCreateUser(): UserDatabase.User = transaction {
        UserDatabase.User.find {
            UserDatabase.Users.userId eq uuid
        }.firstOrNull() ?: createNew()
    }

    private fun createNew() = transaction {
        val nickRef = NicknameDatabase.Nickname.new {
            formattedNickname = ""
            literalNickname = ""
        }
        UserDatabase.User.new {
            userId = uuid
            this.nickRef = nickRef
        }
    }
}