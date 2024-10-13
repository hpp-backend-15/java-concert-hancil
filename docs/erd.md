erDiagram
    user {
        long user_id PK
        string name
        float balance
    }

    concert {
        int id PK
        string name
        string description
    }

    concert_schedule {
        int id PK
        int concert_id FK
        date reservation_available_at
        date concert_at
    }

    seat {
        int id PK
        int concert_schedule_id
        int seat_number
        string status "available, reserved, occupied"
        int seat_price
    }

    reservation {
        int id PK
        int user_id FK
        date reservation_at
        string status "pending, confirmed, cancelled"
    }

    reservation_item {
        int id PK
        int reservation_id FK
        int seat_id FK
        int seat_price
    }

    payment {
        int id PK
        int reservation_id FK
        date payment_at
        float amount
        string payment_method
        string status "pending, completed, failed"
    }

    balance_history {
        int id PK
        int user_id FK
        date transaction_at
        float amount
        string type 
    }


  queue {
        int id PK
        int concert_schedule_id FK
        long user_id FK
        date queued_at
        string status "waiting, processed, cancelled"
    }

    queue_token {
        int id PK
        int queue_id FK
        string token
        date issued_at
        date expiration_at
    }

    queue ||--o{ queue_token : "issues"


    user ||--o{ reservation : "makes"
    concert ||--o{ concert_schedule : "has"
    concert_schedule ||--o{ seat : "offers"
    reservation ||--o{ reservation_item : "contains"
    seat ||--o{ reservation_item : "is part of"
    reservation ||--o{ payment : "is paid with"
    user ||--o{ balance_history : "performs"
     user ||--o{ queue : "registers"
