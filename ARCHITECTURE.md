# Kiến Trúc Hệ Thống Backend (Divvy App)

Tài liệu này mô tả chi tiết kiến trúc, mô hình tổ chức source code và luồng dữ liệu của hệ thống backend Divvy.

---

## 1. Sơ đồ Kiến trúc Tổng quan (Architecture Diagram)

Hệ thống được thiết kế theo mô hình **Layered Architecture** nâng cao kết hợp **Single Model Parameter Pattern** (Command Object Pattern), **Pure Decoupled Validator Layer**, và **Strongly-Typed Enums** nhằm mục đích đảm bảo tính rõ ràng, tách biệt trách nhiệm, tránh phình to phương thức và dễ dàng mở rộng.

```mermaid
graph TD
    Client[Client / Frontend] -->|HTTP Requests - DTOs| Controllers[Controller Layer]
    
    subgraph Controller to Model Transformation
        Controllers -->|MapStruct Mappers| Mappers[MapStruct Mappers]
        Mappers -->|Convert Request DTO + Auth + PathParams| Models[Single Command Models com.hcmut.divvy.service.model]
    end

    subgraph Service & Business Layer
        Controllers -->|Injected Interface| Services[Service Layer Interfaces com.hcmut.divvy.service]
        Services -->|Single Model Input| ServiceImpls[Service Implementations com.hcmut.divvy.service.impl]
        ServiceImpls -->|1. Fetch Entities & Data| Repositories[Repository Layer JPA]
        ServiceImpls -->|2. Pass Entities for Rule Assertions| Validators[Pure Validator Layer com.hcmut.divvy.validator]
    end
    
    subgraph Data Access Layer
        ServiceImpls -->|Data Persist/Query| Repositories
        Repositories -->|ORM Mapping| Entities[JPA Entities]
        Entities -->|Uses Enums| Enums[Strongly-Typed Enums com.hcmut.divvy.entity.enums]
        Entities -->|Extends Audit| BaseEntity[BaseEntity]
    end

    subgraph Database
        Repositories -->|PostgreSQL| DB[(PostgreSQL Database)]
        Flyway[Flyway Migrations] -->|Schema Versioning| DB
        MigrationGen[Prisma-Like MigrationGenerator] -->|Diff Entity vs DB| Flyway
    end
```

---

## 2. Mô tả các Layer (Layer Breakdown)

### 2.1. Controller Layer (`com.hcmut.divvy.controller`)
*   **Nhiệm vụ:** Tiếp nhận HTTP requests, xử lý validation đầu vào bằng `jakarta.validation.constraints` (thông qua `@Valid`), ánh xạ DTOs/Request Params thành Single Model đối tượng thông qua MapStruct Mapper và trả về cấu trúc response đồng nhất `ApiResponse<T>`.
*   **Nguyên tắc:** Inject trực tiếp interface `Service` tương ứng để xử lý công việc.
*   **Ví dụ:** [GroupController](file:///e:/WORKSPACE/PROJECT_WEB/khoi-nghiep/backend/main/src/main/java/com/hcmut/divvy/controller/GroupController.java)

### 2.2. Service Layer (`com.hcmut.divvy.service`)
*   **Nhiệm vụ:** Định nghĩa business interfaces tại gói gốc (`com.hcmut.divvy.service`) và thực thi business logic tại lớp Implementations (`com.hcmut.divvy.service.impl`).
*   **Cấu trúc 2 Folder Con:**
    *   `com.hcmut.divvy.service.impl`: Chứa toàn bộ các lớp Service Implementation thực thi nghiệp vụ (`AuthServiceImpl`, `GroupServiceImpl`, `GroupMemberServiceImpl`, `UserServiceImpl`, `EmailServiceImpl`).
    *   `com.hcmut.divvy.service.model`: Chứa toàn bộ các Single Command/Parameter Models (`CreateGroupModel`, `UpdateGroupModel`, `LoginModel`, `RegisterModel`, `AddMemberModel`,...).
*   **Nguyên tắc Single Model Parameter Pattern & Data Fetching:**
    *   Mỗi phương thức của Service chỉ nhận **DUY NHẤT 1 đối tượng Model** làm tham số đầu vào (ví dụ `groupService.create(CreateGroupModel model)`), gom toàn bộ dữ liệu request body, path parameters và thông tin tài khoản đang đăng nhập.
    *   Lớp implementation đóng vai trò **Orchestrator**: Tự truy xuất dữ liệu/Entities từ Repositories, sau đó chuyển các Entities/trạng thái này cho tầng Validator để kiểm tra điều kiện nghiệp vụ trước khi tiến hành ghi/lưu dữ liệu.

### 2.3. Pure Decoupled Validator Layer (`com.hcmut.divvy.validator`)
*   **Nhiệm vụ:** Tập trung kiểm tra các quy tắc nghiệp vụ, quyền hạn (Authorization checks) và điều kiện hợp lệ trên các Entity/Tham số truyền vào (`UserValidator`, `GroupValidator`, `GroupMemberValidator`, `InvitationValidator`, `PasswordResetValidator`).
*   **Đặc điểm thiết kế (Pure Validation):**
    *   **Không inject Repository:** Tầng Validator hoàn toàn không phụ thuộc vào tầng Data Access Layer hay Database.
    *   **Tách biệt trách nhiệm:** Service chịu trách nhiệm lấy dữ liệu từ DB, còn Validator đóng vai trò Pure Functions chỉ nhận dữ liệu và thực thi quy tắc kiểm tra.
    *   **Hiệu năng & Dễ Test:** Tránh lặp lại các truy vấn DB không cần thiết (Double Fetching) và giúp viết Unit Test cho Validator cực kỳ dễ dàng (không cần Mock Repository).

### 2.4. Entity & Enum Layer (`com.hcmut.divvy.entity` / `com.hcmut.divvy.entity.enums`)
*   **Nhiệm vụ:** Khai báo cấu trúc bảng (ORM mapping). Tất cả các entities kế thừa từ `BaseEntity` để tự động quản lý các trường audit (`created_at`, `updated_at`) thông qua JPA Auditing.
*   **Enums:**
    *   `UserRole`: Quản lý phân quyền người dùng (`USER`, `ADMIN`).
    *   `InvitationStatus`: Quản lý trạng thái lời mời (`PENDING`, `ACCEPTED`, `DECLINED`, `EXPIRED`, `REVOKED`).
    *   `DebtStatus`: Quản lý trạng thái công nợ (`PENDING`, `SETTLED`, `CANCELLED`).

### 2.5. Repository Layer (`com.hcmut.divvy.repository`)
*   **Nhiệm vụ:** Giao tiếp trực tiếp với database. Kế thừa `JpaRepository` của Spring Data JPA.

---

## 3. Quản lý Database Schema (Database Migrations - Prisma-Like Workflow)

Hệ thống sử dụng **Flyway** kết hợp với bộ công cụ **MigrationGenerator (Prisma-like)** để tự động quản lý lịch sử và đồng bộ hóa cấu trúc cơ sở dữ liệu PostgreSQL.

Các file SQL migration được tự động đặt tại thư mục [db/migration](file:///e:/WORKSPACE/PROJECT_WEB/khoi-nghiep/backend/main/src/main/resources/db/migration) với định dạng đặt tên timestamp chuẩn: `V<YYYYMMDDHHMMSS>__<tên_migration>.sql`.

---

## 4. Luồng xử lý một Request mẫu (Request Flow Example)

Khi Client thực hiện gửi request tạo Nhóm mới (`POST /api/groups`):

```text
Client  ──► [Controller: GroupController.createGroup(request, authentication)]
                 │
                 ▼  (Validates @Valid CreateGroupRequest & Maps to Single Model)
            [GroupMapper: groupMapper.toModel(request, authentication.getName())]
                 │
                 ▼  (Passes Single CreateGroupModel)
            [Service: GroupServiceImpl.create(CreateGroupModel model)]
                 │
                 ├──► [Repository: Query Creator User, Category, Currency]
                 ├──► [Validator: pure rule checks on fetched entities]
                 │
                 ├──► [Repository: GroupRepository.save(Group entity)]
                 ├──► [Repository: GroupMemberRepository.save(Creator as OWNER)]
                 │
                 ▼  (Mapping Entity -> GroupResponse)
Client  ◄── [Return ApiResponse.created(groupResponse)]
```
