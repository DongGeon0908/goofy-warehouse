# multiGet()
> 레디스에서 특정 데이터를 조회하는데 자꾸 이상한 값이 나옴 그래서 찾아보니..

Redis의 opsForValue().multiGet() 메서드는 여러 개의 키에 해당하는 값을 한 번에 가져오는 기능을 제공합니다. 이 메서드는 주어진 키들에 대한 값을 리스트로 반환합니다.

아래는 Kotlin에서 opsForValue().multiGet() 메서드를 사용하여 여러 개의 키에 해당하는 값을 가져오는 예시입니다:

```kotlin
import org.springframework.data.redis.core.RedisTemplate

fun main() {
    val redisTemplate = RedisTemplate<String, String>()
    redisTemplate.afterPropertiesSet()

    val keys = listOf("key1", "key2", "key3")

    val values = redisTemplate.opsForValue().multiGet(keys)
    values.forEach { value ->
        println(value)
    }
}
```

위의 예시에서는 RedisTemplate<String, String>을 생성하고, opsForValue().multiGet() 메서드를 사용하여 여러 개의 키에 해당하는 값을 Redis에서 가져옵니다. keys 변수는 가져올 값들의 키들을 리스트로 정의하고, multiGet() 메서드를 호출하여 Redis로부터 해당 키들에 대한 값을 가져옵니다.

multiGet() 메서드의 반환 값은 List<V?> 타입입니다. 가져온 값이 존재하는 경우 해당 값이 리스트에 저장되고, 존재하지 않는 경우 null이 리스트에 저장됩니다. 위의 예시에서는 간단히 값을 출력하기 위해 values 리스트를 순회하면서 각 값을 출력합니다.

주의할 점은 multiGet() 메서드는 입력으로 주어진 키들의 순서대로 값을 반환합니다. 따라서 반환된 값들은 입력한 키들의 순서와 동일한 순서로 리스트에 저장됩니다.

### 결론
찾는 데이터가 없으면 null을 반환
