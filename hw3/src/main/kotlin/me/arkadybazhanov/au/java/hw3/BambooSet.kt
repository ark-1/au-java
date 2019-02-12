package me.arkadybazhanov.au.java.hw3

/**
 * Ordered set implementation. It uses binary tree internally, but for
 * simplicity the nodes never have a left child.
 *
 * @constructor Creates an empty set, ordered by given [comparator].
 * There are also factory functions in companion object.
 */
class BambooSet<E>(private val comparator: Comparator<E>) : MyTreeSet<E>, AbstractMutableSet<E>() {

    // According to specification, binary tree should be used internally
    private interface BinaryTreeNode<T> {
        val parent: BinaryTreeNode<T>?
        @Suppress("unused")
        val left: BinaryTreeNode<T>?
        val right: BinaryTreeNode<T>?
        val element: T?
    }

    // So the bamboo (path) tree was used
    private sealed class BambooNode<T> : BinaryTreeNode<T> {
        final override val left: Nothing? = null
        abstract override var right: NotRoot<T>?

        class Root<T>(override var right: NotRoot<T>?) : BambooNode<T>() {
            override val parent: Nothing? = null
            override val element: Nothing? = null
        }

        class NotRoot<T>(
            override var parent: BambooNode<T>,
            override var right: NotRoot<T>?,
            override val element: T
        ) : BambooNode<T>()
    }

    override var size = 0
        private set

    /**
     * Adds the specified element to the set.
     *
     * @return `true` if the element has been added, `false` if the element is already contained in the set.
     */
    override fun add(element: E): Boolean {
        tailrec fun BambooNode<E>.add(element: E): Boolean {
            val right = right

            return if (right == null || right.element > element) {
                insertAfter(this, element)
                size++
                true
            } else if (right.element < element) {
                right.add(element)
            } else false
        }

        return root.add(element)
    }

    private var validIteratorId = 0

    /** Returns an iterator over the elements in this set in descending order */
    override fun descendingIterator(): MutableIterator<E> = BambooIterator(
        iterator<BambooNode.NotRoot<E>> {
            var node = tail
            while (node is BambooNode.NotRoot) {
                yield(node)
                node = node.parent
            }
        }
    )

    /**
     * Returns a reverse order view of the elements contained in this set.
     * The descending set is backed by this set, so changes to the set are
     * reflected in the descending set, and vice-versa.  If either set is
     * modified while an iteration over either set is in progress (except
     * through the iterator's own `remove` operation), the iterator will
     * throw [ConcurrentModificationException]
     *
     * The expression `s.descendingSet().descendingSet()` returns a
     * view of `s` essentially equivalent to `s`.
     *
     * @return a reverse order view of this set
     */
    override fun descendingSet(): MyTreeSet<E> = object : MyTreeSet<E>, MutableSet<E> by this {
        override fun descendingIterator(): Iterator<E> = this@BambooSet.iterator()
        override fun descendingSet(): MyTreeSet<E> = this@BambooSet
        override fun first(): E = this@BambooSet.last()
        override fun last(): E = this@BambooSet.first()
        override fun lower(e: E): E? = this@BambooSet.higher(e)
        override fun floor(e: E): E? = this@BambooSet.ceiling(e)
        override fun ceiling(e: E): E? = this@BambooSet.floor(e)
        override fun higher(e: E): E? = this@BambooSet.lower(e)
        override fun iterator(): MutableIterator<E> = this@BambooSet.descendingIterator()
    }

    /**
     * Returns the first (lowest) element currently in this set.
     * @throws NoSuchElementException if this set is empty
     */
    override fun first(): E = root.right?.element ?: throw NoSuchElementException()

    /**
     * Returns the last (highest) element currently in this set.
     * @throws NoSuchElementException if this set is empty
     */
    override fun last(): E = tail.element ?: throw NoSuchElementException()

    /**
     * Returns the greatest element in this set strictly less than the
     * given element [e], or `null` if there is no such element.
     */
    override fun lower(e: E): E? {
        tailrec fun BambooNode<E>.lower(e: E): E? {
            val right = right
            return if (right == null || right.element >= e) {
                element
            } else {
                right.lower(e)
            }
        }

        return root.lower(e)
    }

    /**
     * Returns the greatest element in this set less than or equal to
     * the given element [e], or `null` if there is no such element.
     */
    override fun floor(e: E): E? {
        tailrec fun BambooNode<E>.floor(e: E): E? {
            val right = right
            return if (right == null || right.element > e) {
                element
            } else {
                right.floor(e)
            }
        }

        return root.floor(e)
    }

    /**
     * Returns the least element in this set greater than or equal to
     * the given element [e], or `null` if there is no such element.
     */
    override fun ceiling(e: E): E? {
        tailrec fun BambooNode<E>.ceiling(e: E): E? {
            val element = element
            return if (element != null && element >= e) {
                element
            } else {
                this.right?.ceiling(e)
            }
        }

        return root.ceiling(e)
    }

    /**
     * Returns the least element in this set strictly greater than the
     * given element [e], or `null` if there is no such element.
     */
    override fun higher(e: E): E? {
        tailrec fun BambooNode<E>.higher(e: E): E? {
            val element = element
            return if (element != null && element > e) {
                element
            } else {
                this.right?.higher(e)
            }
        }

        return root.higher(e)
    }

    /**
     * Returns an iterator over the elements of this set that supports removing elements during iteration.
     */
    override fun iterator(): MutableIterator<E> = BambooIterator(
        iterator<BambooNode.NotRoot<E>> {
            var node = root.right
            while (node != null) {
                yield(node)
                node = node.right
            }
        }
    )

    private var root = BambooNode.Root<E>(null)
    private var tail: BambooNode<E> = root

    private operator fun E.compareTo(other: E) = comparator.compare(this, other)

    private fun insertAfter(node: BambooNode<E>, element: E) {
        val right = node.right
        val new = BambooNode.NotRoot(node, right, element)

        if (right != null) {
            right.parent = new
        } else {
            tail = new
        }

        node.right = new

        validIteratorId++
    }

    private fun remove(node: BambooNode.NotRoot<E>, iterator: BambooIterator) {
        size--
        val parent = node.parent
        val right = node.right

        parent.right = right

        if (right != null) {
            right.parent = parent
        } else {
            tail = parent
        }

        validIteratorId++
        iterator.id++
    }

    companion object Factory {
        /**
         * Constructs and returns a new empty [BambooSet], comparing elements
         * according to natural ordering. It's not a constructor, so that a
         * type constraint could be applied. When called from Kotlin, it looks
         * like a constructor call: `BambooSet<Int>()`.
         */
        operator fun <T : Comparable<T>> invoke() = BambooSet<T>(compareBy { it })

        /**
         * Constructs and returns a new [BambooSet] with the elements from
         * [elements], comparing them according to natural ordering. It's not
         * a constructor, so that a type constraint could be applied.
         */
        fun <T : Comparable<T>> from(elements: Iterable<T>) = this<T>().apply { addAll(elements) }

        /**
         * Constructs and returns a new [BambooSet] with the specified
         * [elements], comparing them according to natural ordering. It's not
         * a constructor, so that a type constraint could be applied.
         */
        fun <T : Comparable<T>> of(vararg elements: T) = this<T>().apply { addAll(elements) }
    }

    private inner class BambooIterator(private val iterator: Iterator<BambooNode.NotRoot<E>>) : MutableIterator<E> {
        var id = validIteratorId

        private fun checkValid() {
            if (id != validIteratorId) {
                throw ConcurrentModificationException()
            }
        }

        private var last: BambooNode.NotRoot<E>? = null

        override fun hasNext(): Boolean {
            checkValid()
            return iterator.hasNext()
        }

        override fun next(): E {
            checkValid()
            return iterator.next().also { last = it }.element
        }

        override fun remove() {
            val last = last ?: throw NoSuchElementException()

            checkValid()
            remove(last, this)
        }
    }
}
