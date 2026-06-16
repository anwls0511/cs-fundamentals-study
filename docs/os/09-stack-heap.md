# 아홉 번째 학습: Stack과 Heap

> 목표: 프로그램이 실행될 때 메모리가 어떻게 나뉘어 사용되는지, stack과 heap의 차이를 정리한다.

## 오늘의 결론

```text
stack은 method 호출 흐름과 지역 변수를 관리하는 메모리 영역이다.
heap은 객체가 생성되는 메모리 영역이다.
thread마다 stack은 따로 가지지만, 같은 process 안의 thread들은 heap을 공유한다.
이 차이를 알아야 동시성 문제와 GC, 메모리 누수를 이해하기 쉬워진다.
```

## 한눈에 보기

| 개념 | 의미 |
| --- | --- |
| stack | method 호출 정보와 지역 변수가 쌓이는 영역 |
| stack frame | method 하나가 호출될 때 stack에 생기는 실행 정보 |
| heap | 객체가 생성되고 저장되는 영역 |
| reference | heap에 있는 객체를 가리키는 값 |
| GC | 더 이상 사용하지 않는 heap 객체를 정리하는 JVM 기능 |

## 1. 왜 stack과 heap을 알아야 할까

Java 코드를 작성하다 보면 객체를 만들고 method를 호출하는 일은 자연스럽게 한다.

```java
DeviceStatus status = new DeviceStatus("device-1", 25.1, 40.2, 1717830000L);
service.save(status);
```

하지만 이 코드가 메모리에서 어떻게 움직이는지 모르면 아래 질문에서 막히기 쉽다.

```text
지역 변수는 어디에 저장될까?
new로 만든 객체는 어디에 생길까?
method 호출이 끝나면 무엇이 사라질까?
thread가 heap을 공유한다는 말은 무슨 뜻일까?
```

## 2. Stack은 method 호출 흐름을 관리한다

stack은 method가 호출될 때마다 실행 정보를 쌓는 영역이다.

```text
main()
  -> service.save()
  -> repository.put()
```

method 하나가 호출될 때마다 stack frame이 생긴다고 볼 수 있다.

```text
stack
  [repository.put frame]
  [service.save frame]
  [main frame]
```

method 실행이 끝나면 해당 frame은 stack에서 제거된다.

```text
repository.put 종료
  -> repository.put frame 제거
```

지역 변수도 보통 이 stack frame 안에서 관리된다.

```java
void save(DeviceStatus status) {
    String deviceId = status.deviceId();
}
```

여기서 `deviceId` 같은 지역 변수는 method 실행 범위 안에서만 의미가 있다.

## 3. Heap은 객체가 저장되는 곳이다

`new`로 만든 객체는 heap에 생성된다.

```java
DeviceStatus status = new DeviceStatus("device-1", 25.1, 40.2, 1717830000L);
```

이 코드를 단순화하면 이렇게 볼 수 있다.

```text
stack
  status reference

heap
  DeviceStatus object
```

중요한 점은 변수 `status` 자체가 객체를 직접 들고 있는 것이 아니라, heap에 있는 객체를 가리키는 reference를 가지고 있다는 점이다.

## 4. Thread와 연결해서 보기

앞에서 process와 thread를 정리했다.

같은 process 안의 thread들은 heap을 공유하지만, stack은 thread마다 따로 가진다.

```text
process
  heap
    DeviceStatusService object
    ConcurrentHashMap object

  thread 1 stack
  thread 2 stack
```

이 말은 thread마다 method 호출 흐름은 따로 있지만, 같은 heap 객체를 동시에 볼 수 있다는 뜻이다.

```text
thread 1 -> service.save()
thread 2 -> service.save()
```

두 thread가 같은 heap 객체의 값을 동시에 바꾸면 race condition이 생길 수 있다.

## 5. 백엔드 코드와 연결되는 지점

서버 애플리케이션에서는 여러 요청이 동시에 들어올 수 있다.

```text
request A -> thread 1
request B -> thread 2
```

각 thread의 stack은 따로 있지만, Spring Bean이나 Service 객체는 보통 heap에 하나 만들어져 여러 thread가 함께 사용한다.

```text
thread 1 stack -> UserService reference
thread 2 stack -> UserService reference

heap -> UserService object
```

그래서 Service 안에 공유 mutable state를 둘 때는 조심해야 한다.

```java
class CounterService {
    private int count;

    void increase() {
        count++;
    }
}
```

`count`는 heap 객체의 필드다. 여러 thread가 동시에 `increase()`를 호출하면 값이 기대와 다르게 바뀔 수 있다.

## 6. 오늘의 정리

stack은 method 호출 흐름과 지역 변수를 관리하고, heap은 객체가 저장되는 영역이다. Java에서 `new`로 만든 객체는 heap에 생기고, 지역 변수는 그 객체를 가리키는 reference를 가질 수 있다.

thread마다 stack은 따로 있지만 heap은 같은 process 안에서 공유된다. 그래서 여러 thread가 같은 heap 객체를 동시에 수정하면 동시성 문제가 생길 수 있다. 이 차이를 이해하면 race condition, thread-safe, GC 같은 주제를 더 자연스럽게 이어서 볼 수 있다.
