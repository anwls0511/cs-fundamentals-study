# 두 번째 학습: IP, port, socket 연결 흐름

> 목표: 서버와 클라이언트가 실제로 어떤 주소로 만나고, socket이 어떤 역할을 하는지 이해한다.

## 오늘의 결론

```text
IP는 컴퓨터를 찾기 위한 주소다.
port는 그 컴퓨터 안에서 특정 프로그램을 찾기 위한 번호다.
socket은 애플리케이션이 네트워크 통신을 하기 위해 사용하는 연결 통로다.
서버는 port를 열고 기다리고, 클라이언트는 IP와 port로 접속한다.
```

## 한눈에 보기

| 개념 | 오늘 이해한 내용 |
| --- | --- |
| IP | 네트워크에서 컴퓨터를 찾는 주소 |
| port | 한 컴퓨터 안에서 특정 서버 프로그램을 찾는 번호 |
| server socket | 특정 port를 열고 클라이언트 접속을 기다리는 소켓 |
| client socket | 서버 IP와 port로 접속하는 소켓 |
| connection | 클라이언트 socket과 서버 socket 사이에 만들어진 통신 경로 |

## 1. 오늘 이걸 공부한 이유

TCP 메시지 경계를 공부하다 보면 자연스럽게 이런 질문이 생긴다.

```text
클라이언트는 서버를 어떻게 찾아갈까?
서버는 어디에서 기다리고 있을까?
port는 왜 필요할까?
socket은 connection과 같은 말일까?
```

이 질문을 이해해야 Netty, Spring 서버, Redis 연결, DB 연결도 더 자연스럽게 이해할 수 있다.

## 2. IP는 컴퓨터를 찾는 주소다

IP는 네트워크에서 컴퓨터를 찾기 위한 주소다.

예를 들어 내 컴퓨터 자신을 가리키는 대표적인 주소는 `127.0.0.1`이다.

```text
127.0.0.1 = localhost = 내 컴퓨터 자신
```

개발할 때 서버를 내 컴퓨터에서 띄우고 같은 컴퓨터에서 클라이언트로 접속하면 보통 `localhost`나 `127.0.0.1`을 사용한다.

## 3. port는 프로그램을 찾는 번호다

IP만 있으면 컴퓨터는 찾을 수 있지만, 그 컴퓨터 안의 어떤 프로그램으로 갈지는 알 수 없다.

그래서 port가 필요하다.

```text
127.0.0.1:8080 -> 내 컴퓨터의 8080번 port에서 기다리는 프로그램
127.0.0.1:6379 -> 내 컴퓨터의 6379번 port에서 기다리는 Redis
127.0.0.1:19090 -> 이번 예제 서버
```

같은 컴퓨터 안에서도 여러 서버 프로그램이 동시에 떠 있을 수 있다. port는 그중 어떤 프로그램으로 연결할지 구분하는 번호다.

## 4. socket은 네트워크 통신을 위한 통로다

socket은 애플리케이션이 네트워크를 사용하기 위해 OS로부터 얻는 통신 도구라고 볼 수 있다.

서버는 먼저 특정 port를 열고 기다린다.

```java
ServerSocket serverSocket = new ServerSocket(19090);
```

클라이언트는 서버의 IP와 port로 접속한다.

```java
Socket socket = new Socket("127.0.0.1", 19090);
```

서버가 클라이언트 접속을 받으면 통신용 socket이 만들어진다.

```java
Socket clientSocket = serverSocket.accept();
```

이후 서버와 클라이언트는 각자의 socket에서 input stream과 output stream을 사용해 데이터를 주고받는다.

## 5. 이번 예제의 흐름

이번 예제는 하나의 Java 프로그램 안에서 서버와 클라이언트를 같이 실행한다.

```text
1. 서버가 127.0.0.1:19090에서 기다린다.
2. 클라이언트가 127.0.0.1:19090으로 접속한다.
3. 클라이언트가 PING을 보낸다.
4. 서버가 PING을 읽는다.
5. 서버가 PONG을 응답한다.
6. 클라이언트가 PONG을 읽는다.
```

실행 결과는 다음과 같다.

```text
server listening: 127.0.0.1:19090
client connected to server
server accepted connection
server received: PING
client received: PONG
```

## 6. 오늘의 정리

IP와 port에 대해서는 이렇게 정리했다.

IP는 네트워크에서 컴퓨터를 찾기 위한 주소이고, port는 그 컴퓨터 안에서 특정 서버 프로그램을 찾기 위한 번호다. 예를 들어 `127.0.0.1:8080`은 내 컴퓨터의 8080번 port에서 기다리는 서버를 의미한다.

socket에 대해서는 이렇게 정리했다.

socket은 애플리케이션이 네트워크 통신을 하기 위해 사용하는 통신 endpoint다. 서버는 `ServerSocket`으로 특정 port를 열고 기다리고, 클라이언트는 서버 IP와 port로 접속해서 데이터를 주고받는다.

TCP 연결에 대해서는 이렇게 정리했다.

TCP 서버는 port를 열고 대기하고, 클라이언트가 해당 IP와 port로 접속하면 연결이 만들어진다. 연결 이후에는 양쪽 socket의 input stream과 output stream을 통해 바이트 데이터를 주고받는다.
