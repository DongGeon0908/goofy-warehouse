# Spring Local Cache

> Local Cache를 통해 Network IO 비용을 줄이자

### 로컬 캐시 description

- 로컬 캐시는 스프링 init되고 나서, 서버의 로컬 캐싱 저장소에 데이터를 저장하는 방법을 의미
- 변경이 없고, 빠르게 데이터를 서빙해야 하는 상황에서 해당 로컬 캐시 사용
- 네트워크 부하가 많이 발생하는 상황이고, 조회되는 데이터가 변경되지 않는 다면 로컬 캐시 적용관련하여 고려 굳

### 단점

- 데이터 정합성이 중요한 경우, 로컬 캐시는 부적절함
- 팟이 여러개 띄워진 상황에서 로컬캐시에 대한 cache evict을 어떻게 수행하지? -> 별도의 중간 관리체계가 필요함

###    

### 사용법

**Dependency 설정**

```kotlin
object DependencyVersion {
    const val EHCACHE_VERSION = "3.8.1"
    const val JAVA_CACHE_API_VERSION = "1.1.1"
}

implementation("org.springframework.boot:spring-boot-starter-cache")
implementation("org.ehcache:ehcache:${DependencyVersion.EHCACHE_VERSION}")
implementation("javax.cache:cache-api:${DependencyVersion.JAVA_CACHE_API_VERSION}")
```

**Ehcache.xml 설정**

Local cache를 등록할 데이터에 대한 설정을 진행한다. 어떤 타입인지, expired 시간을 어떻게 설정한 것인지 등에 대한 정보를 기입한다.

```xml

<config
        xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'
        xmlns='http://www.ehcache.org/v3'
        xmlns:jsr107="http://www.ehcache.org/v3/jsr107"
        xsi:schemaLocation="http://www.ehcache.org/v3 http://www.ehcache.org/schema/ehcache-core.xsd
http://www.ehcache.org/v3/jsr107 http://www.ehcache.org/schema/ehcache-107-ext-3.0.xsd">
    <service>
        <jsr107:defaults enable-management="true" enable-statistics="true"/>
    </service>

    <!--여기부터 -->
    <cache-template name="myDefaultTemplate">
        <expiry>
            <ttl unit="minutes">60</ttl>
        </expiry>
        <listeners>
            <listener>
                <class>com.goofy.localcache.config.EhCacheEventLogger</class>
                <event-firing-mode>ASYNCHRONOUS</event-firing-mode>
                <event-ordering-mode>UNORDERED</event-ordering-mode>
                <events-to-fire-on>CREATED</events-to-fire-on>
                <events-to-fire-on>EXPIRED</events-to-fire-on>
            </listener>
        </listeners>
        <heap>2</heap>
    </cache-template>

    <!--test-->
    <cache alias="Test.getTest" uses-template="myDefaultTemplate">
        <key-type>java.lang.Long</key-type>
        <value-type>java.lang.String</value-type>
        <resources>
            <heap>2</heap>
        </resources>
    </cache>
    <!--여기까지 사용자 환경설정 -->

</config>
```

**cache config**

JCache에 대한 Config 설정을 진행한다. 기반은 ehcache.xml을 잡는다.

```kotlin
@Configuration
@EnableCaching
class EhCacheConfiguration {
    companion object {
        private const val EHCACHE_XML_PATH = "ehcache.xml"
    }

    @Bean
    fun cacheManagerFactoryBean(): JCacheManagerFactoryBean {
        return JCacheManagerFactoryBean().apply {
            val path = ClassPathResource(EHCACHE_XML_PATH).uri
            this.setCacheManagerUri(path)
        }
    }

    @Bean
    fun testEhCacheManager(): CacheManager {
        return JCacheCacheManager().apply {
            val cacheManager = cacheManagerFactoryBean().getObject()
            this.cacheManager = cacheManager
        }
    }
}
```

**ex cache service**

기존의 Spring Cache의 Cacheable과 사용법은 같다

```kotlin
@Service
class TestCacheService(
    private val testRepository: TestRepository
) {
    private val logger = KotlinLogging.logger {}

    @Cacheable(
        cacheManager = "testEhCacheManager",
        cacheNames = ["Test.getTest"],
        key = "#id"
    )
    fun getTest(id: Long): String? {
        return testRepository.findById(id)
    }
}
```

로컬 캐시를 등록하면, 현재 캐시가 없는 경우에는 캐시가 등록되고, 그 이후부터는 캐시 정보를 읽어서 진행한다.

<br>

**cache관련 작업이 수행된 경우 event를 발행하여 logging을 찍음**
> 기존의 xml에 event listener 추기

```xml
<listeners>
    <listener>
        <class>com.goofy.localcache.config.EhCacheEventLogger</class>
        <event-firing-mode>ASYNCHRONOUS</event-firing-mode>
        <event-ordering-mode>UNORDERED</event-ordering-mode>
        <events-to-fire-on>CREATED</events-to-fire-on>
        <events-to-fire-on>EXPIRED</events-to-fire-on>
    </listener>
</listeners>
```

위의 내용을 xml에 추가한 이후, eventListener를 추가로 생성해야 함

```kotlin
class EhCacheEventLogger : CacheEventListener<String, Any> {
    private val logger = KotlinLogging.logger {}

    override fun onEvent(cacheEvent: CacheEvent<out String, out Any>) {
        logger.info { "cache event logger key : ${cacheEvent.key} / oldValue : ${cacheEvent.oldValue} / newValue : ${cacheEvent.newValue}" }
    }
}
```

### 관련자료

- [REPO](https://github.com/DongGeon0908/local-cache)
- [EHCACHE DOCS](https://www.ehcache.org/documentation/3.10/getting-started.html)
- [JCacheManager](https://www.javadoc.io/doc/org.redisson/redisson/3.2.0/org/redisson/jcache/JCacheManager.html)
