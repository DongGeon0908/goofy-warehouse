import mu.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping(value = ["/api/v1/test"], produces = [MediaType.APPLICATION_JSON_VALUE])
class FilterExample {
    @GetMapping("/filter")
    fun testFilterExtension(
        @RequestParam message: String = "Hello Filter Example"
    ) = ResponseEntity.ok(message)
}

@Component
class SystemLogFilter(
    private val systemLogPublisher: SystemLogPublisher
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        filterChain.doFilter(request, response)

        systemLogPublisher.publishEvent(SystemLogEvent.of(request))
    }
}

@Component
class SystemLogPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    fun publishEvent(event: SystemLogEvent) {
        applicationEventPublisher.publishEvent(event)
    }
}

@Component
class SystemLogSubscriber {
    private val logger = KotlinLogging.logger {}

    @Async(value = "systemLogExecutors")
    @EventListener(SystemLogEvent::class)
    fun subscribe(event: SystemLogEvent) {
        run {
            /** your jobs */
            logger.info { event.request.toString() }
        }
    }
}

@EnableAsync
@Configuration
class SystemLogExecutorsConfiguration {
    @Bean(name = ["systemLogExecutors"])
    fun taskExecutor() = ThreadPoolTaskExecutor()
        .apply {
            this.corePoolSize = 10
            this.queueCapacity = 50
            this.maxPoolSize = 30
        }
}

data class SystemLogEvent(
    val request: SystemRequestLog
) {
    companion object {
        fun of(request: HttpServletRequest): SystemLogEvent {
            val requestWrapper = ContentCachingRequestWrapper(request)

            val requestLog = SystemRequestLog.from(requestWrapper)

            return SystemLogEvent(requestLog)
        }
    }
}

data class SystemRequestLog(
    val path: String,
    val method: String,
    val headers: Map<String, Any>,
    val body: String?
) {
    companion object {
        fun from(request: ContentCachingRequestWrapper): SystemRequestLog {
            return SystemRequestLog(
                path = request.requestURI,
                method = request.method,
                headers = request.getHeadersInfo(),
                body = String(request.contentAsByteArray)
            )
        }
    }
}

fun HttpServletRequest.getHeadersInfo(): Map<String, Any> {
    return this.headerNames
        .asSequence()
        .associateWith { this.getHeader(it) }
}
