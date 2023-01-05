package me.zodd.strax.core

import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer

object StraxDeserializer {
    val minimessageBuilder: MiniMessage.Builder = MiniMessage.builder()

    val minimessage: MiniMessage = MiniMessage.miniMessage()

    val plaintextSerializer = PlainTextComponentSerializer.plainText()
}