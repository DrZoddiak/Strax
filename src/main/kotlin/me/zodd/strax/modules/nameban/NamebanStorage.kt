package me.zodd.strax.modules.nameban

import me.zodd.strax.core.storage.StraxStorage
import me.zodd.strax.core.utils.StraxConfigurationReference
import org.litote.kmongo.eq
import org.litote.kmongo.getCollection

class NamebanStorage {
    private val namebans = StraxStorage.db.getCollection<Nameban>()
    private val namebanConfig = StraxConfigurationReference.straxConfig.modules.namebanConfig

    fun addEntry(entry: String, reason: String = namebanConfig.reason) {
        namebans.insertOne(Nameban(entry to reason))
    }

    fun removeEntry(entry: String) {
        namebans.deleteOne(Nameban::nameReasonMap::name eq entry)
    }

    fun getEntry(entry: String): Nameban? {
        return namebans.find(Nameban::nameReasonMap::name eq entry).firstOrNull()
    }
}

data class Nameban(
    val nameReasonMap: Pair<String, String> = "" to ""
)