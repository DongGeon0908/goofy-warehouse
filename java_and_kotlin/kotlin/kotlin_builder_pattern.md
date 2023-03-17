# Kotlin Builder Pattern Example
> 특정 상황에서는 Builder Pattern이 더 유용

```kotlin
import java.time.LocalDateTime

/** 단순 객체 데이터를 Builder Pattern으로 구성 */
class BuilderPattern private constructor(
    val name: String?,
    val options: List<String>?,
    val date: LocalDateTime?
) {
    data class Builder(
        var name: String? = null,
        var options: List<String>? = null,
        var date: LocalDateTime? = null
    ) {
        fun name(name: String) = apply { this.name = name }
        fun options(options: List<String>) = apply { this.options = options }
        fun date(date: LocalDateTime) = apply { this.date = date }
        fun build() = BuilderPattern(name, options, date)
    }
}

fun main() {
    val test = BuilderPattern.Builder()
        .name("hello")
        .options(listOf("test1", "test2"))
        .date(LocalDateTime.now())
        .build()

    println(test)
}
```
