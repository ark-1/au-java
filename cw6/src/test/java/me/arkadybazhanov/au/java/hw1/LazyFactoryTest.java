package me.arkadybazhanov.au.java.hw1;

import java.util.function.Function;
import java.util.function.Supplier;

import static me.arkadybazhanov.au.java.hw1.LazyFactory.createLazySingleThreaded;
import static org.junit.jupiter.api.Assertions.*;

class LazyFactoryTest {

    @org.junit.jupiter.api.Test
    void testSingleThreadLazy() {
        testLazy(LazyFactory::createLazySingleThreaded);
    }

    @org.junit.jupiter.api.Test
    void testMultiThreaded() {
        testLazy(LazyFactory::createLazySynchronized);
    }

    @org.junit.jupiter.api.Test
    void testLockFree() {
        testLazy(LazyFactory::createLazyLockFree);
    }

    private void testLazy(Function<Supplier<String>, Lazy<String>> lazyProvider) {
        var supplier = new Supplier<String>() {
            int count;

            @Override
            public String get() {
                ++count;
                return "" + count;
            }
        };
        var lazy = lazyProvider.apply(supplier);
        assertEquals("1", lazy.get());
        assertEquals("1", lazy.get());
        assertEquals("1", lazy.get());
        assertEquals(1, supplier.count);
    }
}