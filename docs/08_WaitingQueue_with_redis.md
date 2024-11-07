## 대기열 설계를 rdb에서 redis로 변경

기존 대기열의 상태를 총 4가지를 관리 했지만(STANDBY, PROGRESS, DONE, EXPIRED)
역할에 중복이 있다고 판단하고 두 가지 상태를 갖는 것으로 변경 (WAITING, ACTIVE)
사용이 완료되었거나, 만료된 토큰은 삭제 한다.


### 유저 대기열 토큰 발급
zadd WAITING [System.currentTimeMillis()] user:1
- zadd 명령어로 zset에 UUID를 추가해준다.
  - key: "WAITING"
  - score: 타임스탬프
  - member "user:[userId]"


### 스케줄러로 일정 수만큼 대기열 -> 활성화 토큰으로 변경 (은행창구식)
- currentActiveSize: 활성화 토큰 갯수 조회
- maxActiveSize를 최대 활성화 토큰을 50으로 설정
- (currentActiveSize < maxActiveSize) =>
  zrange 명령어로 적절한 사이즈 만큼 대기열의 userId를 가져온다.
- ZREM 명령어로 가져온 N명의 명단을 대기열 토큰에서 삭제한다
- 활성화 토큰에 가져온 N명의 명단을 key-value로 추가한다
  - key: "ACTIVE_user:[userId]"
  - value: QueueStatus.Progress
    - 사용하지 않는 값
  - 만료시간 10분


### 대기열 정보 조회
- zrank 명령어로 zset에서 user:[userId] 순위를 확인한다.
  - 대기열 토큰에 없을 경우 활성화 토큰에 있는지 확인한다.
    - 활성화 토큰에도 없을 경우 유효하지 않은 토큰이므로 에외를 뱉는다.
    - 활성화 토큰에 있을 경우 0을 반환해 준다.
  - 대기열 토큰에 있을 경우 순위를 반환해 준다.
    - 첫 순위는 0을 반환해주기 때문에 +1을 더해준다


### active한 토큰인지 검사하는 인터셉터
- active 토큰에 없을 경우 예외를 뱉는다


### 토큰 삭제
- 결제 완료 후 del 명령어로 set에서 user:[userId]를 삭제한다.
