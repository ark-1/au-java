package me.arkadybazhanov.au.java.hw2

class Maybe<out T> private constructor() {
    private var _value: T? = null

    val value get() = _value ?: throw NoSuchElementException("No value in this Maybe")

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

    fun <U> map(mapper: (T) -> U) = if (isPresent) just(mapper(_value!!)) else nothing()
}