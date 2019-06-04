package me.arkadybazhanov.au.java.test3

import java.nio.file.*
import java.security.*
import java.util.concurrent.*
import kotlin.system.measureTimeMillis

private fun MessageDigest.md5File(path: Path) {
    Files.newInputStream(path).use {
        val stream = DigestInputStream(it, this)
        while (stream.read() != -1) {
            // do nothing
        }
    }
}

private inline fun withMd5(block: MessageDigest.() -> Unit): ByteArray {
    return MessageDigest.getInstance("MD5").apply { block() }.digest()
}

/** Hashes given [path] with MD5 algorithm */
fun md5SingleThread(path: Path): ByteArray = withMd5 {
    if (!Files.isDirectory(path)) {
        md5File(path)
    } else {
        update(path.fileName.toString().toByteArray())
        for (child in Files.list(path)) {
            update(md5SingleThread(child))
        }
    }
}

private class MD5ForkJoinTask(private val path: Path) : RecursiveTask<ByteArray>() {
    override fun compute(): ByteArray = withMd5 {
        if (!Files.isDirectory(path)) {
            md5File(path)
        } else {
            update(path.fileName.toString().toByteArray())
            val subTasks = mutableListOf<ForkJoinTask<ByteArray>>()
            for (child in Files.list(path)) {
                val task = MD5ForkJoinTask(child)
                task.fork()
                subTasks += task
            }

            for (task in subTasks) {
                update(task.join())
            }
        }
    }
}

/** Hashes given [path] with MD5 algorithm in parallel using ForkJoinPool */
fun md5forkJoin(path: Path): ByteArray {
    val pool = ForkJoinPool()
    val result = pool.invoke(MD5ForkJoinTask(path))
    pool.shutdown()
    return result
}

fun main(args: Array<String>) {
    val path = args[0]

    val singleThreadTime = measureTimeMillis {
        println("md5: ${md5SingleThread(Paths.get(path))}")
    }
    println("Single thread time: $singleThreadTime" + "ms")

    val forkJoinTime = measureTimeMillis {
        println("md5: ${md5forkJoin(Paths.get(path))}")
    }
    println("ForkJoin time: $forkJoinTime" + "ms")
}
