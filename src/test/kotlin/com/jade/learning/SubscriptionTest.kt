package com.jade.learning

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux
import java.time.Duration

class SubscriptionTest {
    @Test
    fun dataStream() {
        val elements = mutableListOf<Int>()

        Flux.just(1, 2, 3, 4)
            .delayElements(Duration.ofMillis(1000))
            .log()
            .subscribe { elements.add(it) }

        Thread.sleep(5000)

        assertThat(elements).containsExactly(1, 2, 3, 4)
    }

    @Test
    fun dataStreamByDirect() {
        val elements = mutableListOf<Int>()

        Flux.just(1, 2, 3, 4)
            .log()
            .subscribe(object : Subscriber<Int> {
                override fun onSubscribe(s: Subscription) {
                    s.request(Long.MAX_VALUE)
                }

                override fun onNext(t: Int) {
                    elements.add(t)
                }

                override fun onError(t: Throwable) {}

                override fun onComplete() {}

            })

        assertThat(elements).containsExactly(1, 2, 3, 4)
    }
    @Test
    fun backpressure() {
        val elements = mutableListOf<Int>()

        (1..10).toFlux()
            .log()
            .subscribe(object : Subscriber<Int> {
                lateinit var s: Subscription
                var onNextAmount = 0

                override fun onSubscribe(s: Subscription) {
                    this.s = s
                    s.request(2)
                }

                override fun onNext(t: Int) {
                    elements.add(t)
                    onNextAmount++
                    if (onNextAmount % 2 == 0) {
                        s.request(2)
                    }
                }

                override fun onError(t: Throwable) {}

                override fun onComplete() {}

            })

        assertThat(elements).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    }
}
