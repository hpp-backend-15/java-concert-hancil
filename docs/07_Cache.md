### STEP13  요구사항
조회가 오래 걸리는 쿼리에 대한 캐싱, 
혹은 Redis 를 이용한 로직 이관을 통해 
성능 개선할 수 있는 로직을 분석하고 
이를 합리적인 이유와 함께 정리한 문서 제출

### 캐시를 사용하기 적절한 데이터인지 판단하는 기준
- 데이터가 변경에 민감한가?
- 데이터의 연산에 드는 비용이 비싼지?
- 데이터의 변경이 전파가 되는지?
- => “잘 바뀌지 않으면서 접근할 일이 많은 데이터,
- 변경되더라도 다른 서비스에 큰 영향을 미치지 않는 데이터” 가 캐시에 저장하여 활용하기 적절하다.


---

### 애플리케이션 조회 API
- 대기열 확인 API
- 콘서트 예약 가능한 날짜 조회 API
- 해당 날짜의 좌석 조회 API
- 잔액 조회 API


### 캐싱 적용 전 API 분석



- 대기열 확인 API
  - 데이터 변경에 민감 한 편,
  - 연산에 드는 비용이 비싸진 않지만 폴링을 고려한다면 무수한 요청으로 인해 db에 부하가 생길 가능성이 높음
  - 레디스의 자료구조로 구현할 예정
- 해당 날짜의 좌석 조회 API
  - 좌석 상태는 좌석 예약이 몰릴 경우 좌석의 상태값이 자주 변해 캐시 데이터도 자주 변경되어야 함.
```
Hibernate: 
    select
        cs1_0.id,
        c1_0.id,
        c1_0.description,
        c1_0.name,
        cs1_0.concert_at,
        cs1_0.reservation_available_at,
        s1_0.schedule_id,
        s1_0.id,
        s1_0.seat_number,
        s1_0.seat_price,
        s1_0.status 
    from
        concert_schedule cs1_0 
    left join
        concert c1_0 
            on c1_0.id=cs1_0.concert_id 
    left join
        seat s1_0 
            on cs1_0.id=s1_0.schedule_id 
    where
        cs1_0.id=?
```
- 잔액 조회 API
  - 사용자의 개인정보이고, 잔액 의 경우 정합성이 중요하기 때문에 캐싱으로 적합하지 않다고 판단 됨
- 예약 가능한 날짜 조회 
  - 데이터 변경에 민감하지 않다
  - 데이터의 연산에 드는 비용이 비싸지 않다.
  - 조회 API중 캐싱을 사용하기에 가장 적합하다.
```
Hibernate: 
    select
        c1_0.id,
        c1_0.description,
        c1_0.name,
        s1_0.concert_id,
        s1_0.id,
        s1_0.concert_at,
        s1_0.reservation_available_at 
    from
        concert c1_0 
    left join
        concert_schedule s1_0 
            on c1_0.id=s1_0.concert_id 
    where
        c1_0.id=?

```



### 캐싱 적용 선택 API
- 예약 가능한 날짜 조회

### 캐싱 전략
- 캐시 데이터를 먼저 찾고 없으면 db에서 조회한 뒤 결과를 반환해 주고 레디스에 쿼리 결과를 새롭게 캐싱한다
- 캐시 데이터의 보관 주기는 24시간 으로 한다.
  