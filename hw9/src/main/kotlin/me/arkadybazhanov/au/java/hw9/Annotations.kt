package me.arkadybazhanov.au.java.hw9

import kotlin.annotation.AnnotationTarget
import kotlin.reflect.KClass

/**
 * Marks method as test.
 * @param expected error with which the class is expected to fail if it is expected to fail
 * @param ignore empty if test isn't ignored, reason for ignoring otherwise
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Test(val expected: KClass<out Throwable> = Nothing::class, val ignore: String = "")

/** Marks method to be run before each test */
@Target(AnnotationTarget.FUNCTION)
annotation class Before

/** Marks method to be run after each test */
@Target(AnnotationTarget.FUNCTION)
annotation class After

/** Marks method to be run before all tests */
@Target(AnnotationTarget.FUNCTION)
annotation class BeforeClass

/** Marks method to be run after all tests */
@Target(AnnotationTarget.FUNCTION)
annotation class AfterClass
