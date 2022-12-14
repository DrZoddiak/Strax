package me.zodd.strax.core.storage

import me.zodd.strax.core.service.StraxStorageService
import me.zodd.strax.modules.nickname.NicknameStorage
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random
import kotlin.streams.asSequence

object StraxStorage {

    val db by lazy {
        Database.connect("jdbc:h2:./strax/strax", "org.h2.Driver","","")

        transaction {
            addLogger(StdOutSqlLogger)

            ServiceLoader.load(StraxStorageService::class.java).forEach {
                SchemaUtils.create(it.table)
            }

            this
        }
    }




}


