package me.zodd.strax.modules.chat.processor

import me.zodd.strax.core.PermissionOptions
import me.zodd.strax.core.utils.StraxMiniMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.spongepowered.api.entity.living.player.server.ServerPlayer
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf

object StraxChatProcessor {
    private val mmBuilder = MiniMessage.builder()

    private val plaintextSerializer = PlainTextComponentSerializer.plainText()

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
        val str = plaintextSerializer.serialize(content)
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
        val randomString = "Oranges"

        val defaultChatStyle = StraxMiniMessage.mm.deserialize("$chatOptions$randomString").style()

        val miniMessage = mmBuilder.tags(resolver.build()).build()

        return miniMessage.deserialize(str).applyFallbackStyle(defaultChatStyle)
    }
}