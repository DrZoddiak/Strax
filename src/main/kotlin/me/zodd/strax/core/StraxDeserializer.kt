package me.zodd.strax.core

import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer

interface StraxDeserializer {
    val minimessageBuilder
        get() = MiniMessage.builder()

    val minimessage: MiniMessage
        get() = MiniMessage.miniMessage()

    val plaintextSerializer : PlainTextComponentSerializer
        get() = PlainTextComponentSerializer.plainText()
}