# SseEmitter

> client-server 연결이 진행되고 나서, 지정된 시간만큼 서버에서 발생한 이벤트를 전달 받을 수 있는 기능을 제공한다.

### SseEmitter에 대한 간단 설명

SseEmitter는 Spring 프레임워크에서 제공하는 비동기 서버-클라이언트 통신 기술 중 하나로, Server-Sent Events (SSE)를 사용하여 클라이언트와 통신하는 방법

SSE는 단방향 통신 프로토콜로, 서버에서 클라이언트로 지속적으로 데이터를 전송하는 기술, 이를 이용하여 클라이언트에서 서버로의 요청 없이도 서버에서 데이터를 전송 가능 (실시간 업데이트나 이벤트 알림 등에 사용)

Spring의 SseEmitter는 SSE 프로토콜을 사용하여 서버에서 클라이언트로 데이터를 전송하는 역할을 담당, 클라이언트는 SSE 프로토콜을 지원하는 브라우저를 사용하여 SSE 요청을 보낼 수 있음

### 동작 과정

- SseEmitter는 일종의 Response Body로 사용
- 클라이언트로의 응답이 SseEmitter를 생성하고 반환
- 서버에서는 SseEmitter 객체를 생성한 후, 데이터를 전송하는데 사용
- SseEmitter는 Servlet 3.0의 비동기 기능을 사용하여 동작

### life-cycle

SseEmitter는 클라이언트와의 연결이 끊기기 전까지 계속해서 데이터를 전송 이 때, 클라이언트와의 연결이 끊기면 complete() 메소드를 호출하여 SseEmitter를 종료

### SSE PROTOCOL

> SSE(Server-Sent Events) 프로토콜은 서버와 클라이언트 간의 단방향 통신을 위한 웹 표준 기술,
> HTTP 프로토콜을 기반으로 하며, 서버에서 클라이언트로 지속적으로 데이터를 전송하는 방식으로 동작

SSE는 웹소켓(WebSocket)과 유사하지만, 웹소켓은 양방향 통신을 지원하는 반면 SSE는 단방향 통신만 가능 웹소켓과 달리 SSE는 HTTP 커넥션을 재사용하여 성능을 최적화하며, 브라우저 호환성이 높음

### SSE의 특징

- 지속적인 연결 (Persistent Connection) : 클라이언트가 서버와 연결된 상태를 유지하면서 지속적으로 데이터를 수신할 수 있고, 이를 통해 실시간 업데이트나 이벤트 알림 등에 사용
- 이벤트 기반 프로토콜 : 데이터를 이벤트(Event) 단위로 전송합니다. 이벤트는 다음과 같은 형식으로 구성
- makefile Copy code event: 이벤트 이름\n data: 이벤트 데이터\n\n 이벤트 이름은 생략 가능하며, 이벤트 데이터는 JSON 형태로 전송될 수 있음
- HTTP 프로토콜과 호환성 : HTTP 프로토콜을 기반으로 하기 때문에, 기존의 웹 애플리케이션에서 쉽게 적용 가능. SSE는 주로 실시간 데이터 업데이트, 알림 기능, 뉴스 피드 등에 사용, 서버는 SSE
  프로토콜을 지원하는 라이브러리를 사용하여 SSE 요청에 응답하고, 클라이언트는 SSE API를 사용하여 SSE 요청을 보낼 수 있음

### SSE SPRING CODE

```java
/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * A specialization of {@link ResponseBodyEmitter} for sending
 * <a href="https://www.w3.org/TR/eventsource/">Server-Sent Events</a>.
 *
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 4.2
 */
public class SseEmitter extends ResponseBodyEmitter {

    private static final MediaType TEXT_PLAIN = new MediaType("text", "plain", StandardCharsets.UTF_8);

    /**
     * Create a new SseEmitter instance.
     */
    public SseEmitter() {
        super();
    }

    /**
     * Create a SseEmitter with a custom timeout value.
     * <p>By default not set in which case the default configured in the MVC
     * Java Config or the MVC namespace is used, or if that's not set, then the
     * timeout depends on the default of the underlying server.
     * @param timeout the timeout value in milliseconds
     * @since 4.2.2
     */
    public SseEmitter(Long timeout) {
        super(timeout);
    }


    @Override
    protected void extendResponse(ServerHttpResponse outputMessage) {
        super.extendResponse(outputMessage);

        HttpHeaders headers = outputMessage.getHeaders();
        if (headers.getContentType() == null) {
            headers.setContentType(MediaType.TEXT_EVENT_STREAM);
        }
    }

    /**
     * Send the object formatted as a single SSE "data" line. It's equivalent to:
     * <pre>
     * // static import of SseEmitter.*
     *
     * SseEmitter emitter = new SseEmitter();
     * emitter.send(event().data(myObject));
     * </pre>
     * <p>Please, see {@link ResponseBodyEmitter#send(Object) parent Javadoc}
     * for important notes on exception handling.
     * @param object the object to write
     * @throws IOException raised when an I/O error occurs
     * @throws java.lang.IllegalStateException wraps any other errors
     */
    @Override
    public void send(Object object) throws IOException {
        send(object, null);
    }

    /**
     * Send the object formatted as a single SSE "data" line. It's equivalent to:
     * <pre>
     * // static import of SseEmitter.*
     *
     * SseEmitter emitter = new SseEmitter();
     * emitter.send(event().data(myObject, MediaType.APPLICATION_JSON));
     * </pre>
     * <p>Please, see {@link ResponseBodyEmitter#send(Object) parent Javadoc}
     * for important notes on exception handling.
     * @param object the object to write
     * @param mediaType a MediaType hint for selecting an HttpMessageConverter
     * @throws IOException raised when an I/O error occurs
     */
    @Override
    public void send(Object object, @Nullable MediaType mediaType) throws IOException {
        send(event().data(object, mediaType));
    }

    /**
     * Send an SSE event prepared with the given builder. For example:
     * <pre>
     * // static import of SseEmitter
     * SseEmitter emitter = new SseEmitter();
     * emitter.send(event().name("update").id("1").data(myObject));
     * </pre>
     * @param builder a builder for an SSE formatted event.
     * @throws IOException raised when an I/O error occurs
     */
    public void send(SseEventBuilder builder) throws IOException {
        Set<DataWithMediaType> dataToSend = builder.build();
        synchronized (this) {
            for (DataWithMediaType entry : dataToSend) {
                super.send(entry.getData(), entry.getMediaType());
            }
        }
    }

    @Override
    public String toString() {
        return "SseEmitter@" + ObjectUtils.getIdentityHexString(this);
    }


    public static SseEventBuilder event() {
        return new SseEventBuilderImpl();
    }


    /**
     * A builder for an SSE event.
     */
    public interface SseEventBuilder {

        /**
         * Add an SSE "id" line.
         */
        SseEventBuilder id(String id);

        /**
         * Add an SSE "event" line.
         */
        SseEventBuilder name(String eventName);

        /**
         * Add an SSE "retry" line.
         */
        SseEventBuilder reconnectTime(long reconnectTimeMillis);

        /**
         * Add an SSE "comment" line.
         */
        SseEventBuilder comment(String comment);

        /**
         * Add an SSE "data" line.
         */
        SseEventBuilder data(Object object);

        /**
         * Add an SSE "data" line.
         */
        SseEventBuilder data(Object object, @Nullable MediaType mediaType);

        /**
         * Return one or more Object-MediaType pairs to write via
         * {@link #send(Object, MediaType)}.
         * @since 4.2.3
         */
        Set<DataWithMediaType> build();
    }


    /**
     * Default implementation of SseEventBuilder.
     */
    private static class SseEventBuilderImpl implements SseEventBuilder {

        private final Set<DataWithMediaType> dataToSend = new LinkedHashSet<>(4);

        @Nullable
        private StringBuilder sb;

        @Override
        public SseEventBuilder id(String id) {
            append("id:").append(id).append('\n');
            return this;
        }

        @Override
        public SseEventBuilder name(String name) {
            append("event:").append(name).append('\n');
            return this;
        }

        @Override
        public SseEventBuilder reconnectTime(long reconnectTimeMillis) {
            append("retry:").append(String.valueOf(reconnectTimeMillis)).append('\n');
            return this;
        }

        @Override
        public SseEventBuilder comment(String comment) {
            append(':').append(comment).append('\n');
            return this;
        }

        @Override
        public SseEventBuilder data(Object object) {
            return data(object, null);
        }

        @Override
        public SseEventBuilder data(Object object, @Nullable MediaType mediaType) {
            append("data:");
            saveAppendedText();
            this.dataToSend.add(new DataWithMediaType(object, mediaType));
            append('\n');
            return this;
        }

        SseEventBuilderImpl append(String text) {
            if (this.sb == null) {
                this.sb = new StringBuilder();
            }
            this.sb.append(text);
            return this;
        }

        SseEventBuilderImpl append(char ch) {
            if (this.sb == null) {
                this.sb = new StringBuilder();
            }
            this.sb.append(ch);
            return this;
        }

        @Override
        public Set<DataWithMediaType> build() {
            if (!StringUtils.hasLength(this.sb) && this.dataToSend.isEmpty()) {
                return Collections.emptySet();
            }
            append('\n');
            saveAppendedText();
            return this.dataToSend;
        }

        private void saveAppendedText() {
            if (this.sb != null) {
                this.dataToSend.add(new DataWithMediaType(this.sb.toString(), TEXT_PLAIN));
                this.sb = null;
            }
        }
    }

}
```

### with Kotlin Utils Code

**Noti Controller**

```kotlin
package com.goofy.sse.controller

import com.goofy.sse.service.NotificationService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class NotificationController(
    private val notificationService: NotificationService
) {
    @GetMapping(
        path = ["/api/v1/notifications"],
        produces = [MediaType.TEXT_EVENT_STREAM_VALUE]
    )
    fun notifyV1() = notificationService.notifyV1()
}
```

**Noti Service**

```kotlin
package com.goofy.sse.service

import com.goofy.sse.event.SseEmitterEvent
import com.goofy.sse.event.SseEmitterEvent.Companion.send
import com.goofy.sse.model.NotificationEventModel
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.net.URLEncoder
import java.time.ZonedDateTime
import java.util.concurrent.Executors

@Service
class NotificationService {
    private val logger = KotlinLogging.logger {}

    fun notifyV1(): SseEmitter {
        val emitter = SseEmitterEvent.generate(180000)

        // 가능하면, 코루틴으로
        val sseMvcExecutor = Executors.newSingleThreadExecutor()

        sseMvcExecutor.execute {
            var i = 0
            while (i != -1) {
                i++
                runCatching {
                    val data = NotificationEventModel(
                        id = i.toLong(),
                        message = "hello world",
                        createdAt = ZonedDateTime.now()
                    )

                    emitter.send(
                        id = i.toString(),
                        name = "sse event with SSE EMITTER",
                        reconnectTime = 10000,
                        comment = URLEncoder.encode("SSE를 활용한 이벤트 루프"),
                        data = data
                    )
                }.onFailure {
                    logger.error { "error / ${it.message}" }
                    emitter.completeWithError(it)

                    // break
                    i = -1
                }
            }
        }

        sseMvcExecutor.shutdown()

        return emitter
    }
}
```

**SseEmitterEvent**

```kotlin
package com.goofy.sse.event

import org.springframework.http.MediaType
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

class SseEmitterEvent {
    companion object {
        fun generate(timeout: Long? = null): SseEmitter {
            return when (timeout) {
                null -> SseEmitter()
                else -> SseEmitter(timeout)
            }
        }

        fun SseEmitter.send(
            id: String? = null,
            name: String? = null,
            reconnectTime: Long? = null,
            comment: String? = null,
            data: Any? = null,
            mediaType: MediaType = MediaType.APPLICATION_JSON
        ) {
            val event = SseEmitter.event()
                .apply {
                    id?.let { this.id(id) }
                    name?.let { this.name(name) }
                    reconnectTime?.let { this.reconnectTime(reconnectTime) }
                    comment?.let { this.comment(comment) }
                    data?.let { this.data(data, mediaType) }
                }

            this.send(event)
        }
    }
}
```

### Example Response

```text
id:1
event:sse event with SSE EMITTER
retry:10000
:SSE%EB%A5%BC+%ED%99%9C%EC%9A%A9%ED%95%9C+%EC%9D%B4%EB%B2%A4%ED%8A%B8+%EB%A3%A8%ED%94%84
data:{"id":1,"message":"hello world","createdAt":"2023-04-16T23:46:15.982"}

id:2
event:sse event with SSE EMITTER
retry:10000
:SSE%EB%A5%BC+%ED%99%9C%EC%9A%A9%ED%95%9C+%EC%9D%B4%EB%B2%A4%ED%8A%B8+%EB%A3%A8%ED%94%84
data:{"id":2,"message":"hello world","createdAt":"2023-04-16T23:46:15.984"}

id:3
event:sse event with SSE EMITTER
retry:10000
:SSE%EB%A5%BC+%ED%99%9C%EC%9A%A9%ED%95%9C+%EC%9D%B4%EB%B2%A4%ED%8A%B8+%EB%A3%A8%ED%94%84
data:{"id":3,"message":"hello world","createdAt":"2023-04-16T23:46:15.989"}

```

# Reference

- https://www.baeldung.com/spring-server-sent-events
- https://github.com/DongGeon0908/spring-sse
