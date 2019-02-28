package me.arkadybazhanov.au.java.hw2

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


interface Serializable {
    /**
     * Writes serialized data into specified [OutputStream]. Can be later
     * deserialized with [deserialize].
     */
    @Throws(IOException::class)
    fun serialize(out: OutputStream)

    /**
     * Deserializes previously serialized by [serialize] data from specified
     * [InputStream] into this object, discarding previously contained data.
     */
    @Throws(IOException::class)
    fun deserialize(`in`: InputStream)
}
