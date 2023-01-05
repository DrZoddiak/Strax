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
import java.net.URLEncoder

object StraxStorage {

    private val conf = StraxConfigurationReference.straxConfig.storage

    private val username = URLEncoder.encode(conf.user, "UTF-8")
    private val password = URLEncoder.encode(conf.password, "UTF-8")
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

class StraxStorageException(msg: String) : MongoException(msg)