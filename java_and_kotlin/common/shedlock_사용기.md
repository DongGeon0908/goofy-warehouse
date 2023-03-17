### ShedLock
> 분산시스템 환경에서 하나의 스케줄러만 동작할 수 있도록하는 스케줄러 락킹 라이브러리,
> 안전한 서비스 구조는 HA 이중화 구조를 가진다. 그렇다면... 스케줄링이 각각 존재한다면, 모든 서버에서 스케줄링이 돌면...
> 그럼 배치성 로직이 2번 도는 문제가 발생하지 않을까? 이걸 어떻게 제어할까?

### Dependency

```groovy
implementation("net.javacrumbs.shedlock:shedlock-spring:4.14.0")
implementation("net.javacrumbs.shedlock:shedlock-provider-jdbc-template:4.14.0")
```

shedlock을 사용하기 위해서는 build.gradle에 다음과 같은 라이브러리를 추가해야 함!



### Table

```sql
CREATE TABLE shedlock (
  name VARCHAR(64),
  lock_until TIMESTAMP(3) NULL,
  locked_at TIMESTAMP(3) NULL,
  locked_by VARCHAR(255),
  PRIMARY KEY (name)
)
```

shedlock library는 다음의 테이블을 기준으로 진행하기 때문에, 테이블 정보를 미리 생성해야 한다! (물론 Redis를 사용해도 괜찮을 것 같습니다!)



### ScheudlerConfig

```kotlin
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
@Configuration
class SchedulerConfiguration {
    @Bean
    fun lockProvider(dataSource: DataSource): LockProvider {
        return JdbcTemplateLockProvider(dataSource)
    }
```

shedlock이 가능하도록 Config 파일 생성 및 lockProvider Bean을 등록해줘야 합니다!

### Scheduler Lock Lock Lock!!

```kotlin
@Component
class TestScheduler {
    private val logger = KotlinLogging.logger { }

    @Scheduled(fixedDelay = 10000)
    @SchedulerLock(
        name = "runScheduledLock",
        lockAtLeastFor = "PT5S",
        lockAtMostFor = "PT5S"
    )
    fun runScheduledLock() {
        logger.info { "------------------------------" }
        logger.info { "안녕하세요 저는 8000 이에요" }
        logger.info { LocalDateTime.now().toString() }
        logger.info { "------------------------------" }
        Thread.sleep(5000)
    }
}
```

@SchedulerLock 어노테이션을 기반으로 특정 스케줄러에 대한 락을 걸 수 있다.

### 동작과정

분산환경에서 배치성 팟이 2~3개 이상 구축될 수 있다. 이런 상황에서 특정 팟에만 있는 스케줄러만 동작해야 하는데, 이를 관리하기 위해 Shedlock 라이브러리는 탁월한 선택일 수 있다.
해당 라이브러리가 스케줄러를 락을 거는 방법은 ThreadLocal에서 특정 스레드를 죽이는 방식을 택한다. 요거는 라이브러리 조금 보면 바로 확인 가능!

### Reference
- [baeldung](https://www.baeldung.com/shedlock-spring)
- [예제 코드](https://github.com/DongGeon0908/scheduler-lock)
