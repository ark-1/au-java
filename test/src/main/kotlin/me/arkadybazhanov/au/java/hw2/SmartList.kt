package me.arkadybazhanov.au.java.hw2

import java.lang.IndexOutOfBoundsException

/**
 * An implementation of [MutableList] that is optimized for small number of elements
 * If it contains single element, the reference to it is stored directly.
 * If it contains from 2 to [SmartList.MAX_ARRAY_SIZE] elements, they are stored in an array.
 * Else elements are stored in default kotlin mutable list.
 */
class SmartList<T>() : AbstractMutableList<T>() {

    override var size: Int = 0
        private set

    private var elements: Any? = null

    constructor(collection: Collection<T>) : this() {
        size = collection.size
        when (size) {
            0 -> {}
            1 -> elements = collection.single()
            in 2..MAX_ARRAY_SIZE -> elements = collection.toTypedArray<Any?>().copyOf(MAX_ARRAY_SIZE)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private val single: T get() = elements as T

    @Suppress("UNCHECKED_CAST")
    private val array: Array<T> get() = elements as Array<T>

    @Suppress("UNCHECKED_CAST")
    private val list: MutableList<T> get() = elements as MutableList<T>

    override fun add(index: Int, element: T) {
        boundsCheck(index, size + 1)
        size++

        if (size == 1) {
            elements = element
            return
        }

        if (size == 2) {
            elements = arrayOf<Any?>(single, null, null, null, null)
        }
        if (size in 2..MAX_ARRAY_SIZE) {
            for (i in size - 2 downTo index) {
                array[i + 1] = array[i]
            }
            array[index] = element
            return
        }

        if (size == MAX_ARRAY_SIZE + 1) {
            elements = array.toMutableList()
        }
        list.add(index, element)
    }

    override fun get(index: Int): T {
        boundsCheck(index)
        return when (size) {
            1 -> single
            in 2..MAX_ARRAY_SIZE -> array[index]
            else -> list[index]
        }
    }

    private fun boundsCheck(index: Int, size: Int = this.size) {
        if (index !in 0 until size) throw IndexOutOfBoundsException()
    }

    override fun removeAt(index: Int): T {
        boundsCheck(index)
        size--

        if (size == 0) {
            return single.also { elements = null }
        }

        if (size == 1) {
            return array[index].also {
                val otherIndex = 1 - index
                elements = array[otherIndex]
            }
        }

        if (size >= MAX_ARRAY_SIZE) {
            val res = list.removeAt(index)
            if (size == MAX_ARRAY_SIZE) {
                elements = list.toTypedArray<Any?>()
            }
            return res
        }

        val res = array[index]
        for (i in size - 1 downTo index) {
            array[i] = array[i + 1]
        }
        return res
    }

    override fun set(index: Int, element: T): T {
        boundsCheck(index)
        return when (size) {
            1 -> single.also { elements = element }
            in 2..MAX_ARRAY_SIZE -> array[index].also { array[index] = element }
            else -> list.set(index, element)
        }
    }

    companion object {
        const val MAX_ARRAY_SIZE = 5
    }

}