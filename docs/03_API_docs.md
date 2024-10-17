### 1. 대기열 토큰 발급 API

대기열에서 사용할 토큰을 발급받습니다.

- Request
    - **URL**: `/v1/queue/issue-token`
    - **HTTP Method**: `POST`
    - **Headers**
        - `Content-Type` : application/json
    - **Body**

        ```json
        {
            "userId": 1
        }
        ```

      ### Code snippet

        ```markdown
        curl --location 'http://localhost:8080/v1/queue/issue-token' \
        --header 'Content-Type: application/json' \
        --data '{
            "userId": 1
        }'
        ```

- Response

  ### Success Response:

    ```json
    {
      "tokenId": 1,
      "createdAt": "2024-10-01T10:00:00",
      "expiredAt": "2024-10-01T10:10:00"
    }
    ```

  ### Error Response:

    ```json
    {
        "code": 404,
        "message": "user not found"
    }
    ```


### 2. 대기열 확인 API

대기열 정보를 조회합니다.

- Request
    - **URL**: `/v1/queue/status`
    - **HTTP Method**: `GET`
    - **Headers**
        - `Authorization` : Bearer QUEUE_TOKEN (대기열 토큰)
        - `Content-Type` : application/json
    - **Body**

        ```json
        {
            "userId": 1
        }
        ```

      ### Code snippet

        ```bash
        curl --location --request GET 'http://localhost:8080/v1/queue/get-token' \
        --header 'Content-Type: application/json' \
        --header 'Authorization: ••••••' \
        --data '{
            "userId": 1
        }'
        ```

- Response

  ### Success Response:

    ```json
    {
        "totalInQueue": 10,
        "currentPosition": 1,
        "expiration": "2024-10-10T10:00:00",
        "status": "WAITING"
    }
    ```

  ### Error Response:

    ```json
    {
        "code": 404,
        "message": "user not found"
    }
    ```


### 3. 콘서트 예약 가능한 날짜 조회 API

콘서트의 예약 가능한 날짜를 조회합니다.

- Request
    - **URL**: `/v1/concerts/{concertId}/schedules`
    - **HTTP Method**: `GET`
    - **Path Parameters**
        - `concertId` : Long (조회할 콘서트 고유 ID)
    - **Headers**
        - `Authorization` : Bearer QUEUE_TOKEN (대기열 토큰)
        - `Content-Type` : application/json
    - **Code snippet**

        ```bash
        curl --location 'http://localhost:8080/v1/concerts/123/schedules' \
        --header 'Authorization: Bearer ••••••' \
        --data ''
        ```

- Response

  ### Success Response:

    ```json
    {
        "concertId": 1,
        "schedules": [
            {
                "scheduleId": 1,
                "concertAt": "2024-12-24T19:00:00",
                "reservationAt": "2024-11-01T10:00:00"
            },
            {
                "scheduleId": 2,
                "concertAt": "2024-12-25T19:00:00",
                "reservationAt": "2024-11-01T10:00:00"
            }
        ]
    }
    ```

  ### Error Response:

    ```json
    {
        "code": 401,
        "message": "invalid token"
    }
    ```


### 4. 해당 날짜의 좌석 조회 API

주어진 콘서트와 날짜의 예약 가능 좌석 정보를 조회합니다.

- Request
    - **URL**: `/v1/concerts/{concertId}/schedules/{scheduleId}/seats`
    - **HTTPMethod**: `GET`
    - **Path Parameters**
        - `concertId` : Long (콘서트 고유 ID)
        - `scheduleId`: Long (콘서트 일정 ID)
    - **Headers**
        - `Authorization` : Bearer QUEUE_TOKEN (대기열 토큰)
        - `Content-Type` : application/json

      ### Code snippet

        ```bash
        curl --location 'http://localhost:8080/v1/concerts/123/schedules/1/seats' \
        --header 'Authorization: ••••••' \
        --data ''
        ```

- Response

  ### Success Response:

    ```json
    {
        "concertId": 123,
        "scheduleId": 1,
        "seats": [
            {
                "seatId": 1,
                "seatNumber": 20,
                "seatStatus": "AVAILABLE",
                "seatPrice": 10000
            },
            {
                "seatId": 2,
                "seatNumber": 21,
                "seatStatus": "OCCUPIED",
                "seatPrice": 10000
            },
            {
                "seatId": 3,
                "seatNumber": 22,
                "seatStatus": "CONFIRMED",
                "seatPrice": 15000
            }
        ]
    }
    ```

  ### Error Response:

    ```json
    {
        "code": 401,
        "message": "invalid token"
    }
    ```


### 5. 좌석 예약 요청 API

좌석 예약과 동시에 좌석을 해당 유저에게 임시 배정합니다.

배정 시간 내 결제가 완료되지 않으면 임시 배정을 해제합니다.

- Request
    - **URL**: `/v1/reservations`
    - **HTTP Method**: `POST`
    - **Headers**
        - `Authorization` : Bearer QUEUE_TOKEN (대기열 토큰)
        - `Content-Type` : application/json
    - **Body**

    ```json
    {
        "userId": 1,
        "concertId": 123,
        "scheduleId": 1,
        "seatIds": [
            1,
            4
        ]
    }
    ```

  ### Code snippet

    ```bash
    curl --location --request GET 'http://localhost:8080/v1/reservations' \
    --header 'Content-Type: application/json' \
    --header 'Authorization: ••••••' \
    --data '{
        "userId": 1,
        "concertId": 123,
        "scheduleId": 1,
        "seatIds": [
            1,
            4
        ]
    }'
    ```

- Response

  ### Success Response:

    ```json
    {
        "reservationId": 1,
        "concertId": 123,
        "scheduleId": 1,
        "seats": [
            {
                "seatNumber": 10,
                "price": 10000
            },
            {
                "seatNumber": 11,
                "price": 15000
            }
        ],
        "totalPrice": 25000,
        "status": "PENDING"
    }
    ```

  ### Error Response:

    ```json
    {
        "code": 401,
        "message": "invalid token"
    }
    ```


### 6. 잔액 충전 API

사용자의 잔액을 충전합니다.

- Request
    - **URL**: `/v1/balance/{userId}/charge`
    - **HTTP Method**: `PATCH`
    - **Path Parameters**
        - `userId`: Long (사용자 ID)
    - **Headers**
        - `Content-Type` : application/json
    - **Body**

    ```json
    {
        "amount": 50000
    }
    ```

  ### Code snippet

    ```bash
    curl --location --request PATCH 'http://localhost:8080/v1/balance/1/charge' \
    --header 'Content-Type: application/json' \
    --data '{
        "amount": 50000
    }'
    ```

- Response

  ### Success Response:

    ```json
    {
      "userId": 1,
      "currentAmount": 40000
    }
    ```

  ### Error Response:

    ```json
    {
        "code": 404,
        "message": "user not found"
    }
    ```


### 7. 잔액 조회 API

사용자의 잔액을 조회합니다.

- Request
    - **URL**: `/v1/balance/{userId}`
    - **HTTP Method**: `GET`
    - **Path Parameters**
        - `userId`: Long (사용자 ID)

  ### Code snippet

    ```bash
    curl --location 'http://localhost:8080/v1/balance/1' \
    --data ''
    ```

- Response

  ### Success Response:

    ```json
    {
        "userId": 1,
        "currentAmount": 40000
    }
    ```

  ### Error Response:

    ```json
    {
        "code": 404,
        "message": "user not found"
    }
    ```


### 8. 결제 API

결제 처리하고 결제 내역을 생성합니다.

- Request
    - **URL**: `/v1/payments/users/{userId}`
    - **HTTP Method**: `POST`
    - **Path Parameters**
        - `userId`: Long (사용자 ID)
    - **Headers**
        - `Content-Type` : application/json
    - **Body**

        ```json
        {
            "reservationId": 1
        }
        ```


    ### Code snippet
    
    ```bash
    curl --location 'http://localhost:8080/v1/payments/users/1' \
    --header 'Content-Type: application/json' \
    --data '{
        "reservationId": 1
    }'
    ```

- Response

  ### Success Response:

    ```json
    {
        "paymentId": 1,
        "amount": 30000,
        "status": "COMPLETED"
    }
    ```

  ### Error Response:

    ```json
    {
        "code": 404,
        "message": "user not found"
    }
    ```