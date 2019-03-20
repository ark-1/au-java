package me.arkadybazhanov.au.java.hw2


import me.arkadybazhanov.au.java.test2.Injector
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*


internal class InjectorTest {
    interface B
    interface C

    class A : B, C {
        init {
            constructedTimes++
        }

        companion object {
            var constructedTimes = 0
        }
    }

    @Suppress("UNUSED_PARAMETER")
    class D(b: B, c: C)

    @Test
    fun simple() {
        A.constructedTimes = 0
        assertTrue(Injector.initialize(A::class.java, A::class.java) is A)
        assertEquals(1, A.constructedTimes)
    }

    @Test
    fun complex() {
        A.constructedTimes = 0
        assertTrue(Injector.initialize(D::class.java, A::class.java, D::class.java) is D)
        assertEquals(1, A.constructedTimes)
    }

    @Suppress("UNUSED_PARAMETER")
    class X(y: Y)
    @Suppress("UNUSED_PARAMETER")
    class Y(x: X)

    @Test
    fun cyclic() {
        assertThrows(Injector.InjectorException.InjectionCycleException::class.java) {
            Injector.initialize(X::class.java, X::class.java, Y::class.java)
        }
    }

    class N
    @Suppress("UNUSED_PARAMETER")
    class H(any: Any)

    @Test
    fun ambiguous() {
        assertThrows(Injector.InjectorException.AmbiguousImplementationException::class.java) {
            Injector.initialize(H::class.java, H::class.java, N::class.java)
        }
    }
}
