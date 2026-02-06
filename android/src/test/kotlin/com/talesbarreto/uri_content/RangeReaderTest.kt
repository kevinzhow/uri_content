package com.talesbarreto.uri_content

import java.io.ByteArrayInputStream
import java.io.InputStream
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RangeReaderTest {
    @Test
    fun readFromInputStream_readsExpectedRange() {
        val input = ByteArrayInputStream(byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9))

        val result = RangeReader.readFromInputStream(input, start = 2, length = 4)

        assertContentEquals(byteArrayOf(2, 3, 4, 5), result)
    }

    @Test
    fun readFromInputStream_returnsPartialDataAtEnd() {
        val input = ByteArrayInputStream(byteArrayOf(0, 1, 2, 3, 4))

        val result = RangeReader.readFromInputStream(input, start = 3, length = 5)

        assertContentEquals(byteArrayOf(3, 4), result)
    }

    @Test
    fun readFromInputStream_returnsNullWhenStartPastEof() {
        val input = ByteArrayInputStream(byteArrayOf(0, 1, 2))

        val result = RangeReader.readFromInputStream(input, start = 8, length = 2)

        assertNull(result)
    }

    @Test
    fun readFromInputStream_handlesSkipReturningZero() {
        val input = ZeroSkipInputStream(byteArrayOf(0, 1, 2, 3, 4, 5))

        val result = RangeReader.readFromInputStream(input, start = 3, length = 2)

        assertContentEquals(byteArrayOf(3, 4), result)
        assertEquals(3, input.skipCallCount)
    }
}

private class ZeroSkipInputStream(private val data: ByteArray) : InputStream() {
    private var index = 0
    var skipCallCount = 0
        private set

    override fun read(): Int {
        if (index >= data.size) {
            return -1
        }
        return data[index++].toInt() and 0xFF
    }

    override fun skip(n: Long): Long {
        skipCallCount += 1
        return 0
    }
}
