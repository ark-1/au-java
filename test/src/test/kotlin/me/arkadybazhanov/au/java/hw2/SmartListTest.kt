package me.arkadybazhanov.au.java.hw2


import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.lang.IndexOutOfBoundsException

class SmartListTest {

    @Test
    fun testOutOfBounds() {
        assertThrows(IndexOutOfBoundsException::class.java) {
            val list = newList<Int>()
            list += 1
            list[1]
        }

        assertThrows(IndexOutOfBoundsException::class.java) {
            val list = newList<Int>()
            list += 1
            list[1] = 2
        }

        assertThrows(IndexOutOfBoundsException::class.java) {
            val list = newList<Int>()
            list += 1
            list.add(2, 3)
        }
    }

    @Test
    fun testSimple() {
        val list = newList<Int>()

        assertEquals(emptyList<Int>(), list)

        list += 1
        assertEquals(listOf(1), list)

        list += 2
        assertEquals(listOf(1, 2), list)
    }

    @Test
    fun testGetSet() {
        val list = newList<Any>()

        list += 1

        assertEquals(1, list[0])
        assertEquals(1, list.set(0, 2))
        assertEquals(2, list[0])
        assertEquals(2, list.set(0, 1))

        list += 2

        assertEquals(1, list[0])
        assertEquals(2, list[1])

        assertEquals(1, list.set(0, 2))

        assertEquals(listOf(2, 2), list)
    }

    @Test
    fun testRemove() {
        val list = newList<Any>()

        list += 1
        list.removeAt(0)
        assertEquals(emptyList<Any>(), list)

        list += 2
        list.remove(2 as Any)
        assertEquals(emptyList<Any>(), list)

        list += 1
        list += 2
        assertEquals(listOf(1, 2), list)

        list.removeAt(0)
        assertEquals(listOf(2), list)

        list.removeAt(0)
        assertEquals(emptyList<Any>(), list)
    }

    @Test
    fun testIteratorRemove() {
        val list = newList<Any>()
        assertFalse(list.iterator().hasNext())

        list += 1

        var iterator: MutableIterator<Any> = list.iterator()
        assertTrue(iterator.hasNext())
        assertEquals(1, iterator.next())

        iterator.remove()
        assertFalse(iterator.hasNext())
        assertEquals(emptyList<Any>(), list)

        list.addAll(listOf(1, 2))

        iterator = list.iterator()
        assertTrue(iterator.hasNext())
        assertEquals(1, iterator.next())

        iterator.remove()
        assertTrue(iterator.hasNext())
        assertEquals(listOf(2), list)
        assertEquals(2, iterator.next())

        iterator.remove()
        assertFalse(iterator.hasNext())
        assertEquals(emptyList<Any>(), list)
    }


    @Test
    fun testCollectionConstructor() {
        assertEquals(listOf<Int>(), newList(listOf<Int>()))
        assertEquals(listOf(1), newList(listOf(1)))
        assertEquals(listOf(1, 2), newList(listOf(1, 2)))
    }

    @Test
    fun testAddManyElementsThenRemove() {
        val list = newList<Any>()
        for (i in 0..6) {
            list += i + 1
        }

        assertEquals(listOf(1, 2, 3, 4, 5, 6, 7), list)

        for (i in 0..6) {
            list.removeAt(list.size - 1)
            assertEquals(6 - i, list.size)
        }

        assertEquals(emptyList<Any>(), list)
    }


    private val listClass: Class<*>
        get() = Class.forName("me.arkadybazhanov.au.java.hw2.SmartList")

    @Suppress("UNCHECKED_CAST")
    private fun <T> newList(): MutableList<T> = listClass.getConstructor().newInstance() as MutableList<T>

    @Suppress("UNCHECKED_CAST")
    private fun <T> newList(collection: Collection<T>): MutableList<T> =
        listClass.getConstructor(Collection::class.java).newInstance(collection) as MutableList<T>
}
