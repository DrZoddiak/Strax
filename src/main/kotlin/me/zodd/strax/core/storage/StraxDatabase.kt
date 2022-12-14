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


fun main() {
    StraxStorage.db

    transaction {
        addLogger(StdOutSqlLogger)

        generateEntries(1000)
    }
}

fun generateEntries(int: Int) {
    for (i in int downTo 1) {
        val uuid = UUID.randomUUID()
        NicknameStorage(uuid).updateNick(randomName())
    }
}

val tagList = listOf(
    "<red>",
    "<yellow>",
    "<green>",
    "<blue>",
    "<gray>",
    "<black>",
    "<white>",
)

fun randomName(): String {
    val charPool: List<Char> = ('a'..'z') + ('A'..'Z')

    return ThreadLocalRandom.current()
        .ints(Random.nextLong(3, 64), 0, charPool.size)
        .asSequence()
        .map(charPool::get)
        .joinToString("")
        .replace("z", tagList.random(), true)

}
