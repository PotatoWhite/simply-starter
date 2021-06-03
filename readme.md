# Simply - Serviceable, Controllable, Eventable, Clientable

Simply은 Spring 기반의 Restful API, Event-Driven 개발시 중복적인 코드를 줄여 준다. 중복적인 코드를 줄임으로써 Application 개발자가 Project의 목적인 Business
Logic에 더 집중할 수 있게 하는 것이 목적이다.

* Quickstart 관련한 사용 예제는 https://github.com/PotatoWhite/simply-quickstart 를 참고하여 주세요.
* Kafka Binder 관련한 사용 예제는 https://github.com/PotatoWhite/simply-messaging-producer , https://github.com/PotatoWhite/simply-messaging-consumer 를 참고하여 주세요.

## Features

- Serviceable : Jpa 기반 Entity의 기본적인 CRUD를 생성한다.
- Controllable : Serviceable 기반의 Restful API를 생성한다.
- Eventable : Kafka를 통해 Entity의 변경시 다른 서비스로 변경을 알린다.
- Clientable : Kafka를 통해 Entity의 변경시 다른 서비스로 변경을 알린다.

기본적으로 Simply은 spring framework을 이용한 Restful API 등을 개발 할 때 도움이 되고자 한다. 추상화하는 내역으로는 @Service로 대표되는 Service,
@RestConroller로 대표되는 Controllable 마지막으로 Event Driven을 위한 Entity변경 시 필요로한 Service에서의 Event Listener를 제공한다. 또한 부가적으로
Client모듈을 제공해 개발자가 마치 로컬 메소드를 호출하 듯 원격의 Restful API를 호출하여 사용할 수 있게 한다.

## Build

Maven 중앙 Repository로 부터 Library를 가져온다.

```groovy
repositories {
    mavenCentral()
}
```

## 사용방법

- gradle 을 이용해 사용한다.

```groovy
[build.gradle]

implementation 'io.easywalk:simply-common:0.0.4.RELEASE'
implementation 'io.easywalk:simply-serviceable:0.0.4.RELEASE'
implementation 'io.easywalk:simply-controllable:0.0.4.RELEASE'
implementation 'io.easywalk:simply-eventable-kafka-binder:0.0.4.RELEASE'
implementation 'io.easywalk:simply-clientable:0.0.4.RELEASE'
```

## 설정

* eventable에 한하여 별도의 설정이 필요하다.
* entity-base-package는 Producer가 Kafka의 Topic을 생성하기 위해 Bean의 Scan 지점을 설정한다.
* number-of-replicas는 Topic의 Replicas 갯수이다.
* number-of-partitions는 Topic의 Partitions 갯수이다.

```yaml
simply:
  eventable:
    entity-base-package: io.easywalk.demo.entities
    topic-property:
      number-of-replicas: 1
      number-of-partitions: 10
```

## simply-common

- simply 에서 제공하고자 하는 Spec 정의.

### SimplySpec

```java
public interface SimplySpec<T, ID> {
    T create(T createForm) throws Throwable;

    T replaceById(ID id, T replace) throws Throwable;

    T updateById(ID id, Map<String, Object> fields) throws Throwable;

    T get(ID id) throws Throwable;

    List<T> getAll() throws Throwable;

    void deleteById(ID id) throws Throwable;

    void delete(T entity) throws Throwable;
}  
```

## Serviceable

- 특정 Entity의 관리를 목적으로하는 CRUD 기능을 자동화한다.

### Serviceable Example

- Serviceable을 사용하기 위해서는 'AbstractServiceable' 을 상속 받는다.
- 상속받아 새로 만든 Class를 통해 Entity를 다루는 Repository를 주입한다.

```java

@Service
public class UserService extends AbstractServiceable<User, Long> {
    protected UserService(UserRepository repository) {
        super(repository);
    }
}

```

### Serviceable의 확장

- Simply에서 제공하는 기능외 확장이 가능하다.
- 확장을 위해서 목적 실제 Service Class에서 Method를 정의한다.

```java

@Service
public class UserService extends AbstractServiceable<User, Long> {
    // Repository를 주입 받는다.
    private final UserRepository repository;

    protected UserService(UserRepository repository) {
        super(repository);
        this.repository = repository;
    }

    // 새로운 Method 를 작성한다.
    public boolean isExist(Long id) {
        return repository.existsById(id);
    }
}
```

## Controllable

- Serviceable Bean을 기반으로 CRUD 기능을 RestfulAPI 형태로 expose 한다.

### EnableSimplyControllable

* Using "@EnableSimplyControllable" annotation, activate controllable features.

```java

@EnableSimplyControllable
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### Controllable Method

- Restful API를 제공하는 Method는 다음과 같다.

```java
public interface Controllable<T1, T2> {

    T1 create(T1 createForm) throws Throwable;

    T1 replaceById(T2 id, T1 replace) throws Throwable;

    T1 updateById(T2 id, Map<String, Object> fields) throws Throwable;

    T1 get(T2 id) throws Throwable;

    List<T1> getAll() throws Throwable;

    void deleteById(T2 id) throws Throwable;
}
```

### Controllable Example

- Controllable 을 사용하기 위해서는 'AbstractControllable' 을 상속 받는다.
- 상속받아 새로 만든 Class의 생성자를 통해 Servieable Bean을 주입한다.
- @SimplyControllableResponse 은 Controller Advice를 활성화한다. Advice에서 에러처리는 하기 Response Code를 참조한다.

```java

@RestController
@SimplyControllableResponse
@RequestMapping("/users")
public class UserController extends AbstractControllable<User, Long> {
    public UserController(UserService service) {
        super(service);
    }

}
```

### Controllable의 처리완료 Response Code

|Method|ResponseCode|Reason|Comment|
|---|---|---|---|
|POST|201 Created|성공|
|PUT|200 OK|성공|
|PATCH|200 OK|성공|
|DELETE|204 No Contents|성공||

### Controllable의 처리실패 Response Code

|Method|ResponseCode|Reason|Comment|
|---|---|---|---|
|POST|400 Bad Request|실패|규격 오류|
|POST|409 Conflict|실패|이미 존재함|
|PUT|404 Not Found|실패|컨텐츠 미존재|
|PATCH|404 No Contents|실패|컨텐츠 미존재|
|PATCH|400 Bad Request|실패|규격 오류|
|undefined|500 Internal Server Error|실패|

# Eventable

* Eventable은 Application 간에 변경된 Entity를 Provisioning 및 Event Driven을 지원한다.
* Application 간의 통신은 Kafka를 Broker로 사용한다.

## Eventable 의 구성

* Entity의 Entity의 Ownership이 있는 Producer Application은 '@EnableSimplyProducer' annotation을 통해 활성화한다.
* Entity를 사용하는 Consumer Application은 'AbstractSimplyConsumer' 를 상속받아 사용한다.

## Producer

* Using "@EnableSimplyProducer" annotation, activate producer features.

```java

@EnableSimplyProducer
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### Entity의 정의

* 일반적인 JPA Entity형식이지만 Event Driven이 필요한 Entity는 Eventable interface를 적용한다.

```java

@Getter
@Setter
@ToString
@Entity
public class User implements SimplyEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @NotNull
    @Column(unique = true)
    private String email;
}
```

### @SimplyProducer Annotation

* Simply Serviceable 을 시용하는 Service Class를 정의할 때 @SimplyProducer를 사용해 Event를 발행할 Service를 구현할 수 있다.
* @SimplyProducer("user") 발행될 Topic은 "user" 와 같이 지정한다.

```java

@SimplyProducer("user")
@Service
public class UserService extends AbstractServiceable<User, Long> {

    protected UserService(UserRepository repository) {
        super(repository);
    }
}
```

## Consumer

* Consumer는 동일한 상의 User를 전달 받기 위해, Producer에서 생성한 User Class를 사용한다. Gradle이나 Maven의 Module을 이용하는 것을 권장한다.

### Entity의 정의

* 일반적인 JPA Entity형식이지만 Event Driven이 필요한 Entity는 Eventable interface를 적용한다.

```java

@Getter
@Setter
@ToString
@Entity
public class User implements SimplyEntity<Long> {
    @Id
    private Long id;

    private String name;

    @NotNull
    @Column(unique = true)
    private String email;
}
```

## Listener의 구현

* Producer가 발행하 event는 AbstractSimplyConsumer 통해 전달 받을 수 있다.

### SimplyConsumer를 이용한 Entity 복제 예시
```java
@Slf4j
@Component
public class UserListener extends AbstractSimplyConsumer<User, Long> {
    private static String         TOPIC = "user";
    private final  UserRepository repository;

    protected UserListener(UserRepository repository) {
        super(TOPIC, User.class);
        this.repository = repository;
    }
    
    @SneakyThrows
    @Override
    public void on(SimplyEventableMessage<User> message) {
        switch (message.getEventType()) {
            case "CREATE":
            case "UPDATE":
                User user = convertToEntity(message.getPayload(), User.class);
                repository.save(user);
                break;
            case "DELETE":
                repository.deleteById(Long.valueOf(message.getKey()));
                break;
            default:
                log.error("messsage {}", message);
        }
    }
}
```

