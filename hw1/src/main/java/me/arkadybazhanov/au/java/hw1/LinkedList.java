package me.arkadybazhanov.au.java.hw1;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Partial implementation of <i>linked list</i> data structure.
 *
 * @param <T> the type of values to store.
 */
public final class LinkedList<T> implements Iterable<T> {

    private @Nullable Node<T> lastNode = null;
    private @Nullable Node<T> firstNode = null;
    private int size;

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list
     */
    @Contract(pure = true)
    public int size() {
        return size;
    }

    /**
     * Adds the specified value into this list and returns the {@link Node} associated with it.
     *
     * @param value the value to add.
     * @return new {@link Node}.
     */
    public @NotNull Node<T> add(T value) {
        lastNode = Node.appendTo(lastNode, value);

        size++;
        if (firstNode == null) {
            firstNode = lastNode;
        }

        return lastNode;
    }

    public void remove(@NotNull Node<T> node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        }

        size--;
        if (node == lastNode) {
            lastNode = node.prev;
        }
        if (node == firstNode) {
            firstNode = node.next;
        }

        node.value = null;
        node.prev = null;
    }

    /**
     * Returns an {@link Iterator} over the list elements. Besides required
     * {@link Iterator#hasNext()} and {@link Iterator#next()} methods
     * {@link Iterator#remove()} method can be called to remove the last
     * element returned by {@link Iterator#next()} from the list.
     *
     * @return an {@link Iterator} over the list elements.
     */
    @Contract(value = " -> new", pure = true)
    @Override
    public @NotNull Iterator<T> iterator() {
        return new Iterator<>() {
            private @NotNull Node<T> current = new Node<>(null, firstNode, null);

            /**
             * @inheritDoc
             */
            @Override
            public boolean hasNext() {
                return current.next != null;
            }

            /**
             * @inheritDoc
             */
            @Override
            public T next() {
                if (current.next == null) throw new NoSuchElementException();
                current = current.next;
                return current.value;
            }

            /**
             * @inheritDoc
             */
            @Override
            public void remove() {
                LinkedList.this.remove(current);
            }
        };
    }

    /**
     * The node in linked list, containing its element.
     * Can be used as a pointer to this element.
     *
     * @param <T> the type of element.
     */
    public static final class Node<T> {
        private @Nullable Node<T> prev;
        private @Nullable Node<T> next;
        public T value;

        private Node(@Nullable Node<T> prev, @Nullable Node<T> next, T value) {
            this.prev = prev;
            this.next = next;
            this.value = value;
        }

        private static <T> @NotNull Node<T> appendTo(@Nullable Node<T> last, T value) {
            var node = new Node<>(last, null, value);

            if (last != null) {
                assert last.next == null;
                last.next = node;
            }

            return node;
        }
    }
}
