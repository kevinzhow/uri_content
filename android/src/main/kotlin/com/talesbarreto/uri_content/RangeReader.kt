package com.talesbarreto.uri_content

import java.io.InputStream

internal object RangeReader {
    fun readFromInputStream(inputStream: InputStream, start: Long, length: Long): ByteArray? {
        require(start >= 0) { "start must be non-negative: $start" }
        require(length >= 0) { "length must be non-negative: $length" }
        require(length <= Int.MAX_VALUE.toLong()) { "length is too large: $length" }

        if (length == 0L) {
            return ByteArray(0)
        }

        var skipped = 0L
        while (skipped < start) {
            val skippedNow = inputStream.skip(start - skipped)
            if (skippedNow > 0) {
                skipped += skippedNow
                continue
            }

            // Some InputStream implementations may return 0 from skip even before EOF.
            if (inputStream.read() == -1) {
                return null
            }
            skipped += 1
        }

        val expectedLength = length.toInt()
        val buffer = ByteArray(expectedLength)
        var totalRead = 0
        while (totalRead < expectedLength) {
            val bytesRead = inputStream.read(buffer, totalRead, expectedLength - totalRead)
            if (bytesRead <= 0) {
                break
            }
            totalRead += bytesRead
        }

        if (totalRead == 0) {
            return null
        }

        return if (totalRead == expectedLength) {
            buffer
        } else {
            buffer.copyOf(totalRead)
        }
    }
}
