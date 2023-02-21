# ThreadLocal이란?

> 다양한 스레드가 하나의 자원을 소유하여 사용하려고 할때 동시성 문제가 발생할 수 있다. 내가 원하던 값과 다른 정반대의 값이 반환될 수 있다. 그럴때 ThreadLocal을 통해 각 스레드별로 자원을 할당하게 하여 사용할 수 있다.

### ThreadLocal이란?

특정 스레드에서만 접근할 수 있는 데이터를 저장하고 관리하는 기능을 제공한다. 서로 다른 스레드가 서로 다른 자원을 사용하기 때문에 동시 접근되는 자원이 없어 safety하게 사용할 수 있다는 장점 있다. 패키지는 **java.lang.ThreadLocal**이다.

### 물론 문제점도 있다. 아래의 예시와 같이...

```
1. 먼저 응용 프로그램이 풀에서 스레드를 빌린다.
2. 그런 다음 일부 스레드 제한 값을 현재 스레드의 ThreadLocal에 저장한다.
3. 현재 실행이 완료되면 응용 프로그램은 빌린 스레드를 풀에 반환한다.
4. 잠시 후 애플리케이션은 다른 요청을 처리하기 위해 스레드를 빌렸는데 이전과 동일한 스레드를 빌리게 된다.
5. 응용 프로그램이 마지막으로 필요한 정리를 수행하지 않았기 때문에 새 요청에 대해 동일한 ThreadLocal 데이터 를 다시 사용할 수 있다.
```

위와 같은 문제가 발생한다면, 데이터 정합성이 깨질 위험이 존재하게 된다. 그렇기 때문에 ThreadLocal을 사용하였다면, 해당 자원을 다 사용하고 나서는 반환하는 로직이 꼭 필요하다.
위와 같은 이유가 아니더라도, ThreadLocal을 생성하고, 제거하지 하지 않는다면 메모리 누수 문제가 발생할 것이다.

### ThreadLocal을 쉽게 사용하기 위한 Utils

```kotlin
class ThreadLocalUtils {
    companion object {
        /** ThreadLocal에 데이터 삽입 */
        fun <T> add(data: T): ThreadLocal<T> {
            return ThreadLocal.withInitial { data }
        }

        /** ThreadLocal에 저장된 값이 Null이 될 경우 발생 */
        fun <T> get(data: ThreadLocal<T>): T? {
            return data.get()
        }

        /** ThreadLocal의 데이터를 삭제 */
        fun <T> delete(data: ThreadLocal<T>) {
            data.remove()
        }
    }
}
```

### Reference

- [REPO](https://github.com/DongGeon0908/thread)
- [baeldung](https://www.baeldung.com/java-threadlocal)
- [JAVA DOCS](https://docs.oracle.com/javase/8/docs/api/java/lang/ThreadLocal.html)
