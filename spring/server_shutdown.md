# Server Shutdown

> 서버의 shutdown 방식에 대해 학습하자

### Default Options

기본적으로 Spring에서 서버 shutdown 옵션은 `IMMEDIATE`이다. 즉시 서버의 구동을 중지시키는 것이다. 하지만, 이런 옵션은 현재 작업중인 API의 응답 손실을 야기시킬 수 있다. 그렇기 때문에
데이터 혹은 요청 손실을 줄이기 위해서 `GRACEFUL`을 이용하는 것을 권한다. (물론, 상황에 따라 다름)

### IMMEDIATE

기본으로 설정된 shutdown 기법이다. `org.springframework.boot.web.server.Shutdown`을 참고하자. 서버 종료 요청이 들어온 경우, 즉시 서버의 가동을 중지시킨다.

### GRACEFUL

서버 종료 요청이 들어오더라도, 현재 작업 중인 내역을 수행하고 중지시키는 기법이다. 아래의 yml 설정을 기반으로 한다.

```yaml
server:
    shutdown: immediate

spring:
    lifecycle:
        timeout-per-shutdown-phase: 30s
```

셧다운의 타임 딜레이 값을 설정할 수 있는데, default로 30초로 구성되어 있다.
`org.springframework.boot.autoconfigure.context.LifecycleProperties` 참고
