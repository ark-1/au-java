package me.arkadybazhanov.au.java.hw2

import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.random.Random

internal class TrieTest {

    @Test
    fun add() {
        val trie = Trie()

        for (i in 1..1000) {
            val element = "elem $i"
            assertTrue(trie.add(element))
            assertFalse(trie.add(element))
        }

        var element = "elem"
        for (i in 1..1000) {
            element = "long $element"
            assertTrue(trie.add(element))
            assertFalse(trie.add(element))
        }
    }

    @Test
    fun contains() {
        val trie = Trie()

        for (i in 1..1000) {
            val element = "elem $i"
            assertFalse(element in trie)
            trie.add(element)
            assertTrue(element in trie)
        }

        for (i in 1..1000) {
            val element = "elem $i"
            assertTrue(element in trie)
            trie.remove(element)
            assertFalse(element in trie)
        }

        var element = "elem"
        for (i in 1..1000) {
            element = "long $element"
            assertFalse(element in trie)
            trie.add(element)
            assertTrue(element in trie)
        }

        element = "elem"
        for (i in 1..1000) {
            element = "long $element"
            assertTrue(element in trie)
            trie.remove(element)
            assertFalse(element in trie)
        }
    }

    @Test
    fun remove() {
        val trie = Trie()

        for (i in 1..1000) {
            trie.add("elem $i")
        }

        for (i in 1..1000) {
            val element = "elem $i"
            assertTrue(trie.remove(element))
            assertFalse(trie.remove(element))
        }

        var element = "elem"
        for (i in 1..1000) {
            element = "long $element"
            trie.add(element)
        }

        element = "elem"
        for (i in 1..1000) {
            element = "long $element"
            assertTrue(trie.remove(element))
            assertFalse(trie.remove(element))
        }
    }

    @Test
    fun size() {
        val trie = Trie()
        for (i in 1..1000) {
            trie.add("elem $i")
            assertEquals(i, trie.size)
        }

        for (i in 1000 downTo 1) {
            assertEquals(i, trie.size)
            trie.remove("elem $i")
        }

        var element = "elem"
        for (i in 1..1000) {
            element = "long $element"
            trie.add(element)
            assertEquals(i, trie.size)
        }

        element = "elem"
        for (i in 1000 downTo 1) {
            element = "long $element"
            assertEquals(i, trie.size)
            trie.remove(element)
        }
    }

    private fun Random.getRandomString(size: Int) = CharArray(size) {
        ('a'..'z').random(this)
    }.contentToString()

    @Test
    fun howManyStartWithPrefix() {
        val random = Random(0)

        fun Iterable<String>.howManyStartWithPrefix(prefix: String) = count { it.startsWith(prefix) }

        fun String.randomPrefix() = substring(0, random.nextInt(length))

        val trie = Trie()
        val pseudo = mutableListOf<String>()
        for (i in 1..10000) {
            val elem = random.getRandomString(100)
            trie.add(elem)
            pseudo.add(elem)
        }

        for (i in 1..1000) {
            val prefix = pseudo.random(random).randomPrefix()
            assertEquals(pseudo.howManyStartWithPrefix(prefix), trie.howManyStartWithPrefix(prefix))
        }
    }

    @Test
    fun serialize() {
        val trie = Trie()

        for (i in 1..1000) {
            trie.add("elem $i")
        }

        var element = "elem"
        for (i in 1..1000) {
            element = "long $element"
            trie.add(element)
        }

        val bytes = MockOutputStream().use { out ->
            trie.serialize(out)
            out.toByteArray().also { assertFalse(out.closed) }
        }

        val trie2 = Trie().apply {
            add("dirt")
            MockInputStream(bytes).use {
                deserialize(it)
                assertFalse(it.closed)
            }
        }

        assertFalse("dirt" in trie2)

        for (i in 1..1000) {
            val elem = "elem $i"
            assertEquals(elem in trie, elem in trie2)
        }

        element = "elem"
        for (i in 1..1000) {
            element = "long $element"
            assertEquals(element in trie, element in trie2)
        }
    }

    @Test
    fun deserialize() {
        val random = Random(0)
        val trie = Trie()
        val pseudo = mutableListOf<String>()
        for (i in 1..1000) {
            val elem = random.getRandomString(100)
            trie.add(elem)
            pseudo.add(elem)
        }

        val bytes = MockOutputStream().use { out ->
            trie.serialize(out)
            out.toByteArray().also { assertFalse(out.closed) }
        }

        val trie2 = Trie().apply {
            add("dirt")
            MockInputStream(bytes).use {
                deserialize(it)
                assertFalse(it.closed)
            }
        }

        for (elem in pseudo) {
            assertTrue(elem in trie && elem in trie2)
        }
    }

    private class MockInputStream(bytes: ByteArray) : ByteArrayInputStream(bytes) {
        var closed = false
            private set

        override fun close() {
            super.close()
            closed = true
        }
    }

    private class MockOutputStream : ByteArrayOutputStream() {
        var closed = false
            private set

        override fun close() {
            super.close()
            closed = true
        }
    }
}
