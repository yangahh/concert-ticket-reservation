### 대기열 토큰 발급 API
```mermaid
sequenceDiagram
    Actor user as 서비스 사용자
    participant fe as FE: 콘서트 예약 페이지 진입
    participant token as Module: 토큰 모듈
    participant db as DB
    
    user ->> fe: 대기열 토큰 요청 (POST /queue/token)
    fe ->> token: 토큰 생성 요청
    token ->> db: 토큰 데이터 저장<br>(user_id, created_at, expires_at)
    db -->> token: 저장 완료
    token -->> fe: 토큰 발급 완료 (token_id)
    fe -->> user: 토큰 반환 (token_id)
```


### 대기 순번 조회 API
```mermaid
sequenceDiagram
    Actor user as 서비스 사용자
    participant fe as FE: 대기순번 확인
    participant token as Module: 토큰 모듈
    participant db as DB
    
    loop 매 5초마다
        fe ->> token: 대기 순번 조회 요청<br>(GET /queue/position)
        token ->> db: 대기열에서 토큰 조회 (created_at 기준)
        db -->> token: 토큰 정보 반환
        token -->> fe: 대기 순번 계산 및 active 여부 반환 (position)
        fe -->> user: 현재 대기 순번, active 여부 표시
    end
```


### active 토큰 변환 스케줄러 (1분마다 실행)
```mermaid
sequenceDiagram
    participant sch as Scheduler: active 토큰 발급
    participant token as Module: 토큰 모듈
    participant db as DB

    sch ->> token: 1분마다 waiting 토큰을 active 토큰으로<br> 전환하는 스케줄러가 실행
    token ->>+ db: active 토큰 개수 조회
    db -->>- token: active 토큰 개수 반환
    token ->>+ token: n := active 빈 자리 계산 (30 - active 개수)
    
    opt 빈 자리가 있는 경우 (n > 0)
      token ->>+ db: 대기 순서가 오래된 순으로 waiting 토큰 n개를 조회
      db -->>- token: 조건에 맞는 waiting 토큰 목록 반환
      token ->>+ db: waiting -> active로 업데이트
      db -->>- token: 업데이트 완료
    end
    token -->> sch: 종료
```

### 만료된 토큰을 처리하는 스케줄러 (5분마다 실행)
```mermaid
sequenceDiagram
    participant sch as Scheduler: 만료된 토큰 처리
    participant token as Module: 토큰 모듈
    participant db as DB

    sch ->>+ token: 5분마다 만료된 토큰을<br> 삭제 처리하는 스케줄러가 실행
    token ->>+ db: 대기열에서 만료된 토큰 조회
    db -->>- token: 만료된 토큰 목록 반환
    token ->>+ db: 만료된 토큰 삭제
    db -->>- token: 삭제 완료
    token -->>- sch: 종료
```


### 예약 가능한 날짜 목록 조회 API
```mermaid
sequenceDiagram
    Actor user as 서비스 사용자
    participant fe as FE:예약 날짜 선택 페이지
    participant token as Interceptor: 토큰 검증 모듈
    participant cc as Module: 콘서트 모듈
    participant db as DB
    
    user ->>+ fe: 예약 가능 날짜 조회 요청<br>(GET /concerts/{id}/dates)
    fe ->>+ cc: API 호출<br>(Authorization: Active Token)
    cc ->>+ token: 토큰 검증 요청 (tokenId)
    token ->>+ db: 토큰 유효성 조회
    db -->>- token: 유효한 토큰 여부 반환
    token -->>- cc: 토큰 검증 결과 반환
    note over token, cc: 토큰이 유효하다고 가정
    cc ->>+ db: 예약 가능한 날짜 조회 (concertId)
    db -->>- cc: 예약 가능한 날짜 목록 반환
    cc -->>- fe: 예약 가능 날짜 반환
    fe -->>- user: 예약 날짜 목록 표시
```

### 예약 가능한 날짜별 좌석 목록 조회 API
```mermaid
sequenceDiagram
    Actor user as 서비스 사용자
    participant fe as FE: 예약할 좌석 선택 페이지
    participant token as Interceptor: 토큰 검증 모듈
    participant cc as Module: 콘서트 모듈
    participant db as DB
    
    user ->>+ fe: 좌석 조회 요청<br>(GET /concerts/{id}/dates/{date}/seats)
    fe ->>+ cc: API 호출 (Authorization: Active Token)
    cc ->>+ token: 토큰 검증 요청 (token)
    token ->>+ db: 토큰 유효성 조회
    db -->>- token: 유효한 토큰 여부 반환
    token -->>- cc: 토큰 검증 결과 반환
    note over token, cc: 토큰이 유효하다고 가정
    cc ->>+ db: 좌석 조회 요청 (concertId, date)
    db -->>- cc: 좌석 목록 반환
    cc -->>- fe: 좌석 정보 반환
    fe -->>- user: 좌석 목록 표시
```


### 예약 요청 API
```mermaid
sequenceDiagram
    Actor user as 서비스 사용자
    participant fe as FE: 좌석 예약 페이지
    participant token as Interceptor: 토큰 검증 모듈
    participant cc as Module: 콘서트 모듈
    participant db as DB
    
    user ->>+ fe: 좌석 예약 요청<br>(POST /reservations)
    fe ->>+ cc: 좌석 예약 API 호출
    
    cc ->>+ token: 토큰 검증 요청 (token)
    token ->>+ db: 토큰 유효성 조회
    db -->>- token: 유효한 토큰 여부 반환
    token -->>- cc: 토큰 검증 결과 반환
    note over token, cc: 토큰이 유효하다고 가정
    cc ->>+ db: 좌석 상태 조회 (seat_id)
    db -->>- cc: 좌석 상태 반환
    
    alt 좌석이 예약 가능 상태인 경우
        cc ->>+ db: 좌석 상태 업데이트 (is_avaliable=false)
        db -->>- cc: 업데이트 완료
        cc -->>- fe: 예약 성공
        fe -->>- user: 예약 완료 메시지
    else 좌석이 이미 예약된 경우
        cc -->> fe: 예약 실패<br>(400 Seat Unavailable)
        fe -->> user: 예약 실패 메시지
    end
```

### 임시 예약이 만료된 것을 처리하는 스케줄러 (1분마다 실행)
```mermaid
sequenceDiagram
    participant sch as Scheduler: 임시예약 만료 처리
    participant re as Module: 예약 모듈
    participant db as DB

    sch ->> re: 1분마다 5분 이내에 결제되지 않은 예약을<br> 취소 처리하는 스케줄러 실행
    re ->>+ db: 만료된 임시 예약 조회 (현재 시간 기준)
    db -->>- re: 만료된 예약 목록 반환

    alt 만료된 예약 있음
        re ->>+ db: 만료된 예약 상태 업데이트(CANCELED),<br>좌석 상태 업데이트(is_avaliable=true)
        db -->>- re: 업데이트 완료
    else 만료된 예약 없음
        re ->> sch: 만료된 예약 없음 응답
    end
    
    sch -->> sch: 일정 시간 후 다시 실행
```


### 결제 요청 API
```mermaid
sequenceDiagram
    Actor user as 서비스 사용자
    participant fe as FE: 결제 페이지
    participant it_token as Interceptor: 토큰 검증 모듈
    participant pay as Module: 결제 모듈
    participant re as Module: 예약 모듈
    participant point as Module: 포인트 모듈
    participant token as Module: 토큰 모듈
    participant db as DB
    
    user ->>+ fe: 결제 요청 (POST /payments)
    fe ->>+ pay: 결제 요청 API 호출

    pay ->>+ it_token: 토큰 검증 요청 (token)
    it_token ->>+ db: 토큰 유효성 조회
    db -->>- it_token: 유효한 토큰 여부 반환
    it_token -->>- pay: 토큰 검증 결과 반환
    note over it_token, pay: 토큰이 유효하다고 가정

    pay ->>+ re: 임시 배정 시간 초과 여부 조회 (reservation_id)
    re ->>+ db: 임시 배정 만료 시간 확인
    db -->>- re: 임시 배정 만료 시간 반환
    re -->>- pay: 임시 배정 시간 초과 여부 반환
    
    alt 임시 배정 시간이 초과되지 않은 경우
        pay ->>+ point: 포인트 잔액 조회 (user_id)
        point ->>+ db: 포인트 조회
        db -->>- point: 잔액 반환
        
        alt 포인트가 충분한 경우
            point ->>+ db: 포인트 차감
            db -->>- point: 차감 완료
            point -->>- pay: 결제 완료 반환
            pay ->>+ re: 좌석/예약 상태 업데이트 요청
            re ->>+ db: 좌석 상태(is_avaliable=false),<br>예약 상태(CONFIRMED) 업데이트 
            db -->>- re: 좌석/예약 상태 업데이트 완료
            re ->>- pay: 좌석/예약 상태 업데이트 결과 반환
            pay ->>+ token: Active 토큰 만료 요청
            token ->> db: 토큰 만료 처리
            db -->> token: 만료 완료
            token -->>- pay: 토큰 만료 처리 완료
            pay -->>- fe: 결제 성공
            fe -->>- user: 결제 완료 메시지
        else 포인트가 부족한 경우
            point ->> pay: 잔액 부족 반환
            pay -->> fe: 결제 실패 (400 Not Enough Points)
            fe -->> user: "포인트 잔액 부족"
        end
    else 임시 배정 시간이 초과된 경우
        pay ->> re: 임시 배정 해제 요청
        re ->> db: 좌석 상태(is_avaliable=true),<br>예약 상태(CANCELED) 업데이트
        db -->> re: 좌석/예약 상태 업데이트 완료
        re -->> pay: 결제 실패 (400 Hold Expired)
        pay -->> fe: 결제 실패 메시지
        fe -->> user: "결제 실패 - 임시 배정 시간이 초과되었습니다."
    end
```


### 잔액 충전 API
```mermaid
sequenceDiagram
    Actor user as 서비스 사용자
    participant fe as FE: 포인트 충전 페이지
    participant point as Module: 포인트 모듈
    participant db as DB
    
    user ->> fe: 포인트 충전 요청 (POST /points)
    fe ->> point: API 호출 (amount)
    point ->> db: 포인트 잔액 업데이트
    db -->> point: 업데이트 완료 및 잔액 반환
    point ->> fe: 충전 완료
    fe -->> user: 충전 성공 메시지
```


### 잔액 조회 API
```mermaid
sequenceDiagram
    Actor user as 서비스 사용자
    participant fe as FE: 포인트 조회 페이지
    participant point as Module: 포인트 모듈
    participant db as DB
    
    user ->> fe: 포인트 잔액 조회 요청 (GET /points)
    fe ->> point: API 호출 (user_id)
    point ->> db: 포인트 잔액 조회 
    db -->> point: 잔액 반환
    point -->> fe: 잔액 반환
    fe -->> user: 포인트 잔액 표시
```