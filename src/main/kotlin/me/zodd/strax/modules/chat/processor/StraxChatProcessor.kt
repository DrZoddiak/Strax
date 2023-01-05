package me.zodd.strax.modules.chat.processor

import me.zodd.strax.core.PermissionOptions
import me.zodd.strax.core.StraxDeserializer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags
import org.spongepowered.api.entity.living.player.server.ServerPlayer
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf

object StraxChatProcessor {

    private val deserializer = StraxDeserializer

    private val permissionResolverMap: Map<String, TagResolver> = StandardTags::class.members
        .asSequence()
        .filter { it.returnType.isSubtypeOf(TagResolver::class.createType()) }
        .filterNot { it.parameters.any() }
        .filterNot { it.name == "ALL" || it.name == "defaults" }
        .map {
            val res = it.call() as TagResolver
            "strax.chat.${it.name.lowercase()}" to res
        }.toMap()

    fun parseContent(player: ServerPlayer, content: Component): Component {
        val str = deserializer.plaintextSerializer.serialize(content)
        val resolver = TagResolver.builder()

        permissionResolverMap.filter {
            player.hasPermission(it.key)
        }.forEach {
            resolver.resolver(it.value)
        }

        val chatOptions = PermissionOptions.Chat.deserializableOptions.joinToString("") {
            player.option(it).orElse("")
        }

        //When deserializing a string without literal text it won't properly store a Components style
        val defaultChatStyle = deserializer.minimessage.deserialize("${chatOptions}oranges").style()

        val mm = deserializer.minimessageBuilder.tags(resolver.build()).build()

        return mm.deserialize(str).applyFallbackStyle(defaultChatStyle)
    }
}