# 다섯 번째 학습: Non-blocking I/O

> 목표: blocking I/O와 non-blocking I/O의 차이를 작은 코드 흐름으로 이해한다.

## 오늘의 결론

```text
blocking read는 데이터가 올 때까지 thread가 멈춰 기다린다.
non-blocking read는 데이터가 없으면 바로 돌아온다.
바로 돌아오기 때문에 thread가 다른 일을 할 수 있지만, 무작정 반복 확인하면 CPU를 낭비할 수 있다.
그래서 실제 서버에서는 Selector나 이벤트 루프처럼 준비된 채널만 알려주는 구조가 필요하다.
```

## 한눈에 보기

| 개념 | 의미 |
| --- | --- |
| blocking I/O | 데이터가 올 때까지 현재 thread가 기다림 |
| non-blocking I/O | 데이터가 없으면 기다리지 않고 바로 반환 |
| polling | 데이터가 왔는지 반복해서 확인하는 방식 |
| busy wait | 너무 자주 polling해서 CPU를 낭비하는 상태 |
| Selector | 여러 채널 중 준비된 채널을 알려주는 Java NIO 도구 |

## 1. 왜 non-blocking I/O를 알아야 할까

네 번째 학습에서는 blocking I/O에서 thread가 `readLine()`에 멈춰 기다린다는 점을 확인했다.

이 방식은 이해하기 쉽지만 연결이 많아지면 문제가 생긴다.

```text
연결 1개 -> thread 1개 대기
연결 1000개 -> thread 1000개 대기 가능
```

그래서 많은 연결을 처리하는 서버는 thread가 매번 멈춰 기다리지 않는 구조를 사용한다.

## 2. non-blocking read는 기다리지 않는다

Java NIO의 `SocketChannel`은 non-blocking 모드로 설정할 수 있다.

```java
channel.configureBlocking(false);
```

이 상태에서 read를 호출했는데 아직 데이터가 없다면, read는 오래 기다리지 않고 바로 반환된다.

```java
int readBytes = channel.read(buffer);
```

반환값으로 현재 상태를 판단할 수 있다.

| 반환값 | 의미 |
| --- | --- |
| 양수 | 읽은 바이트 수 |
| 0 | 지금 읽을 데이터가 없음 |
| -1 | 상대방이 연결을 닫음 |

## 3. 이번 예제의 흐름

이번 예제는 서버가 non-blocking `SocketChannel`을 사용한다.

```text
server: accept client
non-blocking read: no data yet
client: send PING
server: read PING
```

처음 read에서는 클라이언트가 아직 데이터를 보내지 않았기 때문에 `0`이 나온다. 서버 thread는 멈춰 기다리지 않고 "아직 데이터 없음"을 확인한다.

## 4. 주의할 점

non-blocking I/O는 기다리지 않는다는 장점이 있지만, 아래처럼 계속 반복 확인하면 문제가 된다.

```text
while (true) {
    read();
}
```

데이터가 없는데도 계속 확인하면 CPU를 낭비한다. 이런 상태를 busy wait라고 볼 수 있다.

그래서 실제로는 Selector나 이벤트 루프를 사용한다.

```text
데이터가 준비된 채널만 알려줘.
준비된 채널이 있으면 그때 처리할게.
```

## 5. Netty와 연결되는 지점

Netty는 내부적으로 Java NIO 기반의 non-blocking I/O와 이벤트 루프 구조를 사용한다.

개발자는 직접 `Selector`를 다루지 않고도 `ChannelPipeline`, `Handler`, `EventLoopGroup` 같은 추상화로 네트워크 이벤트를 처리할 수 있다.

## 6. 면접에서 설명할 수 있는 문장

> Blocking I/O는 데이터가 올 때까지 thread가 멈춰 기다리는 방식이고, non-blocking I/O는 데이터가 없으면 바로 반환되는 방식입니다. non-blocking 방식은 thread를 오래 붙잡지 않는 장점이 있지만, 단순 반복 확인은 CPU를 낭비할 수 있어서 Selector나 이벤트 루프처럼 준비된 이벤트를 알려주는 구조와 함께 사용합니다.
