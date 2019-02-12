package me.arkadybazhanov.au.java.hw3

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class BambooSetTest {

    @Test
    fun getSize() {
        val set = BambooSet<Int>()
        for (i in 0..1000) {
            assertEquals(i, set.size)
            set.add(i)
            assertEquals(i + 1, set.size)
            set.add(i)
            assertEquals(i + 1, set.size)
        }

        for (i in 1000 downTo 0) {
            assertEquals(i + 1, set.size)
            set.remove(i)
            assertEquals(i, set.size)
            set.remove(i)
            assertEquals(i, set.size)
        }
    }

    @Test
    fun add() {
        val set = BambooSet<Int>()
        for (i in 0..1000) {
            assertEquals(i, set.size)
            assertTrue(set.add(i))
            assertEquals(i + 1, set.size)
            assertFalse(set.add(i))
            assertEquals(i + 1, set.size)
        }
    }

    @Test
    fun descendingIterator() {
        val set = BambooSet.from(0..1000)
        assertIterableEquals(1000 downTo 0, set.descendingIterator().asSequence().toList())
    }

    @Test
    fun descendingSet() {
        val set = BambooSet.from(0..1000)
        val descending = set.descendingSet()
        assertIterableEquals(1000 downTo 0, descending)
        set.addAll(1001..2000)
        assertIterableEquals(2000 downTo 0, descending)
    }

    @Test
    fun first() {
        val set = BambooSet.from(0..1000)
        assertEquals(0, set.first())
        set.remove(0)
        assertEquals(1, set.first())
        set.add(-1)
        assertEquals(-1, set.first())
    }

    @Test
    fun last() {
        val set = BambooSet.from(0..1000)
        assertEquals(1000, set.last())
        set.remove(1000)
        assertEquals(999, set.last())
        set.add(1001)
        assertEquals(1001, set.last())
    }

    @Test
    fun lower() {
        val set = BambooSet.from(0..1000 step 10)
        assertEquals(10, set.lower(11))
        assertEquals(10, set.lower(20))
        assertEquals(1000, set.lower(2000))
        assertNull(set.lower(-1))
    }

    @Test
    fun floor() {
        val set = BambooSet.from(0..1000 step 10)
        assertEquals(10, set.floor(11))
        assertEquals(20, set.floor(20))
        assertEquals(1000, set.floor(2000))
        assertNull(set.floor(-1))
    }

    @Test
    fun ceiling() {
        val set = BambooSet.from(0..1000 step 10)
        assertEquals(20, set.ceiling(11))
        assertEquals(20, set.ceiling(20))
        assertEquals(0, set.ceiling(-2000))
        assertNull(set.ceiling(2000))
    }

    @Test
    fun higher() {
        val set = BambooSet.from(0..1000 step 10)
        assertEquals(20, set.higher(11))
        assertEquals(30, set.higher(20))
        assertEquals(0, set.higher(-2000))
        assertNull(set.higher(2000))
    }

    @Test
    fun iterator() {
        val set = BambooSet.from(0..1000)
        assertIterableEquals(0..1000, set.iterator().asSequence().asIterable())

        val iterator = set.iterator()
        while (iterator.hasNext()) {
            iterator.next()

            if (!iterator.hasNext()) {
                break
            }

            iterator.next()
            iterator.remove()
        }

        assertIterableEquals(0..1000 step 2, set.iterator().asSequence().asIterable())
    }

    @Test
    fun iteratorConcurrentModification() {
        val set = BambooSet.from(0..1000)

        val iterator1 = set.iterator()
        val iterator2 = set.iterator()
        iterator1.next()
        iterator2.next()

        iterator1.remove()
        assertThrows(ConcurrentModificationException::class.java) {
            iterator2.next()
        }
        iterator1.next()

        set.add(0)
        assertThrows(ConcurrentModificationException::class.java) {
            iterator1.next()
        }
    }

    @Test
    fun factory() {
        val expected = 0..1000
        assertIterableEquals(expected, BambooSet<Int>().apply { addAll(expected) })
        assertIterableEquals(expected, BambooSet.from(expected))
        assertIterableEquals(expected, BambooSet.of(*expected.toList().toTypedArray()))
    }
}