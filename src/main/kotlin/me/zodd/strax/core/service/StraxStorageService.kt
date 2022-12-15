package me.zodd.strax.core.service

import org.jetbrains.exposed.sql.Table

abstract class StraxStorageService(val table: Table) : StraxListenerService()