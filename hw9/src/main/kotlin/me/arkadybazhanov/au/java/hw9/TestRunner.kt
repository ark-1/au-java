package me.arkadybazhanov.au.java.hw9

import me.arkadybazhanov.au.java.hw9.TestResult.*
import me.arkadybazhanov.au.java.hw9.TestResult.Timed.*
import java.io.*
import java.lang.reflect.*
import java.net.URLClassLoader
import java.nio.file.Files
import java.util.jar.*
import kotlin.math.roundToLong

/**
 * Describes test result.
 * [testName]: name of test function
 */
sealed class TestResult {
    abstract val testName: String

    /**
     * Describes test result, for which time is applicable.
     * [timeNano]: time that test ran
     */
    sealed class Timed : TestResult() {
        abstract val timeNano: Long

        /**
         * Describes passed test
         * @param testName name of test function
         * @param timeNano time that test ran
         */
        data class Success(override val testName: String, override val timeNano: Long) : Timed()

        /**
         * Describes failed test
         * @param testName name of test function
         * @param error error which caused test failure
         * @param timeNano time that test ran
         */
        data class Failure(
            override val testName: String,
            val error: Throwable,
            override val timeNano: Long
        ) : Timed()
    }

    /**
     * Describes failed test
     * @param testName name of test function
     * @param why why the test was ignored
     */
    data class Ignored(override val testName: String, val why: String) : TestResult()
}

private inline fun <reified T : Annotation> Array<Method>.filterAnnotated(): List<Method> = filter {
    it.isAnnotationPresent(T::class.java)
}.apply {
    forEach {
        require(it.parameterCount == 0) {
            "Methods annotated with ${T::class.simpleName} should have no parameters. Method name: ${it.name}"
        }
    }
}

private inline fun <reified T : Annotation> List<Method>.invokeAll(obj: Any) {
    for (method in this) {
        try {
            method(obj)
        } catch (e: InvocationTargetException) {
            throw IllegalStateException(
                "Method annotated with ${T::class.simpleName} failed with exception ${e.targetException}",
                e.targetException
            )
        }
    }
}

/**
 * Runs all methods of given class [testClass] annotated with @[Test].
 * Runs methods annotated with @[Before] before each test.
 * Runs methods annotated with @[After] after each test.
 * Runs methods annotated with @[BeforeClass] before all tests.
 * Runs methods annotated with @[AfterClass] after all test.
 */
fun runTests(testClass: Class<*>): List<TestResult> {
    val testClassInstance = requireNotNull(testClass.newInstance()) {
        "Test class should have public nullary constructor"
    }

    val methods = testClass.methods

    val tests = methods.filterAnnotated<Test>()
    val before = methods.filterAnnotated<Before>()
    val after = methods.filterAnnotated<After>()
    val beforeClass = methods.filterAnnotated<BeforeClass>()
    val afterClass = methods.filterAnnotated<AfterClass>()

    beforeClass.invokeAll<BeforeClass>(testClassInstance)

    val results = mutableListOf<TestResult>()
    for (test in tests) {
        val annotation = test.getAnnotation(Test::class.java)!!

        if (annotation.ignore.isNotEmpty()) {
            results += Ignored(test.name, annotation.ignore)
            continue
        }

        before.invokeAll<Before>(testClassInstance)
        val start = System.nanoTime()

        results += try {
            test(testClassInstance)
            val end = System.nanoTime() - start
            Success(test.name, end)
        } catch (e: InvocationTargetException) {
            val end = System.nanoTime() - start
            if (!annotation.expected.java.isAssignableFrom(e.targetException.javaClass)) {
                results += Failure(test.name, e.targetException, end)
                continue
            } else Success(test.name, end)
        } finally {
            after.invokeAll<After>(testClassInstance)
        }
    }

    afterClass.invokeAll<AfterClass>(testClassInstance)

    return results
}

fun loadTestClasses(file: File): List<Class<*>> {
    val testClasses = mutableListOf<String>()

    val url = when (file.extension) {
        "jar" -> {
            for (entry in JarFile(file).entries()) {
                if (entry.name.endsWith(".class")) {
                    testClasses += entry.name.removeSuffix(".class").replace('/', '.')
                }
            }

            file
        }
        "class" -> {
            testClasses += file.nameWithoutExtension
            Files.createTempDirectory("MyJUnit").toFile().also { file.copyTo(it) }
        }
        else -> throw IllegalArgumentException("File must be either a .jar or a .class file. File name: ${file.name}")
    }.toURI().toURL()

    val classLoader = URLClassLoader(arrayOf(url))
    return testClasses.map(classLoader::loadClass).filter {
        it.methods.any { method -> method.isAnnotationPresent(Test::class.java) }
    }
}

private val Timed.millis get() = (timeNano / 1_000.0).roundToLong() / 1_000.0

fun main(vararg args: String) {
    require(args.size == 1) { "Accepts single argument (path). args: ${args.contentToString()}" }
    val file = File(args.single())
    require(file.isFile && file.extension in setOf("class", "jar")) {
        "File should be a normal file (.class or .jar). File: ${file.path}"
    }
    val results = loadTestClasses(file).flatMap { runTests(it) }
    println("Passed: " + results.count { it is Success })
    println("Ignored: " + results.count { it is Ignored })
    println("Failed: " + results.count { it is Failure })
    println()
    println("Tests:")
    results.forEach {
        var message = "${it.testName}: ${it::class.simpleName}"
        if (it is Timed) {
            message += " in ${it.millis}ms"
        }

        println(message)
    }
}
