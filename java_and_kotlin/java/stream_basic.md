# Java Stream 간단 정리

- Java Stream은 자바 8에서 도입된 새로운 기능으로, 컬렉션(Collection)이나 배열(Array) 등의 데이터를 처리하는데 사용 
- Stream은 데이터를 람다 표현식으로 처리하며, 병렬처리가 가능

### Stream은 크게 생성, 중간 연산, 최종 연산의 세 가지 파트로 구성

- 생성(Creation): Stream은 데이터를 읽어들이는 소스에서 생성, 예를 들어, List, Set, Array, 파일 등의 소스에서 데이터를 읽어들여 Stream을 생성
- 중간 연산(Intermediate operations): Stream에서 데이터를 필터링, 정렬, 변환 등의 중간 처리를 수행, 중간 연산은 lazy evaluation이 가능하므로, 필요한 데이터만 연산하여 처리
- 최종 연산(Terminal operations): Stream에서 중간 연산을 거친 데이터를 처리하여 결과를 도출, 최종 연산은 데이터 소스를 소비하므로, 한 번만 실행

> Stream을 사용하면 복잡한 데이터 처리 로직을 간결하고 명확하게 작성할 수 있으며, 병렬 처리가 가능하여 대용량 데이터의 처리 성능을 높일 수 있습니다.

### 생성(Creation)

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
Stream<Integer> stream = numbers.stream();

Integer[] numbersArray = {1, 2, 3, 4, 5};
Stream<Integer> stream = Arrays.stream(numbersArray);
```

### 중간 연산(Intermediate operations)

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
Stream<Integer> stream = numbers.stream();
stream.filter(n -> n % 2 == 0)
      .forEach(System.out::println);
```

### 최종 연산(Terminal operations)

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
int sum = numbers.stream()
                .filter(n -> n % 2 == 0)
                .mapToInt(Integer::intValue) 
                .sum(); 
System.out.println(sum); 
```

### Stream을 써야 하는 이유

- 간결한 코드 작성

```
Stream을 사용하면 데이터 처리 코드를 간결하게 작성할 수 있습니다. Stream은 filter(), map(), reduce() 등의 메소드를 사용하여 코드를 구성하므로, 복잡한 처리 로직을 람다 표현식으로 간결하게 표현할 수 있습니다.
```

- 병렬 처리 가능

```
Stream은 내부적으로 Spliterator를 사용하여 데이터를 병렬 처리할 수 있습니다. Spliterator는 데이터 소스를 분할하여 병렬 처리할 수 있도록 지원하므로, 대용량 데이터 처리에 효과적입니다.
```

- 지연 평가(lazy evaluation)

```
Stream은 중간 연산에서 지연 평가를 수행하여, 필요한 데이터만 연산합니다. 필요한 시점에서만 데이터를 계산하므로, 불필요한 계산을 최소화하여 성능을 향상시킬 수 있습니다.
```

- 병렬화가 쉬운 API

```
Stream은 parallel() 메소드를 호출하여 병렬 처리를 수행할 수 있습니다. 이를 통해 손쉽게 병렬화된 코드를 작성할 수 있으며, parallel() 메소드를 호출하지 않으면 순차 처리가 수행됩니다.
```

- 유연한 스트림 처리

```
Stream은 다양한 데이터 소스에서 생성할 수 있으며, 중간 연산과 최종 연산의 조합에 따라 다양한 데이터 처리가 가능합니다. 또한, Stream API를 활용하여 커스텀 연산자를 구현하면 더욱 유연하게 데이터 처리를 수행할 수 있습니다.
```
