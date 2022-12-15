package me.zodd.strax.core.storage

import me.zodd.strax.core.service.StraxStorageService
import me.zodd.strax.core.utils.StraxConfigurationReference
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object StraxStorage {
    val db by lazy {
        val config = StraxConfigurationReference.straxConfig.storage

        val url = config.url.ifEmpty { "Strax/strax" }

        val conn = Database.connect("jdbc:h2:./$url", "org.h2.Driver", config.user, config.password)

        transaction {
            ServiceLoader.load(StraxStorageService::class.java).forEach {
                SchemaUtils.create(it.table)
            }
            conn
        }
    }
}


