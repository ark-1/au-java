package me.arkadybazhanov.au.java.hw7

import kotlinx.coroutines.*
import kotlin.random.Random
import kotlin.system.*

private fun <T> Array<T>.swap(i: Int, j: Int) {
    this[i] = this[j].also { this[j] = this[i] }
}

private fun <T : Comparable<T>> Array<T>.partition(fromIndex: Int, toIndex: Int): Int {
    val lastElementValue = this[toIndex]
    var i = fromIndex - 1
    for (j in fromIndex until toIndex) {
        if (this[j] <= lastElementValue) {
            i++
            swap(i, j)
        }
    }

    swap(i + 1, toIndex)
    return i + 1
}

private const val ASYNC_THRESHOLD = 10_002

private suspend fun <T : Comparable<T>> Array<T>.sortAsynchronously(
    scope: CoroutineScope,
    fromIndex: Int,
    toIndex: Int
) {
    if (fromIndex >= toIndex) return
    if (toIndex - fromIndex < ASYNC_THRESHOLD) return sortSynchronously(fromIndex, toIndex)

    val middleIndex = this.partition(fromIndex, toIndex)

    val halfJob = scope.launch {
        sortAsynchronously(scope, fromIndex, middleIndex - 1)
    }

    sortAsynchronously(scope, middleIndex + 1, toIndex)

    halfJob.join()
}

/** Sorts [this] array with */
private fun <T : Comparable<T>> Array<T>.sortSynchronously(fromIndex: Int, toIndex: Int) {
    if (fromIndex >= toIndex) return

    val middleIndex = this.partition(fromIndex, toIndex)

    sortSynchronously(fromIndex, middleIndex - 1)
    sortSynchronously(middleIndex + 1, toIndex)
}

/** Sorts [this] array with QuickSort algorithm */
fun <T : Comparable<T>> Array<T>.sortSynchronously() {
    sortSynchronously(0, lastIndex)
}

/**
 * Sorts [this] array with QuickSort algorithm concurrently.
 * When segment size becomes less than [ASYNC_THRESHOLD], synchronous sort is used
 * */
suspend fun <T : Comparable<T>> Array<T>.sortAsynchronously() = coroutineScope {
    sortAsynchronously(this, 0, lastIndex)
}

const val REPEAT_TIMES = 10
private inline fun measureAverageNanoTime(array: Array<Int>, block: (Array<Int>) -> Unit): Long {
    var sum = 0L

    repeat(REPEAT_TIMES) {
        val clone = array.clone()
        sum += measureNanoTime {
            block(clone)
        }
    }

    return sum / REPEAT_TIMES
}

private suspend inline fun measureAndPrint(size: Int) {
    val array = Array(size) {
        Random.nextInt()
    }

    val asyncTime = measureAverageNanoTime(array) {
        it.sortAsynchronously()
    }

    val syncTime = measureAverageNanoTime(array) {
        it.sortSynchronously()
    }

    println("size = $size")
    println("sync: \t$syncTime ns")
    println("async: \t$asyncTime ns")
    println()
}

suspend fun main() {
    measureAndPrint(1)
    measureAndPrint(10)
    measureAndPrint(100)
    measureAndPrint(1_000)
    measureAndPrint(10_000) // on my machine this seems to be the point where sync is faster
    measureAndPrint(100_000)
    measureAndPrint(1_000_000)
}