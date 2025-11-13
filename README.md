# JFSM
Java Finite State Machine (FSM) Manager

JFSM은 자바 기반의 경량 상태 머신(Finite State Machine) 프레임워크입니다. 간결한 API로 상태 천이(transition)를 정의하고, 콜백과 조건(EventCondition), 지연 실행 및 재시도(Retry) 같은 실무 친화 기능을 제공합니다. 멀티 스레드 기반의 태스크 실행기(StateTaskManager)를 통해 비동기/지연 이벤트 처리도 쉽게 구성할 수 있습니다.

- 최소 의존성, 순수 자바로 동작
- 간단한 DSL 스타일의 상태 천이 등록 API
- 천이 성공/실패 콜백, 다음 이벤트 체이닝(nextEvent)
- 지연(delay) 및 재시도(nextEventRetryCount) 지원
- 조건부 실행(EventCondition) 및 스케줄링 지원
- 멀티 스레드 태스크 처리(StateTaskManager)와 안전한 동시성 제어

## STRUCTURE
![스크린샷 2022-01-28 오전 10 26 24](https://user-images.githubusercontent.com/37236920/151470924-c61a46f8-5ee0-428e-90fa-1ff099a5d13e.png)

## 목차
- 소개
- 빠른 시작
- 핵심 개념
- 사용 방법
- 고급 주제: 지연/재시도, 조건, 스케줄링
- 예제: ATM 데모
- 테스트 실행
- Maven/빌드
- FAQ
- 라이선스

---

## 소개
JFSM은 다음과 같은 상황에 유용합니다.
- 사용자/업무 흐름을 명확히 상태로 모델링하고 싶은 경우
- 이벤트 기반으로 단계별 처리와 에러 복구/재시도를 하고 싶은 경우
- 멀티 스레드에서 다수의 상태 객체를 안전하게 관리하고 싶은 경우

구성 요소는 다음과 같습니다.
- `StateManager`: FSM 전체를 관리하는 상위 매니저. 스레드 풀 크기, 핸들러/유닛 등록/삭제 등 제공
- `StateHandler`: 특정 도메인(유형)의 이벤트와 천이를 관리
- `StateEventManager`: 천이 테이블과 실행 로직을 담당
- `StateUnit`: 실제로 상태를 갖고 이벤트를 적용받는 개체(세션/주문/장비 등)
- `EventCondition`: 천이 실행 전 조건 검사 인터페이스
- `CallBack`: 천이 성공/실패 시점 후처리 콜백 인터페이스
- `RetryManager`: 재시도 정책 및 상태 관리 유틸

UML 개요(예시):
- FSM: `JFSM/src/main/resources/uml/fsm_uml.puml`
- ATM 데모: `JFSM/src/test/java/atm/uml/atm_state.puml`


## 왜 FSM인가 (도입 배경과 이점)
현대 서비스는 사용자 흐름, 주문·결제, 기기 제어(예: ATM) 등 단계적 로직이 많습니다. 이 흐름을 if-else/switch, 타이머, 스레드, 큐 로직으로 즉흥 구현하면 코드가 빠르게 복잡해지고 결합도가 높아집니다. FSM(Finite State Machine)은 시스템의 가능한 상태와 이벤트, 전이를 명확히 정의해 다음과 같은 이점을 제공합니다.

- 복잡도 관리와 가독성 향상
  - 상태와 전이(transition)를 표로 관리하므로 흐름이 명확합니다.
  - "어떤 이벤트가 어떤 상태에서 유효한가"를 코드 레벨에서 강제할 수 있습니다.
- 신뢰성 있는 오류 처리와 회복력
  - 성공/실패 콜백으로 후처리를 일관되게 수행합니다.
  - `nextEvent` + `delay` + `nextEventRetryCount`로 실패 후 재시도·백오프·대기 같은 회복 전략을 선언적으로 구성할 수 있습니다.
- 동시성 제어와 비동기 처리의 단순화
  - `StateTaskManager`가 지연 실행과 체이닝을 스레드 풀에서 안전하게 처리합니다.
  - 이벤트 조건(`EventCondition`)으로 경쟁 조건이나 유효성 선검사를 중앙화합니다.
- 테스트 용이성과 추적 가능성
  - 각 전이와 콜백이 독립 모듈이므로 단위 테스트가 쉽습니다.
  - 상태 이력(Prev/Cur)으로 케이스 재현과 장애 분석이 용이합니다.
- 도메인 정합성과 확장성
  - 도메인 언어(예: READY → CARD_READ → PIN_ENTRY)를 그대로 코드에 반영해 의사소통이 쉬워집니다.
  - 새로운 상태/이벤트 추가가 기존 코드에 최소한의 영향으로 가능합니다.

JFSM이 제공하는 기능은 FSM 도입 효과를 실무에 맞게 강화합니다.
- 선언적 API: `StateHandler.addState(...)`로 전이, 콜백, 다음 이벤트, 지연, 재시도까지 한 번에 정의
- 조건부 실행: `EventCondition`으로 사전 검증 로직을 플러그인처럼 추가
- 비동기·스케줄링: 내부 스케줄러/스레드풀 기반으로 다음 이벤트 자동 처리
- 재시도 전략: `RetryManager`를 통해 단순 반복부터 제한 횟수·지연 간격까지 유연하게 구성

언제 FSM을 쓰지 않아도 되는가?
- 전이가 1~2개 수준의 아주 단순한 플로우
- 상태 개념이 거의 없고 절차형 직선 로직으로 충분한 경우
이런 경우에는 오히려 FSM이 과설계가 될 수 있습니다. 그러나 상태/이벤트가 늘어나고 비동기·지연·재시도가 얽히면 FSM이 장기 유지보수 관점에서 유리합니다.

## 빠른 시작
아래는 최소 구성 예시입니다.

```java
import com.fsm.StateManager;
import com.fsm.module.StateHandler;
import com.fsm.event.base.CallBack;

// 1) FSM 매니저 생성 (태스크 스레드 최대 개수 지정)
StateManager fsm = new StateManager(4);

// 2) 핸들러 등록 (도메인 그룹명)
fsm.addStateHandler("ATM");
StateHandler handler = fsm.getStateHandler("ATM");

// 3) 상태 유닛 등록 (실제 상태를 가진 개체)
fsm.addStateUnit("acct-001", "ATM", "INIT", new Object());

// 4) 천이 정의 (event, from, to, 성공콜백, 실패콜백, 다음이벤트, 지연(ms), 재시도)
CallBack ok = (unit, params) -> {
    System.out.println("on success: " + unit.getName());
    return true;
};
CallBack fail = (unit, params) -> {
    System.out.println("on fail: " + unit.getName());
    return true;
};

handler.addState(
    "INPUT_PIN",          // event
    "INIT",               // from
    "PIN_ENTERED",        // to
    ok,                    // success callback
    fail,                  // fail callback
    "SELECT_MENU",        // next event (체이닝)
    0,                     // delay (ms)
    0                      // retry count
);

// 5) 이벤트 실행
var unit = fsm.getStateUnit("acct-001");
String nextState = handler.fire("INPUT_PIN", unit);
System.out.println("Next State: " + nextState);

// 종료 시
fsm.stop();
```


## 핵심 개념
- StateManager
  - FSM 전체 수명과 리소스를 관리합니다.
  - 핸들러(`StateHandler`)와 유닛(`StateUnit`)을 등록/삭제합니다.
  - 내부에 `StateTaskManager`를 보유하며, 비동기 작업을 실행합니다.
- StateHandler
  - 이벤트 천이 정의를 등록하고, `fire/handle`로 실행합니다.
  - 동일 핸들러 내 이벤트 간 체이닝(nextEvent)과 지연을 처리할 수 있습니다.
- StateEventManager
  - 천이 테이블을 저장하고 실행 로직을 수행합니다.
  - 성공/실패 콜백, 재시도, 조건 검사 등을 오케스트레이션합니다.
- StateUnit
  - 이름, 현재 상태, 부가 데이터(payload)를 갖는 FSM의 대상 객체입니다.
- EventCondition
  - 특정 이벤트 실행 전 조건을 검증합니다.
  - 실패/오류 상황 시 다음 천이나 콜백 동작에 영향을 줄 수 있습니다.
- CallBack
  - 천이 성공/실패 시점에 후처리를 구현합니다.
- Retry(재시도)
  - `nextEventRetryCount`로 다음 이벤트 체이닝 시 재시도 횟수를 지정합니다.


## 사용 방법
### 1) StateManager 생성
```java
StateManager fsm = new StateManager(4); // 스레드 최대 4개
```

### 2) StateHandler 등록 및 조회
```java
fsm.addStateHandler("ORDER");
StateHandler handler = fsm.getStateHandler("ORDER");
```

### 3) StateUnit 등록/삭제/조회
```java
fsm.addStateUnit("order-1001", "ORDER", "NEW", payload);
fsm.getStateUnit("order-1001");
fsm.removeStateUnit("order-1001");
```

### 4) 천이 정의 API
오버로드 두 가지가 제공됩니다.
- 단일 from 상태에서 to로 천이
- 다중 from 상태 집합에서 to로 천이

```java
boolean added = handler.addState(
    "PAY",                 // event
    "NEW",                 // fromState
    "PAID",                // toState
    successCb, failCb,
    null,                   // nextEvent 없으면 null
    0,                      // delay
    0,                      // retry count
    new Object[]{/* next params */}
);
```

```java
HashSet<String> from = new HashSet<>(List.of("NEW", "RETRY"));
boolean added = handler.addState(
    "PAY",                 // event
    from,                   // fromStateSet
    "PAID",                // toState
    successCb, failCb,
    "SHIP",                // nextEvent
    1000,                   // delay 1s 후 nextEvent
    3,                      // 최대 3회 재시도
    "param1", 123
);
```

### 5) 실행
```java
String nextState = handler.fire("PAY", stateUnit);
```


## 고급 주제
### 지연과 체이닝(nextEvent)
- `addState`에 `nextEvent`, `delay`를 지정하면 성공 시 다음 이벤트를 지연 후 자동 실행합니다.
- `nextEventRetryCount`로 실패 시 재시도 횟수를 제어할 수 있습니다.

### 조건(EventCondition)
`EventCondition`을 핸들러에 등록해 이벤트 실행 전 검증을 수행할 수 있습니다.

```java
handler.addEventCondition((event, unit) -> {
    // 실행 가능 여부 검사 (true=통과)
    return unit != null && unit.getIsAlive();
}, 0); // delay(ms) — 필요 시 조건도 지연 평가 가능
```

### 비동기/멀티스레드
- `StateTaskManager`와 내부 스케줄러가 지연/체이닝 작업을 백그라운드에서 실행합니다.
- `StateManager` 생성자의 파라미터로 스레드 최대 개수를 설정합니다.

### 오류 처리와 재시도
- 실패 콜백에서 로깅/알림/보정 로직을 수행할 수 있습니다.
- `nextEventRetryCount`와 `RetryManager`를 통해 반복 실행 전략을 설계하세요.


## 예제: ATM 데모
테스트 코드에 ATM 상태 머신 예제가 포함되어 있습니다.
- 경로: `JFSM/src/test/java/atm`
- UML: `JFSM/src/test/java/atm/uml/atm_state.puml`

실행 팁:
```bash
# Maven 테스트 실행
mvn -f JFSM/pom.xml test -Dtest=atm.BasicAtmStateTest
```


## 테스트 실행
```bash
# 전체 테스트
mvn -f JFSM/pom.xml test

# 특정 테스트 클래스
mvn -f JFSM/pom.xml -Dtest=TestMain test
```


## Maven/빌드
프로젝트는 Maven 기반입니다. 로컬 모듈로 사용할 경우 `StateManager` 등 API를 직접 import 하여 사용하세요. (별도의 중앙 저장소 배포 정보가 없다면 소스 의존 또는 로컬 설치를 이용하세요.)

```bash
# 빌드
mvn -f JFSM/pom.xml clean package

# 로컬 설치 (다른 프로젝트에서 사용)
mvn -f JFSM/pom.xml clean install
```

의존 예시(pom.xml) — 로컬 설치 후 사용 시:
```xml
<dependency>
  <groupId>com.fsm</groupId>
  <artifactId>JFSM</artifactId>
  <version>1.0.0</version>
</dependency>
```


## FAQ
- Q. 상태 천이는 어디서 정의하나요?
  - A. `StateHandler.addState(...)`를 통해 정의합니다.
- Q. 여러 from 상태에서 같은 이벤트로 천이 가능할까요?
  - A. 가능합니다. `HashSet<String>`으로 from 상태 집합을 넘기면 됩니다.
- Q. 다음 이벤트를 자동 실행하고 싶습니다.
  - A. `nextEvent`와 `delay`를 지정하세요. 실패 시 `nextEventRetryCount`로 재시도 횟수를 설정합니다.
- Q. 스레드 개수는 어떻게 조절하나요?
  - A. `new StateManager(threadMaxCount)`로 생성 시 설정합니다.


## 라이선스
이 프로젝트는 `LICENSE` 파일의 내용을 따릅니다.
  
