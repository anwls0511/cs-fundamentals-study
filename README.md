# cs-fundamentals-study

백엔드 개발자가 코드를 리뷰하고 설계를 이해하기 위해 쌓아가는 CS 기본기 학습 기록입니다.

## 오늘의 질문

```text
00 00 00 2A는 왜 42일까?
문자열은 네트워크에서 어떤 바이트로 변할까?
TCP에서 메시지 하나의 끝은 어떻게 알까?
헤더에 body length를 넣는다는 건 무슨 뜻일까?
```

## 오늘 공부한 내용

| 주제 | 핵심 정리 |
| --- | --- |
| bit / byte | 네트워크와 파일은 결국 바이트 배열을 주고받는다. |
| 16진수 | `00 00 00 2A`는 4바이트로 표현한 숫자 `42`다. |
| UTF-8 | 문자열 길이와 바이트 길이는 다를 수 있다. |
| TCP stream | TCP는 메시지 단위가 아니라 바이트 흐름을 보장한다. |
| header/body | body 길이를 헤더에 넣으면 메시지 경계를 안정적으로 알 수 있다. |

## 오늘 작성한 문서

| 날짜 | 문서 | 내용 |
| --- | --- | --- |
| 2026-06-09 | [바이트부터 TCP 메시지 경계까지](docs/2026-06-09-byte-network-basics.md) | 16진수, UTF-8, TCP stream, 길이 헤더 정리 |

## 예제 실행

```powershell
.\gradlew.bat runHexExample
.\gradlew.bat runLengthHeaderExample
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
```

## 예제 코드

```text
src/main/java/com/mujin/cs
├─ bytes
│  └─ HexAndUtf8Example.java
└─ network
   └─ LengthHeaderExample.java
```
