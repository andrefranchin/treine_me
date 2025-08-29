package com.example.treine_me.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import java.util.*

@Serializable
abstract class BaseEntity {
    abstract val id: String
    abstract val dtIns: Instant
    abstract val dtUpd: Instant
    abstract val idUserIns: String
    abstract val idUserUpd: String
    abstract val isActive: Boolean
}

abstract class BaseTable(name: String) : UUIDTable(name) {
    val dtIns = timestamp("dt_ins")
    val dtUpd = timestamp("dt_upd")
    val idUserIns = uuid("id_user_ins")
    val idUserUpd = uuid("id_user_upd")
    val isActive = bool("is_active").default(true)
}
