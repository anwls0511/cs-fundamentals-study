# cs-fundamentals-study

백엔드 개발자를 위한 CS 기본기 학습 기록입니다.

목표는 문법이나 프레임워크 사용법을 외우는 것이 아니라, 코드를 리뷰할 때 아래 질문을 할 수 있는 기본기를 쌓는 것입니다.

```text
이 데이터는 실제로 어떤 바이트로 오고 갈까?
TCP에서 메시지 하나의 끝은 어떻게 알까?
서버와 클라이언트는 어떤 프로토콜을 약속했을까?
이 구현은 장애 상황에서도 안전할까?
```

## 오늘 공부한 내용

| 주제 | 정리한 내용 |
| --- | --- | --- |
| bit / byte | 컴퓨터가 데이터를 다루는 가장 작은 단위와 바이트의 의미 |
| 16진수 | `00 00 00 2A` 같은 값을 읽는 방법 |
| UTF-8 | 문자열이 네트워크로 갈 때 바이트 배열로 바뀌는 방식 |
| TCP stream | TCP는 메시지 단위가 아니라 바이트 흐름이라는 점 |
| header/body | body 길이를 헤더에 넣어 메시지 경계를 약속하는 방식 |

## 오늘 작성한 문서

- [2026-06-09: 바이트부터 TCP 메시지 경계까지](docs/2026-06-09-byte-network-basics.md)

## 예제 실행

Windows:

```powershell
.\gradlew.bat runHexExample
.\gradlew.bat runLengthHeaderExample
```

macOS / Linux:

```bash
./gradlew runHexExample
./gradlew runLengthHeaderExample
```

## 예제 구성

```text
src/main/java/com/mujin/cs
├─ bytes
│  └─ HexAndUtf8Example.java
└─ network
   └─ LengthHeaderExample.java
```
