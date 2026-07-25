# Divvy Backend Service 💸

Backend service cho hệ thống quản lý chi tiêu nhóm, chia tiền (expense splitting), công nợ và thanh toán **Divvy**.

---

## 🚀 Công Nghệ Sử Dụng (Tech Stack)

- **Java Language**: Java 26
- **Framework**: Spring Boot `4.1.0`
- **Security**: Spring Security & JWT (`jjwt 0.12.6`)
- **Data Access**: Spring Data JPA & Hibernate
- **Database**: PostgreSQL 16
- **Database Migration**: Flyway (`flyway-core`, `flyway-database-postgresql`)
- **DTO Mapping**: MapStruct `1.6.3`
- **Code Generation**: Lombok
- **Containerization**: Docker & Docker Compose
- **Build Tool**: Gradle

---

## 🏗 Kiến Trúc Hệ Thống (Architecture)

Hệ thống tuân thủ mô hình **Layered Architecture** kết hợp với **Facade Pattern**:

```
Client / Frontend
       │ (HTTP Requests)
       ▼
Controllers (`com.hcmut.divvy.controller`)
       │ (100% Facade-Centric)
       ▼
Facades (`com.hcmut.divvy.facade`)
       │ (Phân giải động qua BaseFacade -> Spring ApplicationContext)
       ▼
Services (`com.hcmut.divvy.service`) ──► Validators (`com.hcmut.divvy.validator`)
       │
       ▼
Repositories (`com.hcmut.divvy.repository`)
       │
       ▼
Entities (`com.hcmut.divvy.entity`) ──► PostgreSQL Database
```

> 📖 Chi tiết sơ đồ kiến trúc và luồng xử lý request mẫu xem tại [ARCHITECTURE.md](file:///e:/WORKSPACE/PROJECT_WEB/khoi-nghiep/backend/main/ARCHITECTURE.md).

---

## 🛠 Hướng Dẫn Khởi Chạy

### 1. Yêu Cầu Tiền Đề (Prerequisites)
- JDK 21+ (Khuyến nghị Java 26)
- Docker & Docker Compose

### 2. Chạy Bằng Docker Compose (Khuyên dùng)
Khởi chạy cả Database (PostgreSQL) và Backend service chỉ với 1 lệnh:

```bash
docker-compose up -d --build
```

Dịch vụ backend sẽ hoạt động tại: `http://localhost:8080`

### 3. Chạy Trong Môi Trường Phát Triển (Local Dev)
1. Khởi chạy riêng container Database:
   ```bash
   docker-compose up -d db
   ```
2. Khởi chạy ứng dụng bằng Gradle Wrapper:
   - **Windows:**
     ```cmd
     .\gradlew.bat bootRun
     ```
   - **Linux / macOS:**
     ```bash
     ./gradlew bootRun
     ```

---

## 🧪 Biên Dịch & Kiểm Thử

- **Biên dịch Java code**:
  ```bash
  ./gradlew compileJava
  ```
- **Chạy Tests**:
  ```bash
  ./gradlew test
  ```
- **Tạo bản đóng gói JAR**:
  ```bash
  ./gradlew bootJar
  ```

---

## 🔒 API Endpoints & Authorization

- **Public Endpoints** (Không yêu cầu Token):
  - `POST /api/auth/register` - Đăng ký tài khoản
  - `POST /api/auth/login` - Đăng nhập lấy JWT Bearer Token
  - `/actuator/**` - System health check & metrics
  - `/error` - Standard error responses
- **Protected Endpoints** (Yêu cầu `Authorization: Bearer <JWT>`):
  - Tất cả các API còn lại (`/api/users/**`, `/api/groups/**`, `/api/expenses/**`, ...)
