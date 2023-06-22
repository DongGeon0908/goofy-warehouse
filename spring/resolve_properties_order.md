# Resolve Properties Order

> Spring Boot는 애플리케이션 코드를 다른 환경에서 동일하게 작업할 수 있도록 구성 파일을 외부화할 수 있게 해줍니다. Java 프로퍼티 파일, YAML 파일, 환경 변수, 명령줄 인자와 같은 다양한 외부 구성 소스를 사용할 수 있습니다.

프로퍼티 값은 @Value 어노테이션을 사용하여 직접 빈에 주입하거나, Spring의 Environment 추상화를 통해 액세스하거나, @ConfigurationProperties를 통해 구조화된 객체에 바인딩할 수 있습니다.

Spring Boot는 값을 간편하게 재정의할 수 있는 특정한 PropertySource 순서를 사용합니다. 나중에 등록된 속성 소스는 이전에 정의된 값을 재정의할 수 있습니다. 속성 소스는 다음 순서로 고려됩니다:

1. 기본 프로퍼티(Default properties): SpringApplication.setDefaultProperties()로 지정한 기본 프로퍼티입니다.
2. @PropertySource 어노테이션: @Configuration 클래스에 정의된 @PropertySource 어노테이션을 통해 등록된 프로퍼티 소스입니다. 다만, 이러한 프로퍼티 소스는 애플리케이션 컨텍스트가 새로 고쳐질 때까지 Environment에 추가되지 않습니다.
3. 구성 데이터(Config data): application.properties와 같은 구성 파일에서 가져온 프로퍼티입니다. 이러한 파일들은 일반적으로 클래스패스에 위치합니다.
4. RandomValuePropertySource: 이 속성 소스는 random.* 네임스페이스에서만 프로퍼티를 제공합니다.
5. 운영체제 환경 변수(OS environment variables)
6. 자바 시스템 프로퍼티(System.getProperties())
7. java:comp/env의 JNDI 속성
8. ServletContext 초기 매개변수
9. ServletConfig 초기 매개변수
10. SPRING_APPLICATION_JSON에서 가져온 프로퍼티 (환경 변수나 시스템 프로퍼티에 포함된 인라인 JSON)
11. 명령줄 인자
12. 테스트에서의 properties 속성 (@SpringBootTest 및 특정 슬라이스를 테스트하는 테스트 어노테이션에 사용 가능)
13. 테스트에서의 @TestPropertySource 어노테이션
14. Devtools가 활성화된 경우 $HOME/.config/spring-boot 디렉터리의 Devtools 전역 설정 프로퍼티

**구성 데이터 파일은 다음 순서로 고려**

1. 패키지된 JAR 내의 애플리케이션 프로퍼티 (application.properties 및 YAML 변형)
2. 패키지된 JAR 내의 프로필별 애플리케이션 프로퍼티 (application-{profile}.properties 및 YAML 변형)
3. 패키지된 JAR 외부의 애플리케이션 프로퍼티 (application.properties 및 YAML 변형)
4. 패키지된 JAR 외부의 프로필별 애플리케이션 프로퍼티 (application-{profile}.properties 및 YAML 변형)

### Reference

- [Resolve Properties Order](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
