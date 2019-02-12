package me.arkadybazhanov.au.java.hw2

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class MaybeTest {

    @Test
    fun getValue() {
        val value = Maybe.just(5)
        assertEquals(5, value.value)
        val nothing: Maybe<Int> = Maybe.nothing()
        assertThrows(NoSuchElementException::class.java) { nothing.value }
        val maybeNull: Maybe<Int?> = Maybe.just(null)
        assertNull(maybeNull.value)
    }

    @Test
    fun isPresent() {
        val value = Maybe.just(5)
        assertTrue(value.isPresent)
        val nothing: Maybe<Int> = Maybe.nothing()
        assertFalse(nothing.isPresent)
        val maybeNull: Maybe<Int?> = Maybe.just(null)
        assertTrue(maybeNull.isPresent)
    }

    @Test
    fun map() {
        val value = Maybe.just(5)
        assertEquals(25, value.map { it * it }.value)
        val nothing: Maybe<Int> = Maybe.nothing()
        assertFalse(nothing.map { it * it }.isPresent)
        val maybeNull: Maybe<Int?> = Maybe.just(null)
        assertEquals(0, maybeNull.map { it ?: 0 }.value)
    }
}