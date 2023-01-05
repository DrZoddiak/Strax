package me.zodd.strax.core.storage

import me.zodd.strax.modules.core.User
import me.zodd.strax.modules.core.UserStorage
import org.litote.kmongo.eq
import org.litote.kmongo.updateOne
import java.util.UUID

abstract class AbstractModuleStorage<T : StorageSerializable>(private val id: UUID) : AbstractStorageObject() {
    abstract val moduleData: T

    abstract fun update(data: T)

    protected fun updateData(copy: User): User {
        user.updateOne(User::id eq id, copy)
        return copy
    }

    protected fun getUserData(): User {
        return UserStorage().getOrInsert(id)
    }
}