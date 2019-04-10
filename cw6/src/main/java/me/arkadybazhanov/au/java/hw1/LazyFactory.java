package me.arkadybazhanov.au.java.hw1;

import java.util.concurrent.atomic.AtomicBoolean;
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

    public static <T> Lazy<T> createLazy3(Supplier<? extends T> supplier) {
        return new Lazy<T>() {
            private T value = null; // TODO
//            private volatile boolean calculated = new AtomicBoolean(false);

            @Override
            public T get() {
//                if (!calculated) {
//                    synchronized (this) {
//                        if (!calculated) {
//                            value = supplier.get();
//                            calculated = true;
//                        }
//                    }
//                }
                return value;
            }
        };
    }

}
