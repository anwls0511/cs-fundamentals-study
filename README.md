# cs-fundamentals-study

백엔드 개발자가 코드를 리뷰하고 설계를 이해하기 위해 쌓아가는 CS 기본기 학습 기록입니다.

## 학습 질문

```text
00 00 00 2A는 왜 42일까?
문자열은 네트워크에서 어떤 바이트로 변할까?
TCP에서 메시지 하나의 끝은 어떻게 알까?
헤더에 body length를 넣는다는 건 무슨 뜻일까?
IP와 port는 각각 무슨 역할일까?
socket은 정확히 무엇일까?
```

## 학습 기록

| 구분 | 문서 | 내용 |
| --- | --- | --- |
| 첫 번째 | [바이트부터 TCP 메시지 경계까지](docs/2026-06-09-byte-network-basics.md) | 16진수, UTF-8, TCP stream, 길이 헤더 |
| 두 번째 | [IP, port, socket 연결 흐름](docs/02-ip-port-socket.md) | IP, port, socket, client/server, 요청/응답 |

## 예제 실행

```powershell
.\gradlew.bat runHexExample
.\gradlew.bat runLengthHeaderExample
.\gradlew.bat runSocketExample
```

## 실행 결과 예시

```text
int value: 42
4-byte hex: 00 00 00 2A

text: 가
char length: 1
byte length: 3
utf-8 hex: EA B0 80

header body length: 42
decoded body: {"deviceId":"device-1","temperature":25.1}

server listening: 127.0.0.1:19090
client connected to server
server received: PING
client received: PONG
```

## 예제 코드

```text
src/main/java/com/mujin/cs
├─ bytes
│  └─ HexAndUtf8Example.java
└─ network
   ├─ LengthHeaderExample.java
   └─ SocketConnectionExample.java
```
