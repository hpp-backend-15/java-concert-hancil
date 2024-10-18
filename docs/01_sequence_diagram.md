- **1. 유저 대기열 토큰 기능**

    ```mermaid
    sequenceDiagram
        participant user as 사용자
        participant API as API
        participant queue as 대기열
        user ->> API: 토큰 생성 API 요청
        API ->> queue : 대기열 토큰 생성 요청
        queue ->> queue : 대기열 토큰 생성
        alt queue 토큰이 존재할 경우
            queue -->> API: 기존 대기열 토큰 반환
        else 대기열 토큰이 존재하지 않을 경우
            queue -->> API: 신규 대기열 토큰 반환
        end
        API -->> user : 토큰 반환
        loop 대기번호 조회 API
            user ->> API: 대기번호 조회 API 요청
            Note over user , API: Authorization에 토큰적재
            API ->> queue : 대기번호 확인 요청
            queue ->> queue : 활성화된 토큰 상태 모두 조회
            alt 대기열에서 기다리지 않고 통과할 수 있는 경우
                queue -->> queue : 해당 대기열 토큰의 만료 시간을 현재시간 + 30분으로 업데이트
                queue -->> queue : 해당 대기열 토큰의 진행 상태를 PROGRESS로 업데이트
                queue -->> user : 0번째 대기 순번 반환
            else 제한 인원이 대기열에 꽉차 대기열을 기다려야 할 경우
                queue ->> queue : 대기열 상태값이 WAITING이고 진입시간이 해당 유저보다 작은 토큰의 전체 수 + 1
                queue -->> API: 몇번째 대기 순번인지 반환
                API -->> user : 몇번째 대기 순번인지 반환
            end
        end
    
    ```

- **2. 예약 가능 날짜 조회**

    ```mermaid
    sequenceDiagram
        participant user as 사용자
        participant API as API
        participant date as 날짜
        participant queue as 대기열
        participant scheduler as 스케줄러
    
        user ->> API: 예약 가능한 날짜 조회 API 요청
        Note over user, API: Authorization에 토큰 적재
        API ->> date: 예약 가능한 날짜 조회
        date ->> queue: 대기열 상태값 조회
        queue -->> date: 대기열 상태값 반환
    
        alt 대기열 상태값이 EXPIRED인 경우
            date -->> user: 에러 응답 (대기열 상태값 EXPIRED)
        else
            date -->> API: 예약 가능한 날짜 조회 결과 반환
            API -->> user: 예약 가능한 날짜 조회 결과 반환
        end
    
        rect rgba(0, 0, 255, .1)
            scheduler -->> scheduler: 대기열의 토큰 상태가 PROGRESS인 토큰 중 <br/>만료 시간이 30분 지난 경우 EXPIRED로 업데이트
        end
    
    ```

- **3. 좌석 조회API**

    ```mermaid
    sequenceDiagram
        participant user as 사용자
        participant API as API
        participant seat as 좌석
        participant queue as 대기열
        participant scheduler as 대기열 토큰 만료 스케줄러
    
        user ->> API: 특정 날짜의 예약 가능한 좌석 조회 API 요청
        Note over user, API: Authorization에 대기열 토큰 적재
        API ->> seat: 특정 날짜의 예약 가능한 좌석 조회
        seat ->> queue: 대기열 상태값 조회
        queue -->> seat: 대기열 상태값 반환
    
        alt 대기열 상태값이 EXPIRED인 경우
            seat -->> user: 에러 응답 (대기열 상태 EXPIRED)
        else
            seat -->> API: 특정 날짜의 예약 가능한 좌석 조회 결과 반환
            API -->> user: 특정 날짜의 예약 가능한 좌석 조회 결과 반환
        end
    
        rect rgba(0, 0, 255, .1)
            scheduler ->> scheduler: 토큰 상태가 PROGRESS인 경우, <br/>만료 시간이 30분 지난 토큰을 EXPIRED로 업데이트
        end
    ```

- **4. 좌석 예약API**

    ```mermaid
    sequenceDiagram
        participant user as 사용자
        participant API as API
        participant seat as 좌석
        participant queue as 대기열
        participant scheduler as 스케줄러
    
        user ->> API: 날짜와 좌석 정보 입력하여 좌석 예약 API 요청
        Note over user, API: Authorization에 토큰 적재
        API ->> seat: 좌석 예약 요청
        seat ->> queue: 대기열 상태값 요청
        queue -->> seat: 대기열 상태값 반환
    
        alt 대기열 상태값이 EXPIRED인 경우
            seat -->> user: 에러 응답 (대기열 상태 EXPIRED)
        else
            seat ->> seat: 특정 날짜에 좌석 임시 예약 요청
            alt 좌석이 찬 경우
                seat -->> user: 에러 응답 (좌석이 찼음)
            else
                seat -->> API: 좌석 임시 예약 성공 응답
                API -->> user: 좌석 임시 예약 성공 응답
            end
        end
    
        rect rgba(0, 0, 255, .1)
            scheduler ->> scheduler: 임시 예약된 좌석 중 <br/>결제가 5분 내 완료되지 않은 좌석은 <br/>임시 배정 해제 및 데이터 삭제
        end
    
        rect rgba(0, 0, 255, .1)
            scheduler ->> scheduler: 대기열의 토큰 상태가 PROGRESS인 경우 <br/>30분이 지난 토큰은 EXPIRED로 업데이트
        end
    ```

- **5. 잔액 충전 API**

    ```mermaid
    sequenceDiagram
        participant user as 사용자
        participant API as API
        participant cash as 캐시
        user ->> API: 잔액 충전 API 요청
        API ->> cash: 잔액 충전 요청
        cash->> cash: 충전 금액 0이상 검사
        alt
            cash-->> user : 충전 금액이 0 이하일 경우 에러 반환
        else
            cash-->> API: 충전 성공 응답
            API -->> user : 충전 성공 응답
        end
    
    ```

- **6. 잔액 조회 API**

    ```mermaid
    sequenceDiagram
        participant user as 사용자
        participant API as API
        participant cash as 캐시
        user ->> API: 잔액 조회 API 요청
        API ->> cash: 잔액 조회 요청
        cash -->> API: 잔액 조회 결과 반환
        API -->> user: 잔액 조회 결과 반환
    
    ```

- **7. 결제 API**

    ```mermaid
    sequenceDiagram
        participant user as 사용자
        participant API as API
        participant payment as 결제
        participant queue as 대기열
        participant cash as 캐시
        participant concert as 콘서트
        participant scheduler as 스케줄러
    
        user ->> API: 결제 요청
        API ->> payment: 결제 요청
        payment ->> queue: 대기열 상태값 조회
        queue -->> payment: 대기열 상태값 반환
    
        alt 대기열 상태값이 EXPIRED일 경우
            payment -->> user: 에러 응답 (대기열 상태 EXPIRED)
        else
            payment ->> cash: 결제 금액과 잔액 비교 요청
            cash ->> cash: 결제 금액과 잔액 비교
            alt 잔액 부족시
                cash -->> user: 에러 응답 (잔액 부족)
            else
                cash ->> cash: 잔액에서 결제 금액 차감
                cash -->> payment: 잔액에서 결제 금액 차감 성공 응답
                payment ->> concert: 좌석을 임시 배정에서 배정 상태로 변경
    
                opt 전체 좌석이 마감된 경우
                    concert ->> concert: 좌석 전체 마감 여부를 마감으로 변경 
                end
    
                payment ->> queue: 대기열 상태 값을 DONE으로 업데이트
                payment -->> payment: 결제내역 영수증 생성
                payment -->> API: 결제내역 영수증 반환
                API -->> user: 결제내역 영수증 반환
            end
        end
    
        rect rgba(0, 0, 255, .1)
            scheduler --> scheduler: 대기열의 토큰 상태가 PROGRESS인 토큰 중 <br/>만료 시간값이 30분이 지났을 경우 EXPIRED로 업데이트
        end
    ```