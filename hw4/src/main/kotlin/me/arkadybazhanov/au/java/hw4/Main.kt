package me.arkadybazhanov.au.java.hw4
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import java.sql.Connection

private const val USAGE: String = """
Commands:
    0 - exit
    1 - add record (name and phone number)
    2 - find phone numbers by name
    3 - find имена by phone number
    4 - delete given record
    5 - set new name for given record
    6 - set new phone number for given record
    7 - print all records
"""

/**
 * Starts command line interface of phone book manager.
 * Accepts exactly one argument (in [args]): path to the SQLite database file.
 */
fun main(args: Array<String>) {
    require(args.size == 1) {
        "Exactly one argument required - db file path (args.size=${args.size})"
    }

    val dbFilePath = args[0]

    Database.connect("jdbc:sqlite:$dbFilePath", "org.sqlite.JDBC")
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

    transaction {
        SchemaUtils.create(PhoneBookRecords)
    }

    println(USAGE)

    val editingCommands = listOf(1, 4, 5, 6)
    while (true) {
        try {
            val command = request("command").toIntOrNull()

            if (command !in 0..7) {
                println("Command should be a number from 0 to 7")
                continue
            } else command!!

            when (command) {
                0 -> return
                1 -> addRecord(requestNewName(), requestNewPhoneNumber())
                2 -> findByName(requestName()).forEach {
                    println(it.phoneNumber)
                }
                3 -> findByPhoneNumber(requestPhoneNumber()).forEach {
                    println(it.name)
                }
                4 -> deleteRecord(requestName(), requestPhoneNumber())
                5 -> setName(request("current name"), requestPhoneNumber(), requestNewName())
                6 -> setPhoneNumber(requestName(), request("current phone number"), requestNewName())
                7 -> {
                    val all = findAll()
                    val size = if (all.size == 1) "1 record" else "${all.size} records"
                    println("Printing $size:")
                    all.forEach {
                        println("name=${it.name} + phoneNumber=${it.phoneNumber}")
                    }
                }
            }
            if (command in editingCommands) println("Success!")

        } catch (_: Eof) {
            return
        } catch (e: PhoneBookOperationInvalid) {
            e.message?.let { println("Error: $it") }
        }
    }
}

private object Eof : Exception()

private fun request(what: String): String {
    println("Enter $what:")
    return readLine() ?: throw Eof
}

private fun requestName() = request("name")
private fun requestNewName() = request("new name")
private fun requestPhoneNumber() = request("phone number")
private fun requestNewPhoneNumber() = request("new phone number")
