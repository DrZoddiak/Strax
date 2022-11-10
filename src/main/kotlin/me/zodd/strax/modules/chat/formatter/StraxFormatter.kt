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



/*
strax.chat.reset
strax.chat.color
strax.chat.selector
strax.chat.transition
strax.chat.decorations
strax.chat.insertion
strax.chat.clickEvent
strax.chat.hoverEvent
strax.chat.rainbow
strax.chat.newline
strax.chat.keybind
strax.chat.translatable
strax.chat.gradient
strax.chat.font

 */

