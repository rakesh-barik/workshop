package com.purina.feedright.sse

import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.CopyOnWriteArrayList

@Component
class SseEmitterRegistry {

    private val emitters = CopyOnWriteArrayList<SseEmitter>()

    fun add(emitter: SseEmitter) {
        emitters.add(emitter)
        emitter.onCompletion { emitters.remove(emitter) }
        emitter.onTimeout { emitters.remove(emitter) }
        emitter.onError { emitters.remove(emitter) }
    }

    fun broadcast(eventName: String, data: Any) {
        val dead = mutableListOf<SseEmitter>()
        emitters.forEach { emitter ->
            try {
                emitter.send(
                    SseEmitter.event()
                        .name(eventName)
                        .data(data)
                )
            } catch (e: Exception) {
                dead.add(emitter)
            }
        }
        emitters.removeAll(dead)
    }
}
