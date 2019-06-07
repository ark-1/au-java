package me.arkadybazhanov.au.java.test4

import me.arkadybazhanov.au.java.test4.Game.State.*

class Game(n: Int) {
    data class Cell(val value: Int, var show: Boolean)

    private val cells = List(n * n) {
        Cell(it / 2 + 1, false)
    }.shuffled().chunked(n)

    operator fun get(x: Int, y: Int) = cells[x][y]

    private var firstCell: Cell? = null
    private var secondCell: Cell? = null

    var state = CHOOSING_FIRST

    fun choose(x: Int, y: Int) {
        when (state) {
            CHOOSING_FIRST -> {
                val cell = cells[x][y]
                if (cell.show) {
                    return
                }

                firstCell = cell.apply { show = true }
                state = CHOOSING_SECOND
            }

            CHOOSING_SECOND -> {
                val cell = cells[x][y]
                if (cell.show) {
                    return
                }

                cell.show = true

                if (cell.value == firstCell!!.value) {
                    state = CHOOSING_FIRST
                    firstCell = null
                    return
                }

                secondCell = cell.apply { show = true }
                state = WATCHING
            }

            WATCHING -> error("Should not happen")
        }
    }

    fun resetState() {
        firstCell?.show = false
        secondCell?.show = false
        firstCell = null
        secondCell = null
        state = CHOOSING_FIRST
    }

    enum class State {
        CHOOSING_FIRST, CHOOSING_SECOND, WATCHING
    }
}