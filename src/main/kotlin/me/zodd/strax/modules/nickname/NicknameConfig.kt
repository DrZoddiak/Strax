package me.zodd.strax.modules.nickname

import me.zodd.strax.Strax
import me.zodd.strax.core.StraxDeserializer
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class NicknameConfig(
    val nicknameMaxSize: Int = 20,
    val nicknameMinSize: Int = 3,
    val nicknameRegex: String = "[a-zA-Z0-9_]+",
    val nicknamePrefix: String = "<blue>~",
) : StraxDeserializer {

    fun validNickname(nickname: String): Boolean {
        Strax.logger.info("Checking Nickname Validity")
        return validNicknameLength(nickname) && validNicknameRegex(nickname)
    }

    private fun validNicknameLength(nickname: String): Boolean {
        val nameStripped = stripTags(nickname)
        val length = nameStripped.length in nicknameMinSize..nicknameMaxSize
        Strax.logger.info("Checking Nickname Length [$nameStripped] : $length")
        Strax.logger.info("max : $nicknameMaxSize ; min : $nicknameMinSize; nameLength : ${nameStripped.length}")
        return length
    }

    private fun validNicknameRegex(nickname: String): Boolean {
        val nameStripped = stripTags(nickname)
        val matches = nameStripped.matches(nicknameRegex.toRegex())
        Strax.logger.info("Checking Nickname Regex : $matches")
        return matches
    }

    private fun stripTags(nickname: String): String {
        return minimessage.stripTags(nickname)
    }
}