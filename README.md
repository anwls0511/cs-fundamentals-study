# cs-fundamentals-study

비전공 백엔드 개발자가 전공자 수준의 기본기를 따라가기 위해 정리하는 CS 학습 기록입니다.

목표는 특정 프레임워크 사용법을 외우는 것이 아니라, 코드를 리뷰할 때 **왜 이렇게 동작하는지** 설명할 수 있는 기반을 만드는 것입니다.

## 학습 방향

```text
코드를 작성할 수 있다
  -> 코드가 왜 그렇게 동작하는지 설명할 수 있다
  -> 어떤 구현이 위험한지 리뷰할 수 있다
  -> 설계 선택의 이유와 trade-off를 말할 수 있다
```

## 학습 맵

| 영역 | 상태 | 목표 |
| --- | --- | --- |
| Network | 진행 중 | TCP, socket, I/O, event loop를 이해한다. |
| Operating System | 예정 | process, thread, memory, context switching을 이해한다. |
| Data Structure | 예정 | List, Map, Queue, Hash를 구현 관점에서 이해한다. |
| Database | 예정 | index, transaction, lock을 백엔드 관점에서 이해한다. |
| Concurrency | 예정 | race condition, deadlock, thread-safe를 이해한다. |

## Network

Netty를 공부하다가 나온 질문을 시작점으로, 네트워크와 I/O 기본기를 정리하고 있습니다.  
Netty 자체가 목적은 아니고, TCP 서버 코드가 왜 그런 구조를 가지는지 이해하는 것이 목적입니다.

| 순서 | 문서 | 핵심 질문 |
| --- | --- | --- |
| 01 | [바이트와 인코딩](docs/network/01-byte-hex-encoding.md) | 데이터는 실제로 어떤 바이트로 오갈까? |
| 02 | [IP, port, socket](docs/network/02-ip-port-socket.md) | 클라이언트는 서버를 어떻게 찾아갈까? |
| 03 | [TCP 연결 생명주기](docs/network/03-tcp-connection-lifecycle.md) | 연결은 언제 만들어지고 언제 닫힐까? |
| 04 | [Blocking I/O와 thread](docs/network/04-blocking-io-thread.md) | 왜 thread가 기다리게 될까? |
| 05 | [Non-blocking I/O](docs/network/05-non-blocking-io.md) | 기다리지 않는 I/O는 어떻게 동작할까? |
| 06 | [Event Loop](docs/network/06-event-loop.md) | 적은 thread로 많은 이벤트를 어떻게 처리할까? |

## 예제 실행

```powershell
.\gradlew.bat runHexExample
.\gradlew.bat runLengthHeaderExample
.\gradlew.bat runSocketExample
.\gradlew.bat runTcpLifecycleExample
.\gradlew.bat runBlockingIoExample
.\gradlew.bat runNonBlockingIoExample
.\gradlew.bat runEventLoopExample
```

## 예제 코드

```text
src/main/java/com/mujin/cs
├─ bytes
│  └─ HexAndUtf8Example.java
├─ io
│  ├─ BlockingIoThreadExample.java
│  └─ NonBlockingIoExample.java
├─ eventloop
│  └─ SimpleEventLoopExample.java
└─ network
   ├─ LengthHeaderExample.java
   ├─ SocketConnectionExample.java
   └─ TcpConnectionLifecycleExample.java
```

## 다음에 이어갈 주제

Network 챕터를 마무리한 뒤에는 OS 기본기로 넘어갈 예정입니다.

```text
07. Process와 Thread
08. Context Switching
09. Stack과 Heap
10. Race Condition
```
