# ionShutdownHook

> 에러가 발생했다. 그런데 ionShutdownHook이라는 단어가 계속 보인다. 이건 뭐지

- ionShutdownHook는 라이브러리 또는 프레임워크에서 제공하는 기능으로, 애플리케이션의 종료 시에 특정 작업을 수행할 수 있게 해줍니다. 이를테면, 애플리케이션이 종료되기 전에 리소스를 정리하거나 데이터를 저장하는 등의 작업을 할 수 있습니다.
- ionShutdownHook는 JVM(Java Virtual Machine)의 종료 과정에서 호출되는 훅(Hook)입니다. JVM은 애플리케이션의 실행을 관리하며, 애플리케이션의 실행이 종료될 때 일련의 종료 작업을 수행합니다. ionShutdownHook는 이러한 종료 과정 중 특정 시점에서 호출되는 메서드로, 사용자가 원하는 작업을 수행할 수 있도록 지원합니다.
- ionShutdownHook를 등록하면, 애플리케이션 종료 시점에서 해당 훅이 실행됩니다. 이때 훅은 사용자가 지정한 코드 블록 또는 메서드를 호출하여 원하는 작업을 수행합니다. 예를 들어, 애플리케이션이 종료될 때 오픈한 파일을 닫거나, 데이터베이스 연결을 해제하는 등의 정리 작업을 ionShutdownHook에서 처리할 수 있습니다.
- ionShutdownHook를 등록하기 위해서는 보통 애플리케이션의 진입점(main 메서드 등)에서 등록 코드를 작성해야 합니다. 아래는 Java에서 ionShutdownHook를 등록하는 간단한 예시입니다:

```java
public class MyApplication {
    public static void main(String[] args) {
        // ionShutdownHook 등록
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                // 종료 시 수행할 작업
                System.out.println("애플리케이션 종료 전에 수행되는 작업");
            }
        });

        // 애플리케이션 실행 코드
        // ...
    }
}
```

Runtime.getRuntime().addShutdownHook() 메서드를 사용하여 ionShutdownHook를 등록하고 있습니다. 등록된 훅은 애플리케이션이 종료될 때 자동으로 실행되며, 사용자는 run() 메서드 내에 원하는 작업을 구현하면 됩니다.

주의할 점은 ionShutdownHook 내에서는 시간이 오래 걸리는 작업을 수행하거나 새로운 스레드를 생성하는 것을 피해야 한다는 것입니다. 애플리케이션 종료 시점에서는 시스템이 빠르게 정리되어야 하므로, 불필요한 작업이나 느린 작업은 다른 스레드나 백그라운드 작업으로 처리하는 것이 좋습니다.

ionShutdownHook를 사용하면 애플리케이션이 제대로 종료될 때 필요한 작업을 수행할 수 있으며, 자원 누수나 데이터 손실을 방지할 수 있습니다.

### 발생 상황

- 애플리케이션이 정상적으로 종료될 때: 애플리케이션이 명시적으로 종료되거나 System.exit() 메서드를 호출하여 종료할 때, 등록된 ionShutdownHook가 실행됩니다.
- 애플리케이션이 비정상적으로 종료될 때: 예외가 발생하여 애플리케이션이 강제로 종료될 때, 등록된 ionShutdownHook가 실행됩니다. 이는 예외가 발생한 스레드에서의 ionShutdownHook 실행 후, 다른 스레드의 ionShutdownHook가 순차적으로 실행됩니다.


### 특징

- ionShutdownHook는 애플리케이션이 종료되기 전에 실행되므로, 애플리케이션의 자원 해제, 임시 파일 삭제, 데이터 저장 등의 정리 작업을 수행할 수 있습니다.
- ionShutdownHook는 다른 스레드에서 실행되므로, 별도의 스레드를 생성하여 시간이 오래 걸리는 작업을 수행할 수 있습니다. 그러나 애플리케이션 종료 전에 모든 ionShutdownHook가 완료되어야 합니다.
- ionShutdownHook는 등록된 순서대로 실행되므로, 작업 간의 의존성이 있는 경우에는 등록 순서를 고려해야 합니다.
