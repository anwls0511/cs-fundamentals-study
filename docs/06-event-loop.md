# 여섯 번째 학습: Event Loop

> 목표: 이벤트 루프가 무엇이고, Netty의 EventLoopGroup을 이해하는 데 왜 중요한지 정리한다.

## 오늘의 결론

```text
이벤트 루프는 이벤트 큐에서 작업을 하나씩 꺼내 처리하는 반복 구조다.
요청이 올 때마다 새 thread를 만드는 방식과 다르게, 정해진 thread가 여러 이벤트를 순서대로 처리한다.
Netty의 EventLoop는 Channel에 발생한 read, write, accept 같은 이벤트를 처리한다.
```

## 한눈에 보기

| 개념 | 의미 |
| --- | --- |
| event | 처리해야 할 일 |
| event queue | 이벤트가 쌓이는 큐 |
| event loop | 큐에서 이벤트를 꺼내 반복 처리하는 구조 |
| handler | 이벤트가 왔을 때 실행되는 처리 코드 |
| Netty EventLoop | Channel의 I/O 이벤트를 처리하는 thread |

## 1. 왜 이벤트 루프를 알아야 할까

blocking I/O 방식에서는 연결마다 thread를 하나씩 두는 구조를 쉽게 떠올릴 수 있다.

```text
client 1 -> thread 1
client 2 -> thread 2
client 3 -> thread 3
```

하지만 연결이 많아질수록 thread 수가 늘어나고, context switching 비용도 커질 수 있다.

이벤트 루프는 다른 관점으로 문제를 본다.

```text
이벤트가 발생하면 큐에 넣고,
정해진 loop가 하나씩 꺼내 처리한다.
```

## 2. 이벤트 루프의 기본 구조

이벤트 루프는 개념적으로 이렇게 생겼다.

```text
while (running) {
    event = queue.take();
    handle(event);
}
```

중요한 점은 이벤트를 순서대로 처리한다는 것이다.

```text
ACCEPT -> READ -> WRITE -> CLOSE
```

## 3. 이번 예제의 흐름

이번 예제는 네트워크 코드는 아니고, 이벤트 루프 구조만 단순화해서 보여준다.

```text
event-loop: handle ACCEPT
event-loop: handle READ
event-loop: handle WRITE
event-loop: handle CLOSE
```

각 이벤트는 큐에 들어가고, 이벤트 루프 thread가 하나씩 꺼내 처리한다.

## 4. Netty와 연결되는 지점

Netty에서는 `EventLoopGroup`이 여러 EventLoop를 관리한다.

```text
EventLoopGroup
  -> EventLoop 1
  -> EventLoop 2
  -> EventLoop 3
```

각 Channel은 보통 하나의 EventLoop에 등록되고, 그 EventLoop가 해당 Channel의 I/O 이벤트를 처리한다.

그래서 Netty Handler 안에서 오래 걸리는 작업을 직접 수행하면 이벤트 루프 thread가 막힐 수 있다. 이 점을 이해하면 Netty 코드에서 왜 blocking 작업을 조심해야 하는지 설명할 수 있다.

## 5. 면접에서 설명할 수 있는 문장

> 이벤트 루프는 이벤트 큐에서 작업을 하나씩 꺼내 처리하는 반복 구조입니다. Netty에서는 EventLoop가 Channel의 read, write 같은 I/O 이벤트를 처리합니다. 이벤트 루프 thread가 막히면 해당 EventLoop에 등록된 다른 Channel 처리도 늦어질 수 있기 때문에, Handler 안에서 오래 걸리는 blocking 작업은 조심해야 합니다.
