package me.arkadybazhanov.au.java.hw4

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

/** This exception is thrown if phone book user tries to perform an invalid operation. */
class PhoneBookOperationInvalid(message: String) : Exception(message)

/**
 * Adds a new record with given [name] and [phoneNumber] into the phone book.
 * @throws [PhoneBookOperationInvalid] if [name] or [phoneNumber] are longer than [MAX_STRING_LENGTH].
 */
fun addRecord(name: String, phoneNumber: String) {
    checkName(name)
    checkPhone(phoneNumber)

    transaction {
        checkNotExists(name, phoneNumber)

        PhoneBookRecordEntity.new {
            this.name = name
            this.phoneNumber = phoneNumber
        }
    }
}

/** Returns all records with given [name]. */
fun findByName(name: String): List<PhoneBookRecord> = transaction {
    PhoneBookRecordEntity.find { PhoneBookRecords.name eq name }.map(::PhoneBookRecord)
}

/** Returns all records with given [phoneNumber]. */
fun findByPhoneNumber(phoneNumber: String): List<PhoneBookRecord> = transaction {
    PhoneBookRecordEntity.find { PhoneBookRecords.phoneNumber eq phoneNumber }.map(::PhoneBookRecord)
}

/**
 * Deletes the record with given [name] and [phoneNumber] from the phone book.
 * @throws [PhoneBookOperationInvalid] if such record doesn't exist.
 */
fun deleteRecord(name: String, phoneNumber: String): Unit = transaction {
    findEntity(name, phoneNumber).delete()
}

/**
 * Changes the name of the record with given [name] and [phoneNumber] to [newName].
 * @throws [PhoneBookOperationInvalid] if [newName] is longer than [MAX_STRING_LENGTH],
 * or if no records with given [name] and [phoneNumber] exist,
 * or there already is a record with [newName] and [phoneNumber].
 */
fun setName(name: String, phoneNumber: String, newName: String) {
    checkName(newName)
    transaction {
        checkNotExists(newName, phoneNumber)
        findEntity(name, phoneNumber).name = newName
    }
}

/**
 * Changes the phone number of the record with given [name] and [phoneNumber] to [newPhoneNumber].
 * @throws [PhoneBookOperationInvalid] if [newPhoneNumber] is longer than [MAX_STRING_LENGTH],
 * or if no records with given [name] and [phoneNumber] exist,
 * or there already is a record with [name] and [newPhoneNumber].
 */
fun setPhoneNumber(name: String, phoneNumber: String, newPhoneNumber: String) {
    checkPhone(newPhoneNumber)
    transaction {
        checkNotExists(name, newPhoneNumber)
        findEntity(name, phoneNumber).phoneNumber = newPhoneNumber
    }
}

/** Returns all records. */
fun findAll(): List<PhoneBookRecord> = transaction {
    PhoneBookRecordEntity.all().map(::PhoneBookRecord)
}

private fun check(value: Boolean, message: String) {
    if (!value) {
        throw PhoneBookOperationInvalid(message)
    }
}

private fun checkName(name: String) = check(
    name.length <= MAX_STRING_LENGTH,
    "name.length > $MAX_STRING_LENGTH, name=$name"
)

private fun checkPhone(phoneNumber: String) = check(
    phoneNumber.length <= MAX_STRING_LENGTH,
    "phoneNumber.length > $MAX_STRING_LENGTH, phoneNumber=$phoneNumber"
)

private fun findEntityOrNull(name: String, phoneNumber: String): PhoneBookRecordEntity? {
    return PhoneBookRecordEntity.find {
        (PhoneBookRecords.name eq name) and (PhoneBookRecords.phoneNumber eq phoneNumber)
    }.singleOrNull()
}

private fun findEntity(name: String, phoneNumber: String) = findEntityOrNull(name, phoneNumber)
    ?: throw PhoneBookOperationInvalid("no such record: name=$name, phoneNumber=$phoneNumber")

private fun checkNotExists(name: String, phoneNumber: String) = check(
    findEntityOrNull(name, phoneNumber) == null,
    "record already exists: name=$name, phoneNumber=$phoneNumber"
)
