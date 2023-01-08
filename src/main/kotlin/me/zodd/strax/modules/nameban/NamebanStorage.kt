package me.zodd.strax.modules.nameban

import me.zodd.strax.core.storage.StraxStorage
import me.zodd.strax.core.storage.StraxStorageException
import me.zodd.strax.core.utils.StraxConfigurationReference
import org.litote.kmongo.*

class NamebanStorage {
    private val namebans = StraxStorage.db.getCollection<Nameban>()
    private val namebanConfig = StraxConfigurationReference.straxConfig.modules.namebanConfig

    fun addEntry(entry: String, reason: String = namebanConfig.reason) {
        val oldList = getOrCreateDocument()
        namebans.updateOne(
            Nameban::bannedNameList eq oldList.bannedNameList,
            push(Nameban::bannedNameList, entry.lowercase() to reason)
        )
    }

    fun removeEntry(entry: String): Boolean {
        val oldList = getOrCreateDocument()
        val reason = oldList.bannedNameList.firstOrNull { it.first == entry }?.second ?: return false

        namebans.updateOne(
            Nameban::bannedNameList eq oldList.bannedNameList,
            pull(Nameban::bannedNameList, entry to reason)
        ).takeIf { it.wasAcknowledged() } ?: return false

        return true
    }

    fun getEntry(entry: String): Pair<String, String>? {
        return getOrCreateDocument().bannedNameList.firstOrNull { it.first.contentEquals(entry, true) }
    }

    private fun getOrCreateDocument(): Nameban {
        return getDocument() ?: insertDocument()
    }

    private fun insertDocument(): Nameban {
        namebans.insertOne(Nameban())
        return getDocument() ?: throw StraxStorageException("Failed to retrieve Namebans")
    }

    private fun getDocument(): Nameban? {
        return namebans.find().firstOrNull()
    }
}

data class Nameban(
    val bannedNameList: List<Pair<String, String>> = listOf()
)