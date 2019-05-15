package me.arkadybazhanov.au.java.test3

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class MD5Test {
    @Test
    fun structuralEquals() {
        val path1 = Paths.get(javaClass.getResource("/testDir2").toURI())
        val path2 = Paths.get(javaClass.getResource("/testDir1").toURI()).resolve("testDir2")
        assertArrayEquals(md5SingleThread(path1), md5SingleThread(path2))
    }

    @Test
    fun structuralNotEquals() {
        val path1 = Paths.get(javaClass.getResource("/testDir1").toURI())
        val path2 = Paths.get(javaClass.getResource("/testDir1").toURI()).resolve("testDir2")
        assertTrue(!md5SingleThread(path1).contentEquals(md5SingleThread(path2)))
    }

    @Test
    fun methodsEquals() {
        val path = Paths.get(javaClass.getResource("/testDir2").toURI())
        assertArrayEquals(md5SingleThread(path), md5forkJoin(path))
    }
}