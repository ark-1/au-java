package me.arkadybazhanov.au.java.hw1;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashtableTest {
    private void testGrowingSize(Hashtable table) {
        for (int i = 0; i < 1000; i++) {
            assertEquals(i, table.size());
            table.put("key " + i, "data");
        }
    }

    @Test
    void size() {
        var table = new Hashtable(10);
        testGrowingSize(table);
        for (int i = 1000 - 1; i >= 0; i--) {
            assertEquals(i + 1, table.size());
            table.remove("key " + i);
        }
    }

    @Test
    void contains() {
        var table = new Hashtable(10);
        for (int i = 0; i < 1000; i++) {
            assertFalse(table.contains("key " + i));
            table.put("key " + i, "data");
            assertTrue(table.contains("key " + i));
        }
        for (int i = 1000 - 1; i >= 0; i--) {
            assertTrue(table.contains("key " + i));
            table.remove("key " + i);
            assertFalse(table.contains("key " + i));
        }
    }

    private void testGetPutRemove() {
        var table = new Hashtable(10);
        for (int i = 0; i < 1000; i++) {
            var key = "key " + i;
            var value = "data " + i;

            assertNull(table.get(key));
            table.put(key, value);
            assertEquals(value, table.get(key));
        }
        for (int i = 1000 - 1; i >= 0; i--) {
            var key = "key " + i;
            var value = "data " + i;

            assertEquals(value, table.get(key));
            table.remove(key);
            assertNull(table.get(key));
        }
    }

    @Test
    void get() {
        testGetPutRemove();
    }

    @Test
    void put() {
        testGetPutRemove();
    }

    @Test
    void remove() {
        testGetPutRemove();
    }

    @Test
    void clear() {
        var table = new Hashtable(10);
        testGrowingSize(table);
        table.clear();
        testGrowingSize(table);
    }
}