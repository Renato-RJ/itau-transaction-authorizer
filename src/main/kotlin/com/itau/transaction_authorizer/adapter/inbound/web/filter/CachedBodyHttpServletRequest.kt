package com.itau.transaction_authorizer.adapter.inbound.web.filter

import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class CachedBodyHttpServletRequest(request: HttpServletRequest) : HttpServletRequestWrapper(request) {

    private val cachedBody: ByteArray = request.inputStream.readBytes()

    override fun getInputStream(): ServletInputStream {
        val byteArrayInputStream = ByteArrayInputStream(cachedBody)

        return object : ServletInputStream() {
            override fun read(): Int = byteArrayInputStream.read()

            override fun isFinished(): Boolean = byteArrayInputStream.available() == 0

            override fun isReady(): Boolean = true

            override fun setReadListener(readListener: ReadListener?) {
                // Synchronous body caching does not support async read listeners.
            }
        }
    }

    override fun getReader(): BufferedReader {
        val charset = resolveCharset()
        return BufferedReader(InputStreamReader(inputStream, charset))
    }

    fun cachedBodyAsString(): String = cachedBody.toString(resolveCharset())

    private fun resolveCharset(): Charset {
        val encoding = characterEncoding ?: return StandardCharsets.UTF_8
        return runCatching { Charset.forName(encoding) }.getOrDefault(StandardCharsets.UTF_8)
    }
}

