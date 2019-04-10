package me.arkadybazhanov.au.java.hw1;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class LazyFactory {
    public static <T> Lazy<T> createLazy1(Supplier<? extends T> supplier) {
        return new Lazy<>() {
            private T value = null;
            private boolean calculated = false;

            @Override
            public T get() {
                if (!calculated) {
                    value = supplier.get();
                    calculated = true;
                }
                return value;
            }
        };
    }

    public static <T> Lazy<T> createLazy2(Supplier<? extends T> supplier) {
        return new Lazy<T>() {
            private T value = null; // TODO
            private volatile boolean calculated = false;

            @Override
            public T get() {
                if (!calculated) {
                    synchronized (this) {
                        if (!calculated) {
                            value = supplier.get();
                            calculated = true;
                        }
                    }
                }
                return value;
            }
        };
    }

//    private static class Box<T> {
//        public T value;
//
//        public Box(T value) {
//            this.value = value;
//        }
//    }

    public static <T> Lazy<T> createLazyLockFree(Supplier<? extends T> supplier) {
        return new Lazy<>() {
            private volatile AtomicReference<Optional<T>> value = new AtomicReference<>(null);

            @Override
            public T get() {
                //noinspection OptionalGetWithoutIsPresent
                return value.updateAndGet(x -> Objects.requireNonNullElseGet(x, () -> Optional.of(supplier.get()))).get();
            }
        };
    }

}
