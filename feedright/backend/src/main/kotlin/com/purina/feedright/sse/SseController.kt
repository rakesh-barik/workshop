package com.purina.feedright.sse

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/api/visits")
class SseController(private val registry: SseEmitterRegistry) {

    @GetMapping("/stream", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun stream(): SseEmitter {
        val emitter = SseEmitter(5 * 60 * 1000L) // 5-minute timeout; browser auto-reconnects
        registry.add(emitter)
        emitter.send(
            SseEmitter.event()
                .name("connected")
                .data(mapOf("type" to "connected"))
        )
        return emitter
    }
}
