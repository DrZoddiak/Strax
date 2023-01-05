package me.zodd.strax.core.storage

import me.zodd.strax.modules.core.User
import org.litote.kmongo.getCollection

abstract class AbstractStorageObject {
    protected val user = StraxStorage.db.getCollection<User>()
}
