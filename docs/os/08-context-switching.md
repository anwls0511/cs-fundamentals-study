# 여덟 번째 학습: Context Switching

> 목표: CPU가 여러 작업을 번갈아 처리하는 방식과 context switching 비용을 이해한다.

## 오늘의 결론

```text
CPU core 하나는 한 순간에 하나의 실행 흐름만 처리한다.
여러 process나 thread가 동시에 실행되는 것처럼 보이는 이유는 OS가 빠르게 번갈아 실행시키기 때문이다.
이때 실행 중이던 상태를 저장하고 다음 실행 상태를 복원하는 작업을 context switching이라고 한다.
```

## 한눈에 보기

| 개념 | 의미 |
| --- | --- |
| context | 다시 실행하기 위해 기억해야 하는 실행 상태 |
| context switching | 실행 중인 작업을 바꾸기 위해 상태를 저장하고 복원하는 작업 |
| scheduler | 어떤 작업을 CPU에 올릴지 결정하는 OS 구성 요소 |
| time slice | 한 작업이 CPU를 사용하는 짧은 시간 단위 |
| overhead | 실제 비즈니스 로직이 아니라 전환 자체에 쓰이는 비용 |

## 1. CPU는 진짜로 동시에 다 처리하지 않는다

CPU core 하나는 한 순간에 하나의 thread만 실행할 수 있다.

그런데 우리는 여러 프로그램이 동시에 실행되는 것처럼 느낀다.

```text
브라우저 실행
음악 재생
서버 실행
터미널 입력
```

이게 가능한 이유는 OS가 작업을 아주 빠르게 번갈아 실행시키기 때문이다.

```text
thread A 실행
thread B 실행
thread C 실행
thread A 다시 실행
```

사람 입장에서는 동시에 실행되는 것처럼 보이지만, CPU 입장에서는 짧은 시간 단위로 계속 바뀌는 것이다.

## 2. Context는 다시 실행하기 위한 상태다

thread를 잠깐 멈췄다가 나중에 다시 실행하려면, 어디까지 실행했는지 기억해야 한다.

예를 들면 이런 정보들이 필요하다.

```text
어느 명령어까지 실행했는지
CPU register 값이 무엇이었는지
stack 상태가 어땠는지
```

이런 실행 상태를 context라고 볼 수 있다.

## 3. Context Switching 흐름

context switching은 단순히 이름만 바꾸는 작업이 아니다.  
현재 작업을 멈추고 다음 작업을 이어서 실행할 수 있도록 상태를 저장하고 복원한다.

```text
thread A 실행 중
  -> thread A context 저장
  -> thread B context 복원
  -> thread B 실행
```

이 과정은 필요하지만 공짜는 아니다.

```text
비즈니스 로직 실행 시간
+ 작업 전환 비용
```

thread가 너무 많으면 실제 일을 하는 시간보다 전환 비용이 커질 수 있다.

## 4. 백엔드 서버와 연결해서 보기

요청마다 thread를 하나씩 만든다고 생각해보자.

```text
client 1 -> thread 1
client 2 -> thread 2
client 3 -> thread 3
...
client 1000 -> thread 1000
```

thread가 많아지면 동시에 처리할 수 있는 것처럼 보이지만, CPU core 수보다 훨씬 많은 thread가 생기면 OS는 계속 thread를 바꿔가며 실행해야 한다.

```text
thread 증가
  -> scheduler가 바꿔야 할 대상 증가
  -> context switching 증가
  -> CPU가 실제 로직보다 전환에 시간을 더 쓸 수 있음
```

그래서 많은 연결을 다루는 서버에서는 thread를 무한히 늘리는 방식이 부담이 될 수 있다.

## 5. Netty와 연결되는 지점

Netty의 EventLoop는 적은 수의 thread로 여러 Channel 이벤트를 처리한다.

```text
많은 connection
  -> 적은 EventLoop thread
  -> 이벤트 기반 처리
```

이 구조를 이해하려면 context switching 비용을 알아야 한다.  
Netty가 thread를 적게 쓰려는 이유 중 하나는 thread가 많아질수록 전환 비용과 관리 비용이 커질 수 있기 때문이다.

물론 thread를 적게 쓰는 대신 주의할 점도 있다.

```text
EventLoop thread에서 오래 걸리는 작업을 하면
그 EventLoop가 맡은 다른 Channel 처리도 늦어진다.
```

즉, context switching을 줄이려는 구조와 event loop를 막지 않아야 하는 이유는 같이 이해해야 한다.

## 6. 오늘의 정리

context switching은 OS가 실행 중인 process나 thread를 바꿀 때 현재 실행 상태를 저장하고 다음 실행 상태를 복원하는 작업이다. 이 작업은 꼭 필요하지만 비용이 있기 때문에 thread가 너무 많아지면 실제 로직보다 전환 비용이 커질 수 있다. 그래서 서버에서는 요청마다 thread를 무한히 늘리는 구조보다 thread 수와 blocking 작업을 함께 고려해야 한다.
