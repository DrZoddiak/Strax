package me.zodd.strax.core.service

import me.zodd.strax.core.utils.StraxConfigurationReference

abstract class StraxListenerService {
    val config = StraxConfigurationReference.straxConfig.modules
}