package me.arkadybazhanov.au.java.hw2

class Maybe<out T> private constructor() {
    private var _value: T? = null

    @Suppress("UNCHECKED_CAST")
    val value
        get() = if (isPresent) _value as T else throw NoSuchElementException("No value in this Maybe")

    var isPresent = false
        private set

    private constructor(value: T) : this() {
        _value = value
        this.isPresent = true
    }

    companion object {
        fun <T> just(value: T) = Maybe(value)
        fun nothing() = Maybe<Nothing>()
    }

    fun <U> map(mapper: (T) -> U) = if (isPresent) just(mapper(value)) else nothing()
}