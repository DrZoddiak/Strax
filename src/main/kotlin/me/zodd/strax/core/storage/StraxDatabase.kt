package me.zodd.strax.core.storage

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.MongoException
import com.mongodb.client.MongoDatabase
import me.zodd.strax.core.utils.StraxConfigurationReference
import org.bson.UuidRepresentation
import org.litote.kmongo.*
import org.litote.kmongo.util.KMongoJacksonFeature
import org.litote.kmongo.util.UpdateConfiguration

object StraxStorage {

    private val url = StraxConfigurationReference.straxConfig.storage.mongodbConnection.takeIf {
        it.isNotBlank()
    } ?: throw StraxStorageException("Connection String is Blank!")

    private val connectionString = ConnectionString(url)

    private val settings = MongoClientSettings.builder()
        .uuidRepresentation(UuidRepresentation.STANDARD)
        .applyConnectionString(connectionString)
        .build()

    val db: MongoDatabase by lazy {
        KMongoJacksonFeature.setUUIDRepresentation(UuidRepresentation.STANDARD)
        UpdateConfiguration.updateOnlyNotNullProperties

        KMongo.createClient(settings).getDatabase("strax_data")
    }
}

class StraxStorageException(msg: String) : MongoException(msg)