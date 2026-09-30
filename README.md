# JWT Demo - Spring Boot 3 & Spring Security 6

Dự án mẫu triển khai xác thực và phân quyền bằng **JSON Web Token (JWT)** sử dụng **Spring Boot 3**, **Spring Security 6**, thư viện **JJWT 0.12.6** kết hợp giao diện Web Ajax/Thymeleaf.

---

## 🛠 Công nghệ sử dụng

- **Ngôn ngữ:** Java 21+
- **Framework:** Spring Boot 3.2.4
- **Bảo mật:** Spring Security 6 (Stateless JWT Authentication)
- **Thư viện JWT:** JJWT 0.12.6 (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`)
- **Cơ sở dữ liệu:** MySQL (Spring Data JPA / Hibernate)
- **Frontend:** Thymeleaf, HTML5, Bootstrap 5, jQuery / AJAX

---

## 📁 Cấu trúc thư mục dự án

```
BT10/
├── src/main/java/vn/iotstar/
│   ├── configs/
│   │   ├── ApplicationConfiguration.java     # Cấu hình UserDetailsService, PasswordEncoder, AuthProvider
│   │   ├── SecurityConfiguration.java        # Cấu hình SecurityFilterChain, phân quyền URL, CORS
│   │   └── GlobalExceptionHandler.java       # Xử lý ngoại lệ toàn cục (ProblemDetail RFC 7807)
│   ├── controllers/
│   │   ├── AuthenticationController.java     # REST API: /auth/signup, /auth/login
│   │   ├── UserController.java               # REST API: /users/me, /users/
│   │   └── AuthController.java               # Web Controller: /login, /user/profile
│   ├── entity/
│   │   └── User.java                         # Entity ánh xạ bảng `users`, implements UserDetails
│   ├── filter/
│   │   └── JwtAuthenticationFilter.java      # Filter chặn request, trích xuất và xác thực Bearer Token
│   ├── models/
│   │   ├── LoginResponse.java                # DTO trả về token và thời hạn
│   │   ├── LoginUserModel.java               # DTO nhận thông tin đăng nhập
│   │   └── RegisterUserModel.java            # DTO nhận thông tin đăng ký
│   ├── repository/
│   │   └── UserRepository.java               # JpaRepository tương tác bảng users
│   ├── services/
│   │   ├── AuthenticationService.java        # Nghiệp vụ đăng ký và xác thực người dùng
│   │   ├── JwtService.java                   # Nghiệp vụ sinh mã, mã hóa và giải mã JWT
│   │   └── UserService.java                  # Nghiệp vụ lấy danh sách người dùng
│   └── JwtSpringboot3Application.java        # Main class & CommandLineRunner khởi tạo user mẫu
├── src/main/resources/
│   ├── static/js/
│   │   └── mainjs.js                         # Xử lý AJAX đăng nhập, đăng ký và gọi API profile
│   ├── templates/
│   │   ├── login.html                        # Giao diện Đăng nhập & Đăng ký (Tab toggle)
│   │   └── profile.html                      # Giao diện thông tin tài khoản sau đăng nhập
│   └── application.properties                # Cấu hình Database, Port và JWT Secret
└── pom.xml
```

---

## ⚙️ Cấu hình ứng dụng (`application.properties`)

```properties
spring.application.name=JWT_springboot3
server.port=8005

# Cấu hình Database MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/bt10?serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false
spring.datasource.username=root
spring.datasource.password=12345

# Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false

# Cấu hình JWT
security.jwt.secret-key=3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b
# Thời hạn token: 1 giờ (3,600,000 ms)
security.jwt.expiration-time=3600000
```

---

## 🚀 Hướng dẫn chạy ứng dụng

### 1. Chuẩn bị Cơ sở dữ liệu:
Tạo database MySQL `bt10` (nếu chưa có):
```sql
CREATE DATABASE IF NOT EXISTS bt10;
```

### 2. Biên dịch và khởi chạy:
```bash
mvn clean compile
mvn spring-boot:run
```
Ứng dụng sẽ chạy tại cổng: `http://localhost:8005`

---

## 👤 Tài khoản mặc định

Khi ứng dụng khởi động lần đầu, hệ thống tự động khởi tạo tài khoản mẫu trong Database:
- **Email:** `admin@gmail.com`
- **Username:** `admin`
- **Mật khẩu:** `123456`
- **Họ tên:** `Quản trị viên (Admin)`

---

## 🌐 Hướng dẫn sử dụng Giao diện Web

1. **Trang Đăng nhập & Đăng ký:** Mở trình duyệt vào `http://localhost:8005/login` hoặc `http://localhost:8005/`
   - Nhập `admin` hoặc `admin@gmail.com` và mật khẩu `123456` để đăng nhập.
   - Hoặc chuyển sang tab **Đăng ký** để tạo tài khoản mới trực tiếp.
2. **Trang Hồ sơ (`http://localhost:8005/user/profile`):**
   - Sau khi đăng nhập thành công, token được lưu vào `localStorage`.
   - Trang tự động gửi Ajax kèm Header `Authorization: Bearer <token>` để lấy và hiển thị thông tin User.
   - Nhấn nút **Logout** để xóa token và đăng xuất.

---

## 📮 Kiểm thử API qua Postman / cURL

### 1. Đăng ký tài khoản mới (`POST /auth/signup`)
- **URL:** `http://localhost:8005/auth/signup`
- **Method:** `POST`
- **Headers:** `Content-Type: application/json`
- **Body (raw JSON):**
  ```json
  {
    "fullName": "Nguyễn Hữu Trung",
    "email": "trungnh@hcmute.edu.vn",
    "username": "trungnh",
    "password": "123456"
  }
  ```
- **Response (200 OK):**
  ```json
  {
    "id": 2,
    "fullName": "Nguyễn Hữu Trung",
    "email": "trungnh@hcmute.edu.vn",
    "images": "https://cdn-icons-png.flaticon.com/512/847/847969.png",
    "createdAt": "2026-09-30T01:48:47.207+00:00",
    "updatedAt": "2026-09-30T01:48:47.207+00:00"
  }
  ```

---

### 2. Đăng nhập lấy JWT Token (`POST /auth/login`)
- **URL:** `http://localhost:8005/auth/login`
- **Method:** `POST`
- **Headers:** `Content-Type: application/json`
- **Body (raw JSON):** *(hỗ trợ email hoặc username)*
  ```json
  {
    "email": "trungnh@hcmute.edu.vn",
    "password": "123456"
  }
  ```
- **Response (200 OK):**
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0cnVuZ25oQGhjbXV0ZS5lZHUudm4i...",
    "expiresIn": 3600000
  }
  ```

---

### 3. Lấy thông tin tài khoản hiện tại (`GET /users/me`) - *Yêu cầu Token*
- **URL:** `http://localhost:8005/users/me`
- **Method:** `GET`
- **Headers:**
  - `Authorization`: `Bearer <paste_jwt_token_here>`
- **Response (200 OK):**
  ```json
  {
    "id": 2,
    "fullName": "Nguyễn Hữu Trung",
    "email": "trungnh@hcmute.edu.vn",
    "images": "https://cdn-icons-png.flaticon.com/512/847/847969.png",
    "authorities": [],
    "username": "trungnh@hcmute.edu.vn",
    "accountNonExpired": true,
    "accountNonLocked": true,
    "credentialsNonExpired": true,
    "enabled": true
  }
  ```

---

### 4. Lấy danh sách tất cả người dùng (`GET /users/`) - *Yêu cầu Token*
- **URL:** `http://localhost:8005/users/`
- **Method:** `GET`
- **Headers:**
  - `Authorization`: `Bearer <paste_jwt_token_here>`
- **Response (200 OK):** Trả về danh sách mảng JSON các User.

---

### 5. Kiểm thử xử lý lỗi (Global Exception Handler)
- Khi truy cập endpoint được bảo vệ mà không có token hoặc token sai định dạng (`Bearer dfdfdfdf`), hệ thống trả về mã lỗi kèm cấu trúc chuẩn **RFC 7807 Problem Details**:
  ```json
  {
    "type": "about:blank",
    "title": "Forbidden",
    "status": 403,
    "detail": "The JWT signature is invalid",
    "instance": "/users/me",
    "description": "The JWT signature is invalid"
  }
  ```
