package me.arkadybazhanov.au.java.hw1;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.I_Result;

import java.util.function.Supplier;

import static org.openjdk.jcstress.annotations.Expect.*;

@JCStressTest
@Outcome(id = "1", expect = FORBIDDEN, desc = "Not equal")
@Outcome(id = "2", expect = ACCEPTABLE,  desc = "Equal")
@State
public class StressTest {
    private String value1;
    private String value2;

    private int count;

    private Supplier<String> supplier = new Supplier<String>() {
        @Override
        public String get() {
            ++count;
            return "" + count;
        }
    };

    private Lazy<String> lazy = LazyFactory.createLazySynchronized(supplier);

    @Actor
    public void actor1() {
        value1 = lazy.get();
    }

    @Actor
    public void actor2() {
        value2 = lazy.get();
    }

    @Arbiter
    public void arbiter(I_Result r) {
        r.r1 = value1.equals(value2) && count == 1 ? 2 : 1;
    }
}