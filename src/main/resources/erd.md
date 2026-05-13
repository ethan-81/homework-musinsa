erDiagram
POINT_ACCOUNT ||--o{ POINT_DEPOSIT : "has"
POINT_ACCOUNT ||--o{ POINT_TRANSACTION : "owns"
POINT_TRANSACTION ||--|{ POINT_TRANSACTION_EVENT : "composed of"
POINT_TRANSACTION_EVENT ||--o{ POINT_TRANSACTION_DETAIL : "records"
POINT_DEPOSIT ||--o{ POINT_TRANSACTION_DETAIL : "tracked in"

    POINT_ACCOUNT {
        long id PK
        string userId "UK"
        string canonicalId
        long balance
        long version
        datetime createdAt
        datetime updatedAt
    }

    POINT_DEPOSIT {
        long id PK
        long accountId FK
        string pointType "FREE_POINT, ADMIN_POINT"
        long depositAmount
        long balance
        long expiredAmount
        boolean isExpired
        date expiresDate
        long version
        datetime createdAt
        datetime updatedAt
    }

    POINT_TRANSACTION {
        long id PK
        string userId FK
        string canonicalId
        string transactionType "CHARGE, USE, EXPIRE"
        long amount
        string status "READY, COMPLETED, PARTIALLY_CANCELED, CANCELED"
        string channelType
        string channelTransactionId
        datetime transactedAt
        long version
        datetime createdAt
        datetime updatedAt
    }

    POINT_TRANSACTION_EVENT {
        long id PK
        long transactionId FK
        int sequence
        string transactionEventType
        long amount
        string idempotencyKey "UK"
        string requestUserType
        string requestUserId
        string requestReason
        datetime processedAt
        datetime createdAt
    }

    POINT_TRANSACTION_DETAIL {
        long id PK
        long transactionId FK
        long transactionEventId FK
        long depositId FK
        long processedAmount
        string processingType "GRANT, DEDUCT"
        string processingCause "ORIGIN, RESTORE, ALTERNATIVE"
        long originalTransactionDetailId
        datetime createdAt
    }

    POINT_POLICY {
        long id PK
        long maxChargePoint
        long maxHoldPoint
        int validPeriodInDays
    }
