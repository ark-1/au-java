package me.arkadybazhanov.au.java.hw2


import org.junit.jupiter.api.Test

import java.util.Arrays
import java.util.Collections

import org.junit.jupiter.api.Assertions.*

class SmartListTest {

    private val listClass: Class<*>
        @Throws(ClassNotFoundException::class)
        get() = Class.forName("ru.spbau.mit.SmartList")

    @Test
    fun testSimple() {
        val list = newList<Int>()

        assertEquals(emptyList<Int>(), list)

        list.add(1)
        assertEquals(listOf(1), list)

        list.add(2)
        assertEquals(Arrays.asList(1, 2), list)
    }

    @Test
    fun testGetSet() {
        val list = newList<Any>()

        list.add(1)

        assertEquals(1, list[0])
        assertEquals(1, list.set(0, 2))
        assertEquals(2, list[0])
        assertEquals(2, list.set(0, 1))

        list.add(2)

        assertEquals(1, list[0])
        assertEquals(2, list[1])

        assertEquals(1, list.set(0, 2))

        assertEquals(Arrays.asList(2, 2), list)
    }

    @Test
    @Throws(Exception::class)
    fun testRemove() {
        val list = newList<Any>()

        list.add(1)
        list.removeAt(0)
        assertEquals(emptyList<Any>(), list)

        list.add(2)
        list.remove(2 as Any)
        assertEquals(emptyList<Any>(), list)

        list.add(1)
        list.add(2)
        assertEquals(Arrays.asList(1, 2), list)

        list.removeAt(0)
        assertEquals(listOf(2), list)

        list.removeAt(0)
        assertEquals(emptyList<Any>(), list)
    }

    @Test
    @Throws(Exception::class)
    fun testIteratorRemove() {
        val list = newList<Any>()
        assertFalse(list.iterator().hasNext())

        list.add(1)

        var iterator: MutableIterator<Any> = list.iterator()
        assertTrue(iterator.hasNext())
        assertEquals(1, iterator.next())

        iterator.remove()
        assertFalse(iterator.hasNext())
        assertEquals(emptyList<Any>(), list)

        list.addAll(Arrays.asList(1, 2))

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
    @Throws(Exception::class)
    fun testCollectionConstructor() {
        assertEquals(emptyList<T>(), newList(emptyList<Any>()))
        assertEquals(
                listOf<T>(1),
                newList(listOf(1)))

        assertEquals(
                Arrays.asList<T>(1, 2),
                newList(Arrays.asList(1, 2)))
    }

    @Test
    @Throws(Exception::class)
    fun testAddManyElementsThenRemove() {
        val list = newList<Any>()
        for (i in 0..6) {
            list.add(i + 1)
        }

        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7), list)

        for (i in 0..6) {
            list.removeAt(list.size - 1)
            assertEquals(6 - i, list.size)
        }

        assertEquals(emptyList<Any>(), list)
    }

    private fun <T> newList(): MutableList<T> {
        try {
            return listClass.getConstructor().newInstance() as List<T>
        } catch (e: InstantiationException) {
            throw RuntimeException(e)
        } catch (e: ClassNotFoundException) {
            throw RuntimeException(e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        } catch (e: NoSuchMethodException) {
            throw RuntimeException(e)
        } catch (e: InvocationTargetException) {
            throw RuntimeException(e)
        }

    }

    private fun <T> newList(collection: Collection<T>): List<T> {
        try {
            return listClass.getConstructor(Collection::class.java).newInstance(collection) as List<T>
        } catch (e: InstantiationException) {
            throw RuntimeException(e)
        } catch (e: ClassNotFoundException) {
            throw RuntimeException(e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        } catch (e: NoSuchMethodException) {
            throw RuntimeException(e)
        } catch (e: InvocationTargetException) {
            throw RuntimeException(e)
        }

    }
}
