package me.zodd.strax.core.storage

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.MongoException
import com.mongodb.client.MongoDatabase
import me.zodd.strax.modules.core.User
import me.zodd.strax.modules.nickname.NicknameStorage
import org.bson.UuidRepresentation
import org.litote.kmongo.*
import org.litote.kmongo.util.KMongoJacksonFeature
import org.litote.kmongo.util.UpdateConfiguration
import java.util.*

object StraxStorage {

    val username = "drzodd"
    val password = "HHx3kt7hgX2WeM5"

    private val connectionString =
        ConnectionString("mongodb+srv://$username:$password@cluster0.jni3ihp.mongodb.net/?retryWrites=false&w=majority")

    private val settings = MongoClientSettings.builder()
        .uuidRepresentation(UuidRepresentation.STANDARD)
        .applyConnectionString(connectionString).build()

    val db: MongoDatabase by lazy {
        KMongoJacksonFeature.setUUIDRepresentation(UuidRepresentation.STANDARD)
        UpdateConfiguration.updateOnlyNotNullProperties

        KMongo.createClient(settings).getDatabase("strax_data")
    }
}

fun main() {
    val userID = UUID.fromString("78218344-5800-456e-800f-19b1d62769e1")

    val nickStore = NicknameStorage(userID)

    println("nick : ${nickStore.moduleData}")

    nickStore.update("<blue>Safety<yellow>Human")

    println("nick : ${nickStore.moduleData}")

    nickStore.update("<red>Safety<green>Human")

    println("nick : ${nickStore.moduleData}")

}




class StraxStorageException(msg: String) : MongoException(msg)


//USER : drzodd
//PASS: HHx3kt7hgX2WeM5

