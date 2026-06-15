# 일곱 번째 학습: Process와 Thread

> 목표: process와 thread가 무엇인지, 백엔드 서버 코드에서 왜 중요한지 정리한다.

## 오늘의 결론

```text
process는 실행 중인 프로그램의 독립된 실행 단위다.
thread는 process 안에서 실제로 코드를 실행하는 흐름이다.
하나의 process 안에는 여러 thread가 있을 수 있고, 같은 process의 thread들은 메모리 일부를 공유한다.
```

## 한눈에 보기

| 개념 | 의미 |
| --- | --- |
| program | 디스크에 저장된 실행 파일 또는 코드 |
| process | 실행 중인 program |
| thread | process 안에서 코드를 실행하는 흐름 |
| memory isolation | process끼리는 기본적으로 메모리가 분리되는 성질 |
| shared memory | 같은 process의 thread들이 공유할 수 있는 메모리 영역 |

## 1. Program과 Process는 다르다

처음에는 program과 process를 같은 말처럼 생각하기 쉽다.  
하지만 정확히는 다르다.

```text
program: 아직 실행되지 않은 코드
process: 실행되어 OS가 관리하는 상태가 된 program
```

예를 들어 Java 애플리케이션을 실행하면 JVM process가 만들어진다.

```text
java -jar app.jar
  -> OS 입장에서는 하나의 process
```

process는 실행에 필요한 자원을 가진다.

```text
process
  -> memory
  -> file descriptor
  -> socket
  -> thread
```

## 2. Thread는 process 안의 실행 흐름이다

thread는 process 안에서 실제로 코드를 실행하는 단위다.

```text
process
  -> thread 1
  -> thread 2
  -> thread 3
```

백엔드 서버에서는 여러 요청을 동시에 처리하기 위해 thread를 사용한다.

```text
request A -> thread 1
request B -> thread 2
request C -> thread 3
```

물론 이 구조가 항상 좋은 것은 아니다. thread가 많아지면 메모리 사용량과 context switching 비용도 늘어난다.

## 3. Process는 분리되고, Thread는 공유한다

process끼리는 기본적으로 메모리가 분리된다.

```text
process A memory
process B memory
```

process A가 process B의 메모리를 마음대로 건드릴 수 없다.  
이 분리 덕분에 한 process가 죽어도 다른 process가 바로 같이 망가지지는 않는다.

반면 같은 process 안의 thread들은 일부 메모리를 공유한다.

```text
process memory
  -> thread 1
  -> thread 2
```

이 덕분에 thread끼리 같은 객체를 볼 수 있지만, 동시에 같은 값을 바꾸면 문제가 생길 수 있다.

```text
thread 1: count 증가
thread 2: count 증가
```

이런 문제가 나중에 race condition으로 이어진다.

## 4. 백엔드 개발에서 중요한 이유

Spring Boot 서버든 Netty 서버든 결국 OS 위에서 process로 실행된다.

그리고 요청 처리는 thread와 연결된다.

```text
client request
  -> server process
  -> worker thread
  -> handler or controller
```

그래서 아래 질문을 설명하려면 process와 thread 개념이 필요하다.

```text
왜 요청마다 thread를 만들면 위험할까?
왜 blocking 작업이 thread를 붙잡는다고 말할까?
왜 Netty는 EventLoop thread를 막지 말라고 할까?
```

## 5. 면접에서 설명할 수 있는 문장

> process는 실행 중인 프로그램이고, thread는 process 안에서 실제 코드를 실행하는 흐름입니다. process끼리는 메모리가 분리되지만, 같은 process의 thread들은 메모리 일부를 공유합니다. 그래서 thread를 사용하면 같은 데이터를 쉽게 공유할 수 있지만, 동시에 접근할 때 race condition 같은 동시성 문제가 생길 수 있습니다.
