package me.zodd.strax.modules.core

import me.zodd.strax.core.storage.AbstractStorageObject
import me.zodd.strax.core.storage.StraxStorageException
import me.zodd.strax.modules.home.Homes
import me.zodd.strax.modules.home.PlayerHome
import me.zodd.strax.modules.nickname.Nickname
import org.litote.kmongo.eq
import java.util.*

class UserStorage : AbstractStorageObject() {
    fun findByNickname(name : String): User? {
        val usr = user.find().firstOrNull {
            it.nickname.literalName.contentEquals(name,true)
        }
        return usr
    }
    fun getOrInsert(id: UUID): User {
        return user.find(User::id eq id).first() ?: getOrThrow(id)
    }

    private fun getOrThrow(id: UUID): User {
        val acknowledged = user.insertOne(User(id)).wasAcknowledged()
        return if (acknowledged) {
            user.find(User::id eq id).first()
                ?: throw StraxStorageException("Failed to write data for $id")
        } else {
            throw StraxStorageException("Failed to write data for $id")
        }
    }
}
data class User(
    val id: UUID,
    val nickname: Nickname = Nickname(),
    val homes : List<PlayerHome> = Homes().homes,
)

