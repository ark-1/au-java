package me.arkadybazhanov.au.java.test4

import me.arkadybazhanov.au.java.test4.Game.Cell
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


internal class GameTest {
    @Test
    fun generation() {
        val n = 6
        val game = Game(n)
        val cells = mutableListOf<Cell>()
        for (i in 0 until n) {
            for (j in 0 until n) {
                cells += game[i, j]
            }
        }
        assertTrue(cells.map { it.value }.sorted() == ((1..n * n / 2).toList() + (1..n * n / 2).toList()).sorted())
        assertTrue(cells.none { it.show })
    }
}
