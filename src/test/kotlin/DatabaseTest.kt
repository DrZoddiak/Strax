import me.zodd.strax.core.service.StraxStorageService
import me.zodd.strax.core.storage.StraxStorage
import me.zodd.strax.core.storage.UserDatabase
import me.zodd.strax.modules.nickname.NicknameStorage
import net.kyori.adventure.text.minimessage.MiniMessage
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.testng.annotations.AfterMethod
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random
import kotlin.streams.asSequence
import kotlin.test.*

class DatabaseTest {

    init {
        Database.connect("jdbc:h2:./strax/straxTest", "org.h2.Driver", "", "")

        transaction {
            addLogger(StdOutSqlLogger)

            ServiceLoader.load(StraxStorageService::class.java).forEach {
                SchemaUtils.create(it.table)
            }

            this
        }
    }

    @Test
    fun generateEntries() {
        transaction {
            val entriesGenerated = 1000
            for (i in entriesGenerated downTo 1) {
                val uuid = UUID.randomUUID()
                NicknameStorage(uuid).updateNick(randomName())
            }

            val dbSize = UserDatabase.User.all().count()
            assert(dbSize.toInt() == entriesGenerated)
        }
    }

    @Test(dependsOnMethods = ["generateEntries"], description = "Confirm tags are stripped properly")
    fun verifyNicknameValues() {
        transaction {
            UserDatabase.User.all().forEach {
                val format = it.nickRef.formattedNickname
                val literal = it.nickRef.literalNickname

                val stripped = MiniMessage.miniMessage().stripTags(format)

                assert(literal.contentEquals(stripped))
            }
        }
    }

    @AfterTest
    fun clearDatabase() {
        transaction {
            UserDatabase.User.all().forEach {
                it.delete()
            }
        }
    }


    private fun randomName(): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z')

        val tagList = listOf(
            "<red>",
            "<yellow>",
            "<green>",
            "<blue>",
            "<gray>",
            "<black>",
            "<white>",
        )

        /*
        *   This generates a random sequence of letters
        *   It then replaces any 'z' chars with a random color format
         */
        return ThreadLocalRandom.current()
            .ints(Random.nextLong(3, 64), 0, charPool.size)
            .asSequence()
            .map(charPool::get)
            .joinToString("")
            .replace("z", tagList.random(), true)

    }
}