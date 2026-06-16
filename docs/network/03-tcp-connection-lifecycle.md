# 세 번째 학습: TCP 연결 생명주기

> 목표: 서버와 클라이언트가 TCP 연결을 만들고 닫는 흐름을 코드와 함께 이해한다.

## 오늘의 결론

```text
서버는 port를 열고 listen 상태로 기다린다.
클라이언트가 서버 IP와 port로 connect를 시도한다.
서버가 accept하면 통신용 socket이 만들어진다.
데이터를 주고받은 뒤 양쪽 socket을 닫으면 연결이 종료된다.
```

## 한눈에 보기

| 단계 | 의미 |
| --- | --- |
| listen | 서버가 특정 port에서 연결을 기다리는 상태 |
| connect | 클라이언트가 서버 IP와 port로 접속을 시도하는 동작 |
| accept | 서버가 클라이언트 연결을 받아들이는 동작 |
| read/write | 연결된 socket으로 데이터를 읽고 쓰는 동작 |
| close | socket을 닫아 연결을 종료하는 동작 |

## 1. 왜 연결 생명주기를 알아야 할까

이전 학습에서 IP, port, socket을 정리했다. 그런데 socket을 안다고 해서 서버와 클라이언트가 언제 연결되고 언제 끊기는지까지 바로 이해되는 것은 아니다.

백엔드에서 연결 생명주기를 이해하면 다음 질문에 답하기 쉬워진다.

```text
서버는 언제부터 클라이언트를 받을 수 있을까?
accept는 무슨 일을 할까?
클라이언트가 접속하지 않으면 서버 thread는 어디에서 기다릴까?
close를 안 하면 어떤 문제가 생길까?
```

## 2. 서버는 먼저 listen 한다

TCP 서버는 먼저 특정 port를 열고 기다린다.

```java
ServerSocket serverSocket = new ServerSocket(19100);
```

이 상태에서 서버는 아직 클라이언트와 통신하는 socket을 가진 것이 아니다. 클라이언트 연결을 받을 준비를 한 상태에 가깝다.

## 3. 클라이언트는 connect 한다

클라이언트는 서버 IP와 port로 접속한다.

```java
Socket socket = new Socket("127.0.0.1", 19100);
```

이 코드가 성공하면 클라이언트 쪽 socket이 만들어지고, 서버는 연결 요청을 받을 수 있다.

## 4. 서버는 accept 한다

서버는 `accept()`로 클라이언트 연결을 받아들인다.

```java
Socket clientSocket = serverSocket.accept();
```

중요한 점은 `ServerSocket`과 `Socket`의 역할이 다르다는 것이다.

| 객체 | 역할 |
| --- | --- |
| `ServerSocket` | port를 열고 클라이언트 연결을 기다림 |
| `Socket` | 실제 클라이언트와 데이터를 주고받음 |

## 5. 데이터를 주고받고 close 한다

연결이 만들어지면 양쪽은 input stream과 output stream으로 데이터를 주고받는다.

```java
writer.println("HELLO");
String response = reader.readLine();
```

사용이 끝나면 socket을 닫아야 한다.

```java
socket.close();
```

Java에서는 `try-with-resources`를 사용하면 close를 안전하게 처리할 수 있다.

## 6. 예제 실행 흐름

이번 예제는 연결 생명주기를 로그로 확인한다.

```text
server: listen 127.0.0.1:19100
client: connect
server: accept
client: send HELLO
server: read HELLO
server: send WORLD
client: read WORLD
client: close
server: close
```

## 7. 오늘의 정리

TCP 서버는 먼저 특정 port를 열고 listen 상태로 대기한다. 클라이언트가 서버 IP와 port로 connect하면 서버는 accept를 통해 연결을 받아들이고, 그 결과 생성된 socket으로 데이터를 주고받는다. 통신이 끝나면 socket을 닫아 연결을 정리해야 한다.
