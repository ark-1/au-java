package me.arkadybazhanov.au.java.hw4

import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.Column

const val MAX_STRING_LENGTH = 50

object PhoneBookRecords : LongIdTable() {
    val name: Column<String> = varchar(this::name.name, length = MAX_STRING_LENGTH)
    val phoneNumber: Column<String> = varchar(this::phoneNumber.name, length = MAX_STRING_LENGTH)

    init {
        uniqueIndex(name, phoneNumber)
    }
}

class PhoneBookRecordEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<PhoneBookRecordEntity>(PhoneBookRecords)

    var name by PhoneBookRecords.name
    var phoneNumber by PhoneBookRecords.phoneNumber
}

data class PhoneBookRecord(val name: String, val phoneNumber: String) {
    constructor(entity: PhoneBookRecordEntity) : this(entity.name, entity.phoneNumber)
}
