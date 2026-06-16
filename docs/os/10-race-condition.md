# 열 번째 학습: Race Condition

> 목표: 여러 thread가 같은 데이터를 동시에 다룰 때 왜 결과가 틀어질 수 있는지 정리한다.

## 오늘의 결론

```text
race condition은 실행 순서에 따라 결과가 달라지는 문제다.
여러 thread가 같은 공유 데이터를 동시에 읽고 수정할 때 자주 발생한다.
count++ 같은 짧은 코드도 실제로는 읽기, 계산, 쓰기 단계로 나뉜다.
공유 mutable state를 다룰 때는 동기화나 thread-safe한 자료구조를 고려해야 한다.
```

## 한눈에 보기

| 개념 | 의미 |
| --- | --- |
| shared state | 여러 thread가 함께 접근하는 상태 |
| mutable state | 값이 바뀔 수 있는 상태 |
| race condition | 실행 순서에 따라 결과가 달라지는 문제 |
| atomic | 중간에 끼어들 수 없는 하나의 작업처럼 처리되는 성질 |
| synchronization | 동시에 접근하면 안 되는 구간을 보호하는 방법 |

## 1. 왜 race condition을 알아야 할까

백엔드 서버는 보통 여러 요청을 동시에 처리한다.

```text
request A -> thread 1
request B -> thread 2
```

이때 두 요청이 같은 데이터를 동시에 바꾸면 문제가 생길 수 있다.

```text
thread 1: count 증가
thread 2: count 증가
```

코드로 보면 아주 단순해 보인다.

```java
count++;
```

하지만 이 한 줄도 실제로는 여러 단계로 나뉠 수 있다.

## 2. count++는 한 번에 끝나는 작업이 아니다

`count++`는 개념적으로 아래 세 단계로 볼 수 있다.

```text
1. 현재 count 값을 읽는다.
2. 읽은 값에 1을 더한다.
3. 계산한 값을 다시 저장한다.
```

count가 0이고, thread 두 개가 동시에 증가시킨다고 생각해보자.

```text
thread 1: count 읽기 -> 0
thread 2: count 읽기 -> 0
thread 1: 0 + 1 저장 -> 1
thread 2: 0 + 1 저장 -> 1
```

두 번 증가했으니 기대값은 2지만 실제 값은 1이 될 수 있다.

이처럼 실행 순서에 따라 결과가 달라지는 문제가 race condition이다.

## 3. Stack과 Heap 관점에서 보기

이전 학습에서 thread마다 stack은 따로 있고, heap은 공유될 수 있다고 정리했다.

```text
thread 1 stack
thread 2 stack

heap
  CounterService object
    count = 0
```

`count`가 heap 객체의 필드라면 여러 thread가 같은 값을 함께 본다.

```java
class CounterService {
    private int count;

    void increase() {
        count++;
    }
}
```

각 thread의 method 호출 stack은 다르지만, `CounterService` 객체는 같은 heap 객체일 수 있다. 그래서 동시에 `increase()`를 호출하면 같은 `count`를 두고 경쟁하게 된다.

## 4. 서버 코드에서 조심할 부분

Spring Bean이나 Service 객체는 보통 여러 요청 thread가 공유한다.

그래서 Service 안에 변경 가능한 필드를 두면 조심해야 한다.

```java
class OrderService {
    private int requestCount;

    void createOrder() {
        requestCount++;
    }
}
```

이런 코드는 트래픽이 적을 때는 문제없이 보일 수 있다. 하지만 동시에 요청이 몰리면 값이 누락될 수 있다.

공유 상태가 꼭 필요하다면 아래 방법을 고려해야 한다.

```text
synchronized로 보호한다.
AtomicInteger 같은 atomic 클래스를 사용한다.
ConcurrentHashMap 같은 thread-safe 자료구조를 사용한다.
가능하면 상태를 DB나 외부 저장소의 transaction으로 관리한다.
가능하면 Service 객체 안에 변경 가능한 상태를 두지 않는다.
```

## 5. Netty와 연결되는 지점

Netty Handler나 Service에서도 공유 객체를 사용할 수 있다.

현재 `netty-study`에서는 장비 상태를 `ConcurrentHashMap`에 저장한다.

```java
private final Map<String, DeviceStatus> latestStatuses = new ConcurrentHashMap<>();
```

여러 Channel에서 동시에 상태를 저장할 수 있기 때문에 일반 `HashMap`보다 `ConcurrentHashMap`을 쓰는 편이 안전하다.

다만 `ConcurrentHashMap`을 쓴다고 모든 동시성 문제가 자동으로 사라지는 것은 아니다. 여러 연산을 묶어서 하나의 규칙으로 지켜야 한다면 별도의 동기화나 설계가 필요할 수 있다.

## 6. 오늘의 정리

race condition은 여러 실행 흐름이 같은 데이터를 동시에 다루면서, 실행 순서에 따라 결과가 달라지는 문제다. 특히 공유되는 heap 객체의 mutable field를 여러 thread가 동시에 수정할 때 발생하기 쉽다.

`count++`처럼 짧은 코드도 읽기, 계산, 쓰기 단계로 나뉠 수 있으므로 항상 안전하다고 볼 수 없다. 서버 코드에서는 공유 상태를 줄이고, 꼭 필요하다면 동기화 도구나 thread-safe 자료구조를 사용해야 한다.
