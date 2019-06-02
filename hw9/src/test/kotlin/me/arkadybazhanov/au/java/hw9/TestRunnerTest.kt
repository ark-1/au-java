package me.arkadybazhanov.au.java.hw9

import me.arkadybazhanov.au.java.hw9.TestResult.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.lang.RuntimeException

class TestRunnerTest {
    companion object {
        var beforeClass = false
        var afterClass = false
        var beforeCount = 0
        var afterCount = 0
    }

    class CorrectTests {
        @BeforeClass
        fun setUp() {
            beforeClass = true
        }

        @AfterClass
        fun tearDown() {
            afterClass = true
        }

        @Before
        fun before() {
            beforeCount++
        }

        @After
        fun after() {
            afterCount++
        }

        @me.arkadybazhanov.au.java.hw9.Test
        fun success() {}

        @me.arkadybazhanov.au.java.hw9.Test
        fun failure() {
            error("failure")
        }

        @me.arkadybazhanov.au.java.hw9.Test(RuntimeException::class)
        fun expected() {
            error("failure")
        }

        @me.arkadybazhanov.au.java.hw9.Test(RuntimeException::class)
        fun unexpected() {
            throw Throwable("surprise")
        }

        @me.arkadybazhanov.au.java.hw9.Test(ignore = "because")
        fun ignored() {}
    }

    @Test
    fun testCorrect() {
        val testResults = runTests(CorrectTests::class.java)

        assertEquals(5, testResults.size)
        assertEquals(4, beforeCount)
        assertEquals(4, afterCount)
        assertTrue(beforeClass)
        assertTrue(afterClass)

        assertTrue(testResults.any { it == Success(CorrectTests::success.name) })
        assertTrue(testResults.any {
            it is Failure && it.testName == CorrectTests::failure.name &&
                it.error::class == IllegalStateException::class && it.error.message == "failure"
        })
        assertTrue(testResults.any { it == Success(CorrectTests::expected.name) })
        assertTrue(testResults.any {
            it is Failure && it.testName == CorrectTests::unexpected.name &&
                it.error::class == Throwable::class && it.error.message == "surprise"
        })
        assertTrue(testResults.any { it == Ignored(CorrectTests::ignored.name, "because") })
    }

    class IncorrectTests1 {
        @me.arkadybazhanov.au.java.hw9.Test
        fun args(i: Int) {}
    }

    class IncorrectTests2 {
        @Before
        fun args(i: Int) {}
    }

    class IncorrectTests3 {
        @After
        fun args(i: Int) {}
    }

    class IncorrectTests4 {
        @BeforeClass
        fun args(i: Int) {}
    }

    class IncorrectTests5 {
        @AfterClass
        fun args(i: Int) {}
    }

    @Test
    fun testIncorrect() {
        assertThrows(IllegalArgumentException::class.java) {
            runTests(IncorrectTests1::class.java)
        }
        assertThrows(IllegalArgumentException::class.java) {
            runTests(IncorrectTests2::class.java)
        }
        assertThrows(IllegalArgumentException::class.java) {
            runTests(IncorrectTests3::class.java)
        }
        assertThrows(IllegalArgumentException::class.java) {
            runTests(IncorrectTests4::class.java)
        }
        assertThrows(IllegalArgumentException::class.java) {
            runTests(IncorrectTests5::class.java)
        }
    }

    class ExceptionTest {
        @BeforeClass
        fun exception() {
            throw NoSuchElementException()
        }
    }

    @Test
    fun testException() {
        assertThrows(IllegalArgumentException::class.java) {
            runTests(IncorrectTests5::class.java)
        }
    }
}