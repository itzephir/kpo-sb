# HW4: Асинхронное межсервисное взаимодействие

## Описание

Реализация системы микросервисов для интернет-магазина с выделенным модулем общей логики:
- **Core** - модуль общих компонентов для микросервисов
- **Apphost/API Gateway** (порт 8080) - единая точка входа для всех запросов пользователей
- **Orders Service** (порт 8081) - управление заказами
- **Payments Service** (порт 8082) - управление счетами и оплатами

## Архитектура

### Технологический стек
- **Ktor** - веб-фреймворк
- **Koin** - dependency injection
- **PostgreSQL** - база данных (Exposed ORM)
- **Kafka** - message broker для асинхронной коммуникации
- **Transactional Outbox/Inbox** - паттерны для гарантии exactly-once семантики

### Модульная структура

#### Core Module
Библиотечный модуль с общими компонентами:
- **Database Configuration** - настройка PostgreSQL с HikariCP
- **Outbox/Inbox Tables** - универсальные таблицы для паттернов
- **Kafka Services** - producer/consumer с настройками надежности
- **Processors** - базовые классы для обработки событий
- **Event Models** - общие модели событий

#### Services
Каждый сервис использует компоненты из core модуля:
- **Orders Service** - создание заказов, обработка статусов платежей
- **Payments Service** - управление счетами, обработка платежей

## Запуск системы

### Требования
- Docker и Docker Compose
- Java 17+ (для локальной разработки)

### Запуск через Docker Compose

```bash
cd homework/hw4
docker-compose up --build
```

Система запустит:
- PostgreSQL для Orders Service (порт 5432)
- PostgreSQL для Payments Service (порт 5433)
- Zookeeper
- Kafka (порт 9092)
- Apphost/API Gateway (порт 8080) - единая точка входа
- Orders Service (порт 8081)
- Payments Service (порт 8082)

## API Endpoints

Все запросы делаются через **Apphost/API Gateway** на порту **8080**.

### Payments Service (через API Gateway)

#### Создать счет
```http
POST /accounts
X-User-Id: user123
```

#### Пополнить счет
```http
POST /accounts/topup
X-User-Id: user123
Content-Type: application/json

{
  "amount": "100.00"
}
```

#### Получить баланс
```http
GET /accounts/balance
X-User-Id: user123
```

### Orders Service (через API Gateway)

#### Создать заказ
```http
POST /orders
X-User-Id: user123
Content-Type: application/json

{
  "amount": "50.00",
  "description": "Test order"
}
```

#### Получить список заказов
```http
GET /orders
X-User-Id: user123
```

#### Получить заказ по ID
```http
GET /orders/{orderId}
X-User-Id: user123
```

## Postman Collection

Импортируйте файл `postman_collection.json` в Postman для тестирования всех endpoints.

## Поток создания заказа и оплаты

### 1. Создание заказа (Orders Service)
1. Пользователь создает заказ через API
2. Заказ сохраняется в БД со статусом NEW
3. Событие `PaymentRequestEvent` записывается в **OutboxTable**
4. **OutboxProcessor** отправляет событие в Kafka topic `payment-request`

### 2. Обработка платежа (Payments Service)
1. **KafkaConsumer** получает событие и сохраняет в **InboxTable**
2. **InboxProcessor** обрабатывает сообщения из inbox:
   - Проверяет наличие счета
   - Проверяет достаточность средств
   - Списывает деньги (с optimistic locking)
3. Результат (`PaymentStatusEvent`) записывается в **OutboxTable**
4. **OutboxProcessor** отправляет статус в Kafka topic `payment-status`

### 3. Обновление статуса заказа (Orders Service)
1. **KafkaConsumer** получает статус платежа
2. Статус заказа обновляется:
   - SUCCESS → FINISHED
   - FAILED → CANCELLED

## Гарантии надежности

- **Exactly-once семантика**: Используется таблица `payment_attempts` для идемпотентности
- **Optimistic Locking**: Поле `version` в таблице `accounts` предотвращает race conditions
- **Transactional Outbox**: События записываются в БД в той же транзакции, что и бизнес-операция
- **Transactional Inbox**: Сообщения из Kafka сохраняются в inbox перед обработкой

## Структура проекта

```
hw4/
├── core/                    # Общие компоненты
│   ├── src/main/kotlin/
│   │   └── ru/hse/core/
│   │       ├── db/          # Database configuration и tables
│   │       ├── kafka/       # Kafka producer/consumer
│   │       ├── outbox/      # Outbox processor
│   │       ├── inbox/       # Inbox processor
│   │       └── events/      # Event models
│   └── build.gradle.kts
├── apphost/                 # API Gateway
│   ├── src/main/kotlin/
│   │   ├── routing/        # Gateway routing
│   │   ├── di/             # Dependency injection
│   │   └── Application.kt
│   └── Dockerfile
├── orders-service/          # Order Service
│   ├── src/main/kotlin/
│   │   ├── db/             # Service-specific tables
│   │   ├── kafka/          # Kafka aliases
│   │   ├── outbox/         # Outbox processor extension
│   │   ├── routing/        # REST endpoints
│   │   ├── service/        # Business logic
│   │   └── Application.kt
│   └── Dockerfile
├── payments-service/        # Payments Service
│   ├── src/main/kotlin/
│   │   ├── db/             # Service-specific tables
│   │   ├── kafka/          # Kafka aliases
│   │   ├── inbox/          # Inbox processor extension
│   │   ├── outbox/         # Outbox processor extension
│   │   ├── routing/        # REST endpoints
│   │   ├── service/        # Business logic
│   │   └── Application.kt
│   └── Dockerfile
├── docker-compose.yml       # Docker Compose configuration
└── postman_collection.json  # Postman collection for API testing
```

## Преимущества модульной архитектуры

### DRY принцип
- Устранено дублирование кода между сервисами
- Общие компоненты вынесены в core модуль

### Переиспользование
- Новые сервисы могут легко использовать компоненты из core
- Стандартизированные паттерны Outbox/Inbox

### Консистентность
- Единообразная реализация database configuration
- Общие модели событий для всех сервисов

### Поддержка
- Изменения в общей логике применяются ко всем сервисам
- Централизованное управление зависимостями

### Тестируемость
- Общие компоненты можно тестировать независимо
- Упрощенное unit-тестирование сервисов

## Разработка

### Сборка проекта
```bash
./gradlew build
```

### Запуск отдельного сервиса
```bash
./gradlew :orders-service:run
./gradlew :payments-service:run
./gradlew :apphost:run
```

### Добавление нового сервиса
1. Создайте новый модуль в `settings.gradle.kts`
2. Добавьте зависимость на `core` в `build.gradle.kts`
3. Используйте компоненты из `ru.hse.core.*`
4. Реализуйте специфичную для сервиса логику
