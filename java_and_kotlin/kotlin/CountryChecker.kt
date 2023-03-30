import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/api/country-checker/v1"])
class CountryCheckRestController(
    private val countryCheckService: CountryCheckService
) {
    private val logger = KotlinLogging.logger {}

    @GetMapping
    fun checkCountry(
        @RequestHeader headers: HttpHeaders
    ): ResponseEntity<CountryCheckResponse> {
        val country = countryCheckService.checkLanguage(headers)
        return ResponseEntity.ok(CountryCheckResponse.from(country))
    }
}

@Service
class CountryCheckService {
    companion object {
        val languageHeadersKey = listOf("accept-language", "Accept-Language")
    }

    fun checkLanguage(headers: HttpHeaders): CountryCode {
        val key = languageHeadersKey.firstOrNull { key -> headers[key] != null }
            ?: return CountryCode.KOREA

        return headers[key]!!.mapNotNull { value -> CountryCode.of(value) }
            .ifEmpty { return CountryCode.KOREA }.first()
    }
}

enum class CountryCode(val korName: String) {
    KOREA("한국") {
        override fun check(value: String): Boolean {
            listOf("ko", "kr").firstOrNull {
                value.contains(it)
            } ?: return false

            return true
        }
    },
    JAPAN("일본") {
        override fun check(value: String): Boolean {
            listOf("ja").firstOrNull {
                value.contains(it)
            } ?: return false

            return true
        }
    };

    abstract fun check(value: String): Boolean

    companion object {
        fun of(value: String): CountryCode? {
            return values().firstOrNull { code -> code.check(value) }
        }
    }
}

data class CountryCheckResponse(
    val countryCode: CountryCode,
    val korName: String
) {
    companion object {
        fun from(countryCode: CountryCode): CountryCheckResponse {
            return CountryCheckResponse(
                countryCode = countryCode,
                korName = countryCode.korName
            )
        }
    }
}
