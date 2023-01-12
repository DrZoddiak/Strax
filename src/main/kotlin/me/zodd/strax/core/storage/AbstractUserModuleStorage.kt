package me.zodd.strax.core.storage

import me.zodd.strax.modules.core.User
import me.zodd.strax.modules.core.UserStorage
import org.litote.kmongo.eq
import java.util.UUID

abstract class AbstractUserModuleStorage(private val id: UUID) : AbstractStorageObject() {
    val userFilter = User::id eq id

    protected fun getUserData(): User {
        return UserStorage().getOrInsert(id)
    }
}