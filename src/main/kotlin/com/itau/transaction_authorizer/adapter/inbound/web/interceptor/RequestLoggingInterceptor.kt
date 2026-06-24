package com.itau.transaction_authorizer.adapter.inbound.web.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class RequestLoggingInterceptor : HandlerInterceptor {

    private val log = LoggerFactory.getLogger(RequestLoggingInterceptor::class.java)

    companion object {
        private const val START_TIME_ATTR = "request_start_time"
    }

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        request.setAttribute(START_TIME_ATTR, System.currentTimeMillis())

        log.info(
            "request_in method={} path={} traceId={}",
            request.method,
            request.requestURI,
            MDC.get("traceId")
        )
        return true
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        val startTime = request.getAttribute(START_TIME_ATTR) as? Long ?: System.currentTimeMillis()
        val durationMs = System.currentTimeMillis() - startTime

        log.info(
            "request_out method={} path={} status={} duration_ms={} traceId={}",
            request.method,
            request.requestURI,
            response.status,
            durationMs,
            MDC.get("traceId")
        )
    }
}

