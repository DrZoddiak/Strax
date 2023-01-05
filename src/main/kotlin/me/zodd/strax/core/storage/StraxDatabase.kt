package me.zodd.strax.core.storage

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.MongoException
import com.mongodb.client.MongoDatabase
import me.zodd.strax.core.utils.StraxConfigurationReference
import me.zodd.strax.modules.nickname.NicknameStorage
import org.bson.UuidRepresentation
import org.litote.kmongo.*
import org.litote.kmongo.util.KMongoJacksonFeature
import org.litote.kmongo.util.UpdateConfiguration
import java.util.*

object StraxStorage {

    private val conf = StraxConfigurationReference.straxConfig.storage

    private val username = conf.user
    private val password = conf.password
    private val url = conf.url

    private val connectionString =
        ConnectionString("mongodb+srv://$username:$password@$url")

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

