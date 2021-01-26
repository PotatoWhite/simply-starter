# Pramework - Servicable, Controllable, Eventable

Pramework은 Spring 기반의 Restful API, Event-Driven 개발시 중복적인 코드를 줄여 준다.
중복적인 코드를 줄임으로써 Application 개발자가 Project의 목적인 Business Logic에 더 집중할 수 있게 하는 것이 목적이다.

## Features

- Servicable : Jpa 기반 Entity의 기본적인 CRUD를 생성한다.
- Controllable : Servicable 기반의 Restful API를 생성한다.
- Eventable : Kafka를 통해 Entity의 변경시 다른 서비스로 변경을 알린다.

기본적으로 Pramework은 Spring Framework을 이용한 Restful API를 개발하는 것을 추상화한다.
추상화하는 내역으로는 @Service로 대표되는 Service, @RestConroller로 대표되는 Controllable 마지막으로 Event Driven을 위한 Entity변경 시 필요로한 Service에서의 Event Listener를 제공한다.


## Serviceable
- 특정 Entity의 관리를 목적으로하는 CRUD 기능을 자동화한다.

### Serviceable Method
- Entity 관리를 위한 Method는 다음과 같다.
```java
  // create
  Optional<T1> create(T1 entity) throws EntityExistsException;

  // retrieve
  Optional<T1> retrieve(T2 id);

  // retrieve all
  List<T1> retrieveAll();

  // update
  Optional<T1> patch(T2 id, Map<String, Object> fields) throws GsonTools.JsonObjectExtensionConflictException;

  // delete
  void deleteById(T2 id);

  // delete
  void delete(T1 entity);

  // replace
  Optional<T1> replace(T2 id, T1 replace);
  ```

### Serviceable Example
- Serviceable을 사용하기 위해서는 'ServiceableImpl' 을 상속 받는다.
- 상속받아 새로 만든 Class를 통해 Entity를 다루는 Repository를 주입한다.

```java
@Service
public class UserService extends ServiceableImpl<User, Long> {
  protected UserService(UserRepository repository) {
    super(repository);
  }
}

```

### Serviceable의 확장
- pramework에서 제공하는 기능외 확장이 가능하다.
- 확장을 위해서 목적 실제 Service Class에서 Method를 정의한다.
```java
@Service
public class UserService extends ServiceableImpl<User, Long> {
    // Repository를 주입 받는다.
    private final UserRepository repository;

    protected UserService(UserRepository repository) {
        super(repository);
        this.repository = repository;
    }

    // 새로운 Method 를 작성한다.
    public boolean isExist(Long id){
        return repository.existsById(id);
    }
}
```

## Controllable
- Serviceable Bean을 기반으로 CRUD 기능을 RestfulAPI 형태로 expose 한다.

### Controllable Method
- Restful API를 제공하는 Method는 다음과 같다.
```java
  public interface Controllable<T1, T2> {
    // Post
    ResponseEntity create(T1 createForm);

    // Put
    ResponseEntity replaceById(T2 id, T1 replace);

    // Patch
    ResponseEntity updateById(T2 id, Map<String, Object> fields);

    // Get | Head
    ResponseEntity get(T2 id);

    // Get
    ResponseEntity getAll();

    // Delete
    ResponseEntity deleteById(T2 id);
}
```

### Controllable Example
- Controllable 을 사용하기 위해서는 'ControllableImpl' 을 상속 받는다.
- 상속받아 새로 만든 Class의 생성자를 통해 Servieable Bean을 주입한다.
```java
@RestController
@RequestMapping("/users")
public class UserController extends ControllableImpl <User, Long> {

  public UserController(UserService service) {
    super(service);
  }
}
```


### Controllable의 Response Code
|Method|ResponseCode|Reason|Comment|
|---|---|---|---|
|POST|201 Created|성공| 
|POST|400 Bad Request|실패|규격 오류|
|POST|409 Conflict|실패|이미 존재함|
|PUT|200 OK|성공|
|PUT|204 No contents|실패|컨텐츠 미존재|
|PATCH|200 OK|성공|
|PATCH|204 No contents|실패|컨텐츠 미존재|
|PATCH|400 Bad Request|실패|규격 오류|

// https://mvnrepository.com/artifact/io.github.scouter-project/scouter-agent-java
compile group: 'io.github.scouter-project', name: 'scouter-agent-java', version: '2.10.2'
