package me.zodd.strax.core

import org.spongepowered.api.service.permission.Subject

object PermissionOptions {
    object Chat {
        val deserializableOptions = listOf(
            "chatcolor",
            "chatstyle",
        )

        class ChatFormatOptions(subject: Subject) {
            val prefix = subject.optionOrEmpty("prefix")
            val suffix = subject.optionOrEmpty("suffix")
            val namecolor = subject.optionOrEmpty("namecolor")
            val namestyle = subject.optionOrEmpty("namestyle")
        }
    }

    object Home {
        val limit = "home-limit"
    }

    fun Subject.optionOrEmpty(key: String): String = option(key).orElse("")
}