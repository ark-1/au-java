package me.arkadybazhanov.au.java.hw2

import java.io.FileReader
import java.io.PrintWriter
import java.lang.NumberFormatException
import java.util.Scanner

private fun String.parseInt(): Maybe<Int> = try {
    Maybe.just(toInt())
} catch (_: NumberFormatException) {
    Maybe.nothing()
}

fun main() {

    Scanner(FileReader("input.txt")).use { scanner ->
        PrintWriter("output.txt").use { writer ->
            while (scanner.hasNext()) scanner.next().parseInt().map {
                writer.println(it * it)
            }
        }
    }
}