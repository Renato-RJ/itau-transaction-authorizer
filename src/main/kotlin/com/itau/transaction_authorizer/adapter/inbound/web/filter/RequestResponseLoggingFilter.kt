package com.itau.transaction_authorizer.adapter.inbound.web.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingResponseWrapper
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

@Component
@Order(2)
class RequestResponseLoggingFilter : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(RequestResponseLoggingFilter::class.java)

    companion object {
        private const val MAX_LOGGED_BODY_LENGTH = 2_000
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val wrappedRequest = request as? CachedBodyHttpServletRequest ?: CachedBodyHttpServletRequest(request)
        val wrappedResponse = response as? ContentCachingResponseWrapper ?: ContentCachingResponseWrapper(response)
        val startTime = System.currentTimeMillis()

        log.info(
            "request_in method={} path={} body={} traceId={}",
            wrappedRequest.method,
            wrappedRequest.requestURI,
            extractRequestBody(wrappedRequest),
            MDC.get(TraceIdFilter.TRACE_ID_MDC_KEY)
        )

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse)
        } finally {
            val durationMs = System.currentTimeMillis() - startTime
            log.info(
                "request_out method={} path={} status={} body={} duration_ms={} traceId={}",
                wrappedRequest.method,
                wrappedRequest.requestURI,
                wrappedResponse.status,
                extractResponseBody(wrappedResponse),
                durationMs,
                MDC.get(TraceIdFilter.TRACE_ID_MDC_KEY)
            )
            wrappedResponse.copyBodyToResponse()
        }
    }

    private fun extractRequestBody(request: CachedBodyHttpServletRequest): String {
        val body = normalizeBodyForLog(request.cachedBodyAsString())
        return formatBodyForLog(body)
    }

    private fun extractResponseBody(response: ContentCachingResponseWrapper): String {
        val body = response.contentAsByteArray
            .takeIf { it.isNotEmpty() }
            ?.toString(resolveCharset(response))
            ?.let(::normalizeBodyForLog)
            .orEmpty()

        return formatBodyForLog(body)
    }

    private fun formatBodyForLog(content: String): String {
        if (content.isBlank()) {
            return "-"
        }

        return if (content.length > MAX_LOGGED_BODY_LENGTH) {
            "${content.take(MAX_LOGGED_BODY_LENGTH)}...[truncated]"
        } else {
            content
        }
    }

    private fun normalizeBodyForLog(content: String): String {
        return content
            .lineSequence().joinToString(" ") { it.trim() }
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun resolveCharset(response: HttpServletResponse): Charset {
        val encoding = response.characterEncoding ?: return StandardCharsets.UTF_8
        return runCatching { Charset.forName(encoding) }.getOrDefault(StandardCharsets.UTF_8)
    }
}


