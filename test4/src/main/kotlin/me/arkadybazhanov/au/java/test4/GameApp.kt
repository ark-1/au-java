package me.arkadybazhanov.au.java.test4

import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.layout.RowConstraints
import kotlinx.coroutines.*
import me.arkadybazhanov.au.java.test4.Game.Cell
import me.arkadybazhanov.au.java.test4.Game.State.WATCHING
import tornadofx.*
import kotlin.coroutines.CoroutineContext

class GameApp : App(GameView::class)

class GameView : View(), CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    private val n = app.parameters.raw.single().toInt()
    private var game = Game(n)
    private val buttons = Array(n) { arrayOfNulls<Button>(n) }

    private lateinit var restartButton: Button

    override val root = borderpane {
        center {
            gridpane {
                prefWidth = prefCellSize * n
                prefHeight = prefCellSize * n
                for (i in 0 until n) row {
                    rowConstraints += RowConstraints().apply {
                        percentHeight = 100.0 / n
                    }

                    for (j in 0 until n) {
                        buttons[i][j] = button(game[i, j].caption) {
                            gridpaneColumnConstraints {
                                percentWidth = 100.0 / n
                            }

                            useMaxWidth = true
                            minWidth = minCellSize
                            useMaxHeight = true
                            minHeight = minCellSize

                            onMouseClicked = EventHandler {
                                game.choose(i, j)
                                updateCaption(i, j)
                                if (game.state == WATCHING) {
                                    showCellsForTimeout()
                                }
                            }
                        }
                    }
                }
            }
        }

        right {
            restartButton = button("Restart")
            restartButton.onMouseClicked = EventHandler { reset() }
        }
    }

    private fun updateCaption(x: Int, y: Int) {
        buttons[x][y]?.text = game[x, y].caption
    }

    private fun reset() {
        game = Game(n)
        updateCaptions()
        setButtonsDisable(false)
    }

    private fun updateCaptions() {
        for (i in buttons.indices) {
            for (j in buttons[i].indices) {
                updateCaption(i, j)
            }
        }
    }

    private fun setButtonsDisable(disable: Boolean) {
        restartButton.isDisable = disable
        for (i in buttons.indices) {
            for (j in buttons[i].indices) {
                buttons[i][j]?.isDisable = disable
            }
        }
    }

    private fun showCellsForTimeout() {
        launch {
            setButtonsDisable(true)
            delay(timeoutMillis)
            game.resetState()
            withContext(Dispatchers.Main) {
                updateCaptions()
            }
            setButtonsDisable(false)
        }
    }

    private val Cell.caption get() = if (show) value.toString() else " "

    companion object {
        private const val timeoutMillis = 2000L
        private const val minCellSize = 30.0
        private const val prefCellSize = 50.0
    }
}


fun main(args: Array<String>) {
    val n = args.singleOrNull()?.toIntOrNull() ?: run {
        System.err.println("App should accept single number as argument. args = ${args.contentToString()}")
        return
    }

    if (n < 2 || n > 200) {
        System.err.println("N should be even number in range 2..200")
        return
    }

    launch<GameApp>(args)
}