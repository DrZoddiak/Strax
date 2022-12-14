package me.zodd.strax.core.storage

import me.zodd.strax.modules.core.StraxUserStorage
import java.util.UUID

abstract class AbstractStraxStorage(private val uuid: UUID) {
    val userStorage
        get() = StraxUserStorage(uuid)
}