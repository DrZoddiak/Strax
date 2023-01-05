package me.zodd.strax

import com.google.inject.Inject;
import me.zodd.strax.core.service.StraxCommandService
import me.zodd.strax.core.service.StraxListenerService
import me.zodd.strax.core.storage.StraxStorage
import me.zodd.strax.core.utils.StraxConfigurationReference
import me.zodd.strax.modules.core.CoreConfig
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.Command;
import org.spongepowered.api.config.DefaultConfig
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.ConfigurateException
import org.spongepowered.configurate.reference.ConfigurationReference
import org.spongepowered.plugin.PluginContainer;
import java.util.ServiceLoader

class Strax @Inject internal constructor(
    private val container: PluginContainer,
    @DefaultConfig(sharedRoot = false)
    private val reference: ConfigurationReference<CommentedConfigurationNode>
) {

    companion object {
        lateinit var logger: Logger
    }

    @Listener
    fun onConstructPlugin(event: ConstructPluginEvent) {
        // Perform any one-time setup
        logger = event.plugin().logger()
        logger.info("Constructing Strax")

        loadConfig()

        ServiceLoader.load(StraxListenerService::class.java).forEach {
            Sponge.eventManager().registerListeners(container, it)
        }

        //Initialize Database
        StraxStorage.db
    }

    @Listener
    fun onServerStarting(event: StartingEngineEvent<Server>) {
        // Any setup per-game instance. This can run multiple times when
        // using the integrated (singleplayer) server.
    }

    @Listener
    fun onServerStopping(event: StoppingEngineEvent<Server>) {
        // Any tear down per-game instance. This can run multiple times when
        // using the integrated (singleplayer) server.
    }

    @Listener
    fun onRegisterCommands(event: RegisterCommandEvent<Command.Parameterized>) {
        ServiceLoader.load(StraxCommandService::class.java).map {
            it.commandMap
        }.forEach { map ->
            map.entries.forEach { cmd ->
                val firstAlias = cmd.value[0]
                val remainingAliases = cmd.value.filterNot { it.contentEquals(firstAlias) }.toTypedArray()
                if (remainingAliases.isEmpty()) {
                    event.register(container, cmd.key, firstAlias)
                } else {
                    event.register(container, cmd.key, firstAlias, *remainingAliases)
                }
            }
        }
    }

    private fun loadConfig() {
        logger.info("loading configuration file...")
        val conf = reference.referenceTo(CoreConfig::class.java)
        reference.runCatching { save() }
        val config = conf.get() ?: throw ConfigurateException("Deserialization failure")
        StraxConfigurationReference.straxConfig = config
    }
}