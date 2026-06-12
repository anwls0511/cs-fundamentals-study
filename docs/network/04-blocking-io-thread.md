# 네 번째 학습: Blocking I/O와 thread

> 목표: blocking I/O에서 thread가 왜 기다리게 되는지, 서버 확장성과 어떤 관련이 있는지 이해한다.

## 오늘의 결론

```text
blocking I/O는 데이터가 올 때까지 현재 thread가 멈춰 기다리는 방식이다.
readLine()은 클라이언트가 데이터를 보내기 전까지 반환되지 않는다.
연결마다 thread를 하나씩 쓰면 이해하기 쉽지만, 연결이 많아질수록 부담이 커진다.
Netty 같은 이벤트 기반 서버는 이 문제를 줄이기 위해 non-blocking I/O를 사용한다.
```

## 한눈에 보기

| 개념 | 의미 |
| --- | --- |
| blocking | 작업이 끝날 때까지 현재 thread가 기다리는 것 |
| blocking read | 데이터가 들어올 때까지 read 호출이 반환되지 않는 것 |
| thread | 코드를 실행하는 흐름 |
| thread per connection | 연결마다 thread 하나를 사용하는 단순한 서버 구조 |
| non-blocking I/O | 기다림을 thread 점유 없이 처리하는 방식 |

## 1. 왜 blocking I/O를 알아야 할까

Netty를 이해하려면 먼저 전통적인 blocking I/O가 어떤 방식인지 알아야 한다.

blocking I/O를 모르면 다음 질문이 잘 와닿지 않는다.

```text
왜 Netty는 이벤트 루프를 사용할까?
왜 thread를 연결 수만큼 만들지 않을까?
readLine()이 왜 위험할 수 있을까?
서버가 많은 연결을 받으면 어떤 문제가 생길까?
```

## 2. blocking read는 thread를 기다리게 한다

아래 코드는 클라이언트가 한 줄을 보낼 때까지 기다린다.

```java
String message = reader.readLine();
```

클라이언트가 아직 데이터를 보내지 않았다면 이 코드는 바로 다음 줄로 넘어가지 않는다. 현재 thread는 `readLine()`에서 멈춰 기다린다.

이것이 blocking I/O다.

## 3. 이번 예제의 흐름

이번 예제는 클라이언트가 일부러 1초 늦게 메시지를 보낸다.

```text
server: waiting for client message
client: sleep before sending message
client: send PING
server: read PING
server: send PONG
client: read PONG
```

서버 thread는 클라이언트가 `PING`을 보내기 전까지 `readLine()`에서 기다린다.

## 4. thread per connection의 한계

가장 단순한 서버는 연결마다 thread를 하나씩 만들 수 있다.

```text
client 1 -> thread 1
client 2 -> thread 2
client 3 -> thread 3
```

처음에는 이해하기 쉽지만, 연결이 많아지면 thread도 같이 늘어난다.

thread가 많아지면 다음 문제가 생길 수 있다.

```text
메모리 사용량 증가
context switching 비용 증가
느린 클라이언트가 thread를 오래 점유
동시 연결 수 증가에 취약
```

## 5. Netty와 연결되는 지점

Netty는 많은 연결을 적은 수의 thread로 처리하기 위해 이벤트 기반 구조를 사용한다.

blocking 방식에서는 thread가 read에서 기다리지만, non-blocking 방식에서는 데이터가 준비되었을 때 이벤트로 처리한다.

그래서 Netty를 공부할 때는 blocking I/O를 먼저 이해하는 것이 좋다. 그래야 Netty가 왜 이벤트 루프와 ChannelPipeline 구조를 사용하는지 더 자연스럽게 이해할 수 있다.

## 6. 면접에서 설명할 수 있는 문장

> Blocking I/O에서는 read 같은 호출이 데이터가 올 때까지 현재 thread를 멈춰 기다리게 합니다. 연결마다 thread를 하나씩 쓰는 구조는 단순하지만, 연결이 많아지면 thread 수와 context switching 비용이 늘어날 수 있습니다. Netty는 이런 문제를 줄이기 위해 non-blocking I/O와 이벤트 루프 기반 구조를 사용합니다.
