package me.zodd.strax.modules.chat.formatter

import me.zodd.strax.Strax
import me.zodd.strax.core.PermissionOptions
import me.zodd.strax.core.utils.StraxConfigurationReference
import net.kyori.adventure.text.minimessage.MiniMessage
import org.spongepowered.api.entity.living.player.PlayerChatFormatter
import org.spongepowered.api.service.permission.Subject

abstract class StraxFormatter(subject : Subject) : PlayerChatFormatter {
    val mm = MiniMessage.miniMessage()

    val options = PermissionOptions.Chat.ChatFormatOptions(subject)

    val config = StraxConfigurationReference.straxConfig
}
