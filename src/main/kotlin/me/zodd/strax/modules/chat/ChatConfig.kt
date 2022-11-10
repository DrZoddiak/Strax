package me.zodd.strax.modules.chat

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@ConfigSerializable
data class ChatConfig(
    val chatTemplates: ChatTemplates = ChatTemplates(),
)

@ConfigSerializable
data class ChatTemplates(
    @field:Comment("Sets the prefix to a message.")
    val prefix : String = "",
    @field:Comment("sets the suffix to a message.")
    val suffix : String = "",
)