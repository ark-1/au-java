package me.arkadybazhanov.au.java.hw1;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;


/**
 * Container for storing mapping from non-{@code null} {@link String} keys to
 * non-{@code null} {@link String} values. It is an implementation of
 * <i>hashtable</i> data structure with <i>separate chaining</i> as collision
 * resolution method.
 */
public final class Hashtable {

    private final static double MAX_LOAD_FACTOR = 0.75;
    private final static int GROW_FACTOR = 2;

    private @NotNull Bucket[] buckets;
    private @NotNull LinkedList<@NotNull Entry> entries = new LinkedList<>();

    /**
     * Constructs new empty hashtable with specified number of buckets.
     *
     * @param bucketNumber number of buckets.
     */
    public Hashtable(int bucketNumber) {
        buckets = new Bucket[bucketNumber];
        Arrays.setAll(buckets, i -> new Bucket());
    }

    /**
     * Returns the number of keys in this hashtable.
     *
     * @return the number of keys in this hashtable.
     */
    @Contract(pure = true)
    public int size() {
        return entries.size();
    }

    private @NotNull Bucket bucket(@NotNull String key) {
        return buckets[Math.floorMod(key.hashCode(), buckets.length)];
    }

    /**
     * Checks whether the key is present in this hashtable.
     *
     * @param key the key to check
     * @return {@code true} if and only if the specified parameter
     * is a key in this hashtable.
     */
    public boolean contains(@NotNull String key) {
        for (var entry : bucket(key)) {
            if (entry.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or
     * {@code null} if this map contains no mapping for the key.
     */
    public @Nullable String get(@NotNull String key) {
        for (var entry : bucket(key)) {
            if (entry.key.equals(key)) {
                return entry.value;
            }
        }
        return null;
    }

    /**
     * Maps the specified {@code key} to the specified
     * {@code value} in this hashtable. Neither the key nor the
     * value can be {@code null}. <p>
     * <p>
     * The value can be retrieved by calling the {@code get} method
     * with a key that is equal to the original key.
     *
     * @param key   the hashtable key
     * @param value the value
     * @return the previous value of the specified key in this hashtable,
     * or {@code null} if it did not have one
     * @see #get(String)
     */
    public @Nullable String put(@NotNull String key, @NotNull String value) {
        var bucket = bucket(key);
        for (var entry : bucket) {
            if (entry.key.equals(key)) {
                var res = entry.value;
                entry.value = value;
                return res;
            }
        }

        bucket.nodes.add(entries.add(new Entry(key, value)));

        if (entries.size() >= buckets.length * MAX_LOAD_FACTOR) {
            grow();
        }
        return null;
    }

    private void grow() {
        var newTable = new Hashtable(buckets.length * GROW_FACTOR);
        for (var entry : entries) {
            newTable.put(entry.key, entry.value);
        }
        entries = newTable.entries;
        buckets = newTable.buckets;
    }

    /**
     * Removes the key (and its corresponding value) from this
     * hashtable. This method does nothing if the key is not in the hashtable.
     *
     * @param key the key that needs to be removed
     * @return the value to which the key had been mapped in this hashtable,
     * or {@code null} if the key did not have a mapping
     */
    public @Nullable String remove(@NotNull String key) {
        var bucket = bucket(key);
        for (var iter = bucket.iterator(); iter.hasNext(); ) {
            var entry = iter.next();
            if (entry.key.equals(key)) {
                iter.remove();
                return entry.value;
            }
        }

        return null;
    }

    /**
     * Clears this hashtable so that it contains no keys.
     */
    public void clear() {
        buckets = new Bucket[buckets.length];
        Arrays.setAll(buckets, i -> new Bucket());
        entries = new LinkedList<>();
    }

    private static final class Entry {
        public @NotNull String key;
        public @NotNull String value;

        public Entry(@NotNull String key, @NotNull String value) {
            this.key = key;
            this.value = value;
        }
    }

    private final class Bucket implements Iterable<@NotNull Entry> {
        public final @NotNull LinkedList<LinkedList.@NotNull Node<@NotNull Entry>> nodes = new LinkedList<>();

        @Contract(value = " -> new", pure = true)
        @Override
        public @NotNull Iterator<@NotNull Entry> iterator() {
            var iter = nodes.iterator();
            return new Iterator<>() {
                LinkedList.@Nullable Node<@NotNull Entry> last = null;

                @Override
                public boolean hasNext() {
                    return iter.hasNext();
                }

                @Override
                public @NotNull Entry next() {
                    last = iter.next();
                    return last.value;
                }

                @Override
                public void remove() {
                    if (last == null) {
                        throw new IllegalStateException(
                                "`next` method has not yet been called, or the " +
                                        "`remove` method has already been called " +
                                        "after the last call to the `next` method"
                        );
                    }
                    entries.remove(last);
                    iter.remove();
                    last = null;
                }
            };
        }
    }
}