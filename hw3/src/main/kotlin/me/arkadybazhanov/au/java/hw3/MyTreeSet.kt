package me.arkadybazhanov.au.java.hw3

import java.util.TreeSet

interface MyTreeSet<E> : Set<E> {

    /** [TreeSet.descendingIterator]  */
    fun descendingIterator(): Iterator<E>

    /** [TreeSet.descendingSet]  */
    fun descendingSet(): MyTreeSet<E>


    /** [TreeSet.first]  */
    fun first(): E

    /** [TreeSet.last]  */
    fun last(): E


    /** [TreeSet.lower]  */
    fun lower(e: E): E?

    /** [TreeSet.floor]  */
    fun floor(e: E): E?


    /** [TreeSet.ceiling]  */
    fun ceiling(e: E): E?

    /** [TreeSet.higher]  */
    fun higher(e: E): E?
}
