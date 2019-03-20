package me.arkadybazhanov.au.java.hw7

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test
import kotlin.random.Random

internal class QSortTest {
    @Test
    fun sortSynchronously() {
        withRandomArray(1000) {
            assertArrayEquals(it.sortedArray(), it.apply {
                sortSynchronously()
            })
        }
    }

    @Test
    fun sortAsynchronously() {
        withRandomArray(1000) {
            assertArrayEquals(it.sortedArray(), it.apply {
                runBlocking { sortAsynchronously() }
            })
        }
    }

    private inline fun withRandomArray(size: Int, block: (Array<Int>) -> Unit) {
        val random = Random(0)
        val array = Array(size) {
            random.nextInt()
        }
        block(array)
    }
}