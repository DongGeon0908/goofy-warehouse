# Auth0 기반 Jwt

> JWT(토큰)는 JSON 웹 토큰(JSON Web Token)의 약자로, 인증과 정보 교환을 위해 사용되는 인코딩된 문자열입니다. JWT는 클레임(Claim) 기반의 토큰 방식으로, 페이로드에 클레임 정보를 포함하고, 이를 서명하여 신뢰할 수 있는 방식으로 토큰을 생성합니다.

### JWT의 구성요소

- Header (헤더): JWT의 유형 및 사용하는 암호화 알고리즘 등의 메타 정보를 포함합니다. 일반적으로 Base64로 인코딩되어 있습니다.
- Payload (페이로드): 토큰에 포함되는 클레임 정보를 담고 있습니다. 클레임은 이름-값 쌍으로 이루어져 있으며, 등록된 클레임, 공개 클레임, 비공개 클레임으로 구분됩니다. 클레임에는 토큰의 주제(
  subject), 만료 시간(expiration time), 발급자(issuer) 등의 정보가 포함될 수 있습니다. 마찬가지로 Base64로 인코딩되어 있습니다.
- Signature (서명): 헤더와 페이로드를 기반으로 생성되는 서명입니다. 서명은 비밀 키를 사용하여 생성되며, 토큰의 무결성을 보장합니다. 서명은 헤더, 페이로드, 비밀 키를 조합하여 생성되고, 이를 통해
  토큰이 변조되지 않았음을 확인할 수 있습니다.

### JWT의 주요 특징

- 확장성: JWT는 클레임 정보를 포함하고 있기 때문에 필요한 추가 정보를 페이로드에 포함하여 확장할 수 있습니다.
- 자가 수용(Self-contained): 토큰 자체에 필요한 모든 정보를 포함하고 있기 때문에, 서버의 세션 상태를 유지하지 않고도 토큰을 사용하여 인증과 권한 부여를 처리할 수 있습니다.
- 분산 환경 지원: 토큰은 클라이언트와 서버 간에 전달될 수 있으므로, 분산 환경에서 활용하기 용이합니다.

JWT는 웹 애플리케이션에서 인증과 권한 부여를 위해 널리 사용되는 표준화된 방법 중 하나입니다. 클라이언트가 인증 후 서버로부터 받은 JWT를 이용하여 계속적으로 요청을 보내고, 서버는 JWT를 검증하여 요청의
유효성과 사용자의 권한을 확인할 수 있습니다.

### Jwt의 Claim이란?

JWT(JavaScript Web Token)의 "claim"은 토큰에 포함되는 정보의 일부를 나타냅니다. JWT는 클레임의 집합으로 구성되며, 클레임은 JSON 객체로 표현됩니다. JWT는 보통 인증 및 권한
부여를 위해 사용되며, 클라이언트와 서버 간의 안전한 데이터 교환을 위해 사용됩니다.

클레임은 토큰에 첨부된 정보를 기술합니다. 토큰의 발급자, 수령자, 만료 일자, 토큰의 용도 등과 같은 다양한 정보를 포함할 수 있습니다. JWT는 세 가지 유형의 클레임을 가질 수 있습니다.

- 등록된 클레임(Registered Claims): 일반적으로 사용되는 표준 클레임으로, 선택적으로 사용할 수 있습니다. 예를 들어, "iss" (발급자), "exp" (만료 일자), "sub" (주제) 등이
  있습니다.
- 공개 클레임(Public Claims): 사용자 정의 클레임으로, 등록된 클레임과 충돌하지 않는 이름으로 정의됩니다. 토큰의 내용과 관련된 사용자 지정 데이터를 저장하는 데 사용됩니다.
- 비공개 클레임(Private Claims): 공개 클레임과 유사하지만, 미리 정의된 표준이나 공개적으로 알려진 클레임이 아닌 사용자 정의 데이터를 저장하기 위해 사용됩니다. 이러한 클레임은 서버와 클라이언트 간에
  협의되어야 합니다. 클레임은 토큰의 페이로드에 JSON 형식으로 인코딩되어 포함됩니다. JWT의 헤더와 페이로드는 Base64Url로 인코딩되고, 서명(옵션)이 추가되어 JWT를 형성합니다.

### Code Example

**application.yml**

```yml
auth:
    jwt:
        secret: 2436be3a13b3d5ade04a5f558ef8662dd38c1fcea855217d7755a27cf74402df1776b796e9a9f23ffb283c5d9f0c6d8502a62c1fe3e73c081caa938809a6d946
```

해당 값은 ` openssl rand -hex 64`을 통해 생성 가능

**JwtConfig.kt**

```kotlin
@Configuration
@ConfigurationProperties(prefix = "auth.jwt")
@ConfigurationPropertiesBinding
data class JwtConfig(
    @field:NotBlank
    var secret: String = "",
    val issuer: String = "goofy.kim"
)
```

**JwtService.kt**

```kotlin
@Service
class JwtService(
    private val jwtConfig: JwtConfig
) {
    private val jwtVerifier: JWTVerifier = JWT
        .require(Algorithm.HMAC256(jwtConfig.secret))
        .withIssuer("goofy.kim")
        .build()

    fun create(
        expiredAt: LocalDateTime,
        payloads: Map<String, String>
    ): String {
        return JWT.create()
            .withIssuer(jwtConfig.issuer)
            .withExpiresAt(Date.from(expiredAt.toInstant(ZoneOffset.of("+09:00"))))
            .apply {
                payloads.forEach { (key, value) ->
                    this.withClaim(key, value)
                }
            }.sign(Algorithm.HMAC256(jwtConfig.secret))
    }

    fun <T> verify(token: String, typeRef: TypeReference<T>): T {
        val payload = jwtVerifier.verify(token).payload.decodeBase64()
        return mapper.readValue(payload, typeRef)
    }

    fun verify(token: String): Map<String, Any> {
        val payload = jwtVerifier.verify(token).payload.decodeBase64()
        return mapper.readValue(payload)
    }
}
```

### Reference

- [auth0](https://auth0.com/docs/secure/tokens/json-web-tokens)
- [baeldung](https://www.baeldung.com/java-auth0-jwt)
- [Jwt Code Example](https://github.com/DongGeon0908/auth0-jwt)
