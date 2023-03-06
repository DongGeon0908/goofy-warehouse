### Kotlin Sequence

> 방대한 데이터를 메모리에 직접 올려서 사용하는 것이 아니라, 필요한 상황에 데이터를 이용하는 것이 Sequence

Kotlin에서 Sequence는 연속적인 데이터를 표현하는 인터페이스입니다. Sequence는 컬렉션과 비슷한 연속 데이터 구조를 나타내지만, 데이터를 연속적으로 생성하는 방식이 다릅니다.

Sequence는 지연 계산 방식을 사용하여 데이터를 생성합니다. 이는 Sequence의 데이터가 필요한 경우에만 생성되는 것을 의미합니다. 이러한 방식은 대량의 데이터를 다룰 때 효율적이며, 중간 처리 단계에서 불필요한 데이터 생성을 방지합니다.

또한, Sequence는 여러 단계의 중간 연산을 통해 데이터를 처리할 수 있습니다. 이러한 중간 연산은 새로운 Sequence를 반환하며, 이러한 중간 연산을 사용하여 복잡한 데이터 처리를 쉽게 구현할 수 있습니다.

Kotlin의 Sequence 인터페이스는 `sequenceOf()`와 같은 팩토리 함수를 사용하여 생성할 수 있습니다. 또한, 컬렉션을 Sequence로 변환하기 위해서는 `asSequence()` 함수를 사용할 수 있습니다.



### 동작과정

1. Sequence 생성
   - `sequenceOf()` 함수를 사용하여 Sequence를 생성하거나, 기존 컬렉션을 `asSequence()` 함수를 사용하여 Sequence로 변환합니다.
2. 중간 연산
   - Sequence에 대해 여러 중간 연산을 수행할 수 있습니다.
   - 중간 연산은 Sequence를 반환합니다.
   - 중간 연산은 필요에 따라 실행됩니다. 즉, 다음 연산이나 최종 처리 단계에서 데이터가 필요한 경우에만 실행됩니다.
   - 여러 중간 연산을 연결하여 복잡한 처리를 구현할 수 있습니다.
3. 최종 처리
   - 중간 연산을 수행한 Sequence를 최종 처리합니다.
   - 최종 처리는 데이터를 생성하며, 여러 가지 형태로 반환될 수 있습니다.
   - 최종 처리는 필요한 데이터만 생성되므로, 성능상 이점이 있습니다.

Sequence를 사용하면 중간 처리 과정에서 불필요한 데이터 생성을 방지하여 성능을 향상시킬 수 있으며, 여러 중간 연산을 연결하여 복잡한 처리를 간단하게 구현할 수 있습니다.



### 예제

```kotlin
/** 대용량 리스트 생성 */
val largeList = List(1000000) { it }

/** 컬렉션을 Sequence로 변환 */
val sequence = largeList.asSequence()

/** 중간 연산 */
val result = sequence
    .filter { it % 2 == 0 } // 짝수만 필터링
    .map { it * 2 } // 각 요소를 2배로 변환
    .take(10) // 처음 10개 요소만 선택

/** 최종 처리 */
println(result.toList()) // [0, 4, 8, 12, 16, 20, 24, 28, 32, 36]
```



### Lazy Loading

Sequence는 lazy loading 방식을 사용합니다. 즉, Sequence에 대한 연산은 요청이 있을 때까지 지연됩니다. 이러한 지연 로딩은 효율적인 데이터 처리를 가능하게 합니다.

예를 들어, 다음과 같이 리스트를 생성하고 Sequence로 변환한 후 중간 연산을 수행하는 경우를 생각해보겠습니다.

```kotlin
val list = listOf(1, 2, 3, 4, 5)
val sequence = list.asSequence()
val result = sequence.filter { it % 2 == 0 }.map { it * 2 }
```

위 코드에서는 중간 연산인 `filter`와 `map` 함수를 호출하였지만, 실제로 데이터가 변경되는 것은 아닙니다. Sequence에 대한 최종 처리가 실행되기 전까지는 중간 연산이 적용되지 않습니다.

따라서 최종 처리인 `toList()`나 `forEach()` 등이 호출될 때 비로소 중간 연산이 적용되며, 데이터가 생성됩니다.



### Lazy Loading Example 2

lazy loading은 필요한 시점에 데이터를 로딩하는 방식입니다. 이를 통해 성능 향상과 메모리 절약을 할 수 있습니다.

데이터를 로딩할 때는 크게 두 가지 방식이 있습니다.

첫 번째 방식은 eager loading입니다. eager loading은 데이터를 사용하기 전에 모두 로딩하는 방식입니다. 예를 들어, 리스트를 생성하고 모든 요소를 출력하는 경우, 리스트의 모든 요소가 메모리에 로딩된 후에 출력됩니다.

```kotlin
val list = listOf(1, 2, 3, 4, 5)
list.forEach { println(it) }
```

두 번째 방식은 lazy loading입니다. lazy loading은 필요한 데이터만 로딩하는 방식입니다. 이 방식을 사용하면 메모리 사용량이 줄어들고 성능 향상을 기대할 수 있습니다.

```kotlin
val sequence = listOf(1, 2, 3, 4, 5).asSequence()
sequence.filter { it % 2 == 0 }.forEach { println(it) }
```

위 코드에서는 `asSequence()` 함수를 사용하여 리스트를 Sequence로 변환하고, `filter()` 함수를 사용하여 짝수만 선택한 후 `forEach()` 함수를 사용하여 출력합니다. 이때 짝수 요소만 필요하기 때문에 필요한 데이터만 로딩됩니다.

lazy loading은 필요한 데이터만 로딩하기 때문에 성능상 이점이 있습니다. 또한, 대용량 데이터를 처리할 때 유용합니다. 대용량 데이터를 모두 로딩하지 않고 필요한 데이터만 로딩하기 때문입니다.
