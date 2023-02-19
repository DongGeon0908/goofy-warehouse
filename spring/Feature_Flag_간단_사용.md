# Feature Flag 간단 사용

> 특정 기능에 대하여 A/B 테스트, 혹은 운영 테스트 등을 진행할 때, Feature Flag라는 기능을 통하여 만들어진 기능에 대한 ON/OFF 기능을 사용할 수 있다. Flag를 통해 활성화/비활성화를 배포 이후에도 진행할 수 있다.

### Azure Feature Flag

Feature Flag 기능을 지원하는 라이브러리 중 하나이다. 라이브러리 configuration을 확인하였는데, 2가지의 방식으로 Feature Flag 기능을 활성화할 수 있다. FeatureManagement
Class를 기반 혹은 Application.yml을 통한 properties 기반으로 운용할 수 있다.

### 간단 사용법

**라이브러리 추가하기**

```
object DependencyVersion {
    const val FEATURE_FLAG = "2.6.0"
}

/** feature flag */
implementation("com.azure.spring:azure-spring-cloud-feature-management-web:${DependencyVersion.FEATURE_FLAG}")
```

**Test 용도의 Controller 추가하기**

테스트 용도의 Controller 메서드 2개 생성

```kotlin
@RestController
@RequestMapping("/api/v1/feature-flag")
class TestController {
    @GetMapping("/a")
    @FeatureGate(feature = "feature-a")
    fun executeA() = ResponseEntity.ok("Feature A Execute")

    @GetMapping("/b")
    @FeatureGate(feature = "feature-b")
    fun executeB() = ResponseEntity.ok("Feature B Execute")
}
```

**활성화 혹은 비활성화 추가 - application.yml**

```yaml
feature-management:
    feature-a: true
    feature-b: false
```

위와 같이 설정하게 된 경우, **feature-a**는 api가 정상적으로 동작하지만, **feature-b**는 동작하지 않는다.

실제로 해당 flag가 설정되어 막는 부분은 filter이다. 가장 앞단의 filter에서 해당 어노테이션의 기능을 확인하고 필터링이 진행된다.

**Azure filter chain example code**

```java

@Component
public class FeatureFlagFilter implements Filter {

    @Autowired
    private FeatureManager featureManager;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!featureManager.isEnabledAsync("feature-a").block()) {
            chain.doFilter(request, response);
            return;
        }
        ...
        chain.doFilter(request, response);
    }
}
```

---

### 참고자료

- [repo](https://github.com/DongGeon0908/spring-feature-flag)

- [Feature Flags](https://www.baeldung.com/spring-feature-flags)

- [Azure](https://learn.microsoft.com/ko-kr/azure/azure-app-configuration/use-feature-flags-spring-boot)
