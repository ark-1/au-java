package me.arkadybazhanov.au.java.hw2

import java.io.InputStream
import java.io.OutputStream
import java.util.StringTokenizer
import kotlin.text.Charsets.UTF_8
import kotlin.collections.Map.*

/** *Trie* data structure implementation, a container to store Strings. */
class Trie : Serializable {
    private val root = Node()

    /**
     * Adds specified [element] to the trie.
     * Complexity: linear in `element.size`.
     * @return true if it wasn't there before, false otherwise.
     */
    fun add(element: String): Boolean = if (element !in this) {
        root.add(element, 0)
        true
    } else false

    /**
     * Checks whether this trie contains specified [element]
     * Complexity: linear in `element.size`.
     */
    operator fun contains(element: String): Boolean = root.contains(element, 0)

    /**
     * Removes specified [element] from the trie.
     * Complexity: linear in `element.size`.
     * @return true if it was there before, false otherwise.
     */
    fun remove(element: String): Boolean = if (element in this) {
        root.remove(element, 0)
        true
    } else false

    /** Returns number of elements in the trie. */
    val size: Int get() = root.size

    /**
     * Returns number of elements in the trie, that start with given [prefix].
     * Complexity: linear in `prefix.size`.
     */
    fun howManyStartWithPrefix(prefix: String) = root.getSize(prefix, 0)

    override fun serialize(out: OutputStream) {
        out.write(root.serialize().toByteArray(UTF_8))
    }

    override fun deserialize(`in`: InputStream) {
        root.deserialize(`in`.readBytes().toString(UTF_8))
    }

    private class Node {
        fun serialize(): String = StringBuilder().also { output ->
            val stack = mutableListOf(
                    iterator<Entry<Char?, Node>> {
                        yield(object : Entry<Char?, Node> {
                            override val key: Char? = null
                            override val value = this@Node
                        })
                    }
            )

            while (stack.isNotEmpty()) {
                val lastIterator = stack.last()
                if (!lastIterator.hasNext()) {
                    stack.removeAt(stack.lastIndex)
                    continue
                }

                val (c, node) = lastIterator.next()
                if (c == ' ') {
                    output.append("space ")
                } else if (c != null) {
                    output.append("$c ")
                }
                output.append("${node.size} ${node.isTerminal} ${node.children.size} ")
                stack += (node.children as Map<Char, Node>).iterator()
            }
        }.toString()

        fun deserialize(serialized: String) {
            children.clear()

            val tokenizer = StringTokenizer(serialized, " ")

            size = tokenizer.nextToken().toInt()
            isTerminal = tokenizer.nextToken()!!.toBoolean()

            val stack = mutableListOf(this to tokenizer.nextToken().toInt())

            while (stack.isNotEmpty()) {
                val (node, left) = stack.last()
                stack.removeAt(stack.lastIndex)
                if (left == 0) continue
                stack += node to left - 1
                val token = tokenizer.nextToken()

                node.children[if (token == "space") ' ' else token[0]] = Node().also { child ->
                    child.size = tokenizer.nextToken().toInt()
                    child.isTerminal = tokenizer.nextToken()!!.toBoolean()
                    stack += child to tokenizer.nextToken().toInt()
                }
            }
        }

        var isTerminal: Boolean = false
        val children = mutableMapOf<Char, Node>()
        var size = 0
    }

    private tailrec fun Node.add(element: String, depth: Int) {
        size++
        if (depth == element.length) {
            isTerminal = true
            return
        }
        children.getOrPut(element[depth], ::Node).add(element, depth + 1)
    }

    private tailrec fun Node.contains(element: String, depth: Int): Boolean {
        return if (depth == element.length) {
            isTerminal
        } else {
            (children[element[depth]] ?: return false).contains(element, depth + 1)
        }
    }

    private tailrec fun Node.remove(element: String, depth: Int) {
        size--
        if (depth == element.length) {
            isTerminal = false
            return
        }

        val node = children.getValue(element[depth])
        if (node.size == 1) children.remove(element[depth])
        node.remove(element, depth + 1)
    }

    private tailrec fun Node.getSize(prefix: String, depth: Int): Int {
        return if (depth == prefix.length) {
            size
        } else {
            (children[prefix[depth]] ?: return 0).getSize(prefix, depth + 1)
        }
    }
}
