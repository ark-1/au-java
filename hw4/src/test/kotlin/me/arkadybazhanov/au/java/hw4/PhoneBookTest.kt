package me.arkadybazhanov.au.java.hw4

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import java.io.File
import java.sql.Connection.TRANSACTION_SERIALIZABLE
import kotlin.test.*

internal class PhoneBookTest {
    private val dbFilePath = "phone-book-test.db"

    @BeforeTest
    fun init() {
        File(dbFilePath).delete()

        Database.connect("jdbc:sqlite:$dbFilePath", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = TRANSACTION_SERIALIZABLE

        transaction { SchemaUtils.create(PhoneBookRecords) }
    }

    @AfterTest
    fun end() {
        File(dbFilePath).delete()
    }

    private val longString = "a".repeat(1000)

    private fun PhoneBookRecord.add() = addRecord(name, phoneNumber)
    private fun PhoneBookRecord.delete(): Unit = deleteRecord(name, phoneNumber)
    private fun PhoneBookRecord.setName(newName: String) = setName(name, phoneNumber, newName)
    private fun PhoneBookRecord.setPhoneNumber(newPhoneNumber: String): Unit = setPhoneNumber(name, phoneNumber, newPhoneNumber)

    private fun <T> assertContents(actual: List<T>, vararg expected: T) {
        assertEquals(hashSetOf(*expected), HashSet(actual))
    }

    @Test
    fun addRecord() {
        val record1 = PhoneBookRecord("aa", "aaa").also { it.add() }
        val record2 = PhoneBookRecord("bb", record1.phoneNumber).also { it.add() }
        val record3 = PhoneBookRecord(record1.name, "bbb").also { it.add() }
        PhoneBookRecord(record2.name, record3.phoneNumber).also { it.add() }

        assertFailsWith<PhoneBookOperationInvalid> {
            record1.add()
        }

        assertFailsWith<PhoneBookOperationInvalid> {
            addRecord(longString, "ccc")
        }

        assertFailsWith<PhoneBookOperationInvalid> {
            addRecord("cc", longString)
        }

        record1.delete()
        record1.add()
    }

    @Test
    fun findByName() {
        val record1 = PhoneBookRecord("aa", "aaa").also { it.add() }
        val record2 = PhoneBookRecord("bb", record1.phoneNumber).also { it.add() }
        val record3 = PhoneBookRecord(record1.name, "bbb").also { it.add() }
        PhoneBookRecord(record2.name, record3.phoneNumber).add()

        assertContents(findByName(record1.name), record1, record3)
        assertContents(findByName("cc"))
        assertContents(findByName(longString))

        record1.delete()

        assertContents(findByName(record1.name), record3)
    }

    @Test
    fun findByPhoneNumber() {
        val record1 = PhoneBookRecord("aa", "aaa").also { it.add() }
        val record2 = PhoneBookRecord("bb", record1.phoneNumber).also { it.add() }
        val record3 = PhoneBookRecord(record1.name, "bbb").also { it.add() }
        PhoneBookRecord(record2.name, record3.phoneNumber).add()

        assertContents(findByPhoneNumber(record1.phoneNumber), record1, record2)
        assertContents(findByPhoneNumber("ccc"))
        assertContents(findByName(longString))

        record1.delete()

        assertContents(findByPhoneNumber(record1.phoneNumber), record2)
    }

    @Test
    fun deleteRecord() {
        val record1 = PhoneBookRecord("aa", "aaa").also { it.add() }
        val record2 = PhoneBookRecord("bb", record1.phoneNumber).also { it.add() }
        val record3 = PhoneBookRecord(record1.name, "bbb").also { it.add() }
        val record4 = PhoneBookRecord(record2.name, record3.phoneNumber).also { it.add() }
        assertContents(findAll(), record1, record2, record3, record4)

        record4.delete()
        assertContents(findAll(), record1, record2, record3)

        record3.delete()
        assertContents(findAll(), record1, record2)

        assertFailsWith<PhoneBookOperationInvalid> { record3.delete() }

        record2.delete()
        assertContents(findAll(), record1)

        record1.delete()
        assertContents(findAll())

        record1.add()
        record1.delete()
        assertContents(findAll())
    }

    @Test
    fun setName() {
        val record1 = PhoneBookRecord("aa", "aaa").also { it.add() }
        val record2 = PhoneBookRecord("bb", record1.phoneNumber).also { it.add() }
        val record3 = PhoneBookRecord(record1.name, "bbb").also { it.add() }
        val record4 = PhoneBookRecord(record2.name, record3.phoneNumber).also { it.add() }
        assertContents(findAll(), record1, record2, record3, record4)

        val newRecord4 = record4.copy(name = "cc")
        record4.setName(newRecord4.name)
        assertContents(findAll(), record1, record2, record3, newRecord4)

        assertFailsWith<PhoneBookOperationInvalid> { newRecord4.setName(record3.name) }
        assertFailsWith<PhoneBookOperationInvalid> { newRecord4.setName(longString) }
    }

    @Test
    fun setPhoneNumber() {
        val record1 = PhoneBookRecord("aa", "aaa").also { it.add() }
        val record2 = PhoneBookRecord("bb", record1.phoneNumber).also { it.add() }
        val record3 = PhoneBookRecord(record1.name, "bbb").also { it.add() }
        val record4 = PhoneBookRecord(record2.name, record3.phoneNumber).also { it.add() }
        assertContents(findAll(), record1, record2, record3, record4)

        val newRecord4 = record4.copy(phoneNumber = "ccc")
        record4.setPhoneNumber(newRecord4.phoneNumber)
        assertContents(findAll(), record1, record2, record3, newRecord4)

        assertFailsWith<PhoneBookOperationInvalid> { newRecord4.setPhoneNumber(record2.phoneNumber) }
        assertFailsWith<PhoneBookOperationInvalid> { newRecord4.setPhoneNumber(longString) }
    }

    @Test
    fun `test findAll`() {
        val record1 = PhoneBookRecord("aa", "aaa").also { it.add() }
        assertContents(findAll(), record1)

        val record2 = PhoneBookRecord("bb", record1.phoneNumber).also { it.add() }
        assertContents(findAll(), record1, record2)

        val record3 = PhoneBookRecord(record1.name, "bbb").also { it.add() }
        assertContents(findAll(), record1, record2, record3)

        record2.delete()
        assertContents(findAll(), record1, record3)

        val newRecord1 = record1.copy(name = "cc")
        record1.setName(newRecord1.name)
        assertContents(findAll(), newRecord1, record3)
    }
}