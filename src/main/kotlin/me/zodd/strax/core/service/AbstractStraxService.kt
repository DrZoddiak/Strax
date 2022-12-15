package me.zodd.strax.core.service

import me.zodd.strax.core.utils.StraxConfigurationReference

abstract class AbstractStraxService {
    val config = StraxConfigurationReference.straxConfig
}