package me.arkadybazhanov.au.java.hw1;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class LazyFactory {
    public static <T> Lazy<T> createLazySingleThreaded(Supplier<? extends T> supplier) {
        return new Lazy<>() {

            private Box<T> value = null;

            @Override
            public T get() {
                if (value == null) {
                    value = new Box<>(supplier.get());
                }
                return value.value;
            }
        };
    }

    public static <T> Lazy<T> createLazySynchronized(Supplier<? extends T> supplier) {
        return new Lazy<>() {
            private Box<T> value = null;

            @Override
            public T get() {
                if (value == null) {
                    synchronized (this) {
                        if (value == null) {
                            value = new Box<>(supplier.get());
                        }
                    }
                }
                return value.value;
            }
        };
    }

    public static <T> Lazy<T> createLazyLockFree(Supplier<? extends T> supplier) {
        return new Lazy<>() {
            private volatile AtomicReference<Box<T>> value = new AtomicReference<>(null);

            @Override
            public T get() {
                return value.updateAndGet(x -> Objects.requireNonNullElseGet(x, () -> new Box<>(supplier.get()))).value;
            }
        };
    }

    private final static class Box<T> {
        public T value;

        public Box(T value) {
            this.value = value;
        }
    }

}
