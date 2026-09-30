# JWT Authentication & Authorization with Spring Boot 3 & Nimbus JOSE + JWT

Dự án triển khai xác thực và phân quyền người dùng theo kiến trúc Stateless sử dụng **Spring Boot 3**, **Spring Security 6** và thư viện **Nimbus JOSE + JWT** (`com.nimbusds:nimbus-jose-jwt`) kết hợp giao diện Web Ajax/Thymeleaf.

---

## 🛠 Công nghệ sử dụng

- **Ngôn ngữ:** Java 21+
- **Framework:** Spring Boot 3.2.4
- **Bảo mật:** Spring Security 6 (Stateless JWT Authentication Filter)
- **Thư viện JWT:** **Nimbus JOSE + JWT** (`com.nimbusds:nimbus-jose-jwt:9.37.3`)
- **Cơ sở dữ liệu:** MySQL (Spring Data JPA / Hibernate)
- **Frontend:** Thymeleaf, HTML5, Bootstrap 5, jQuery / AJAX

---

## 💡 Giới thiệu về Nimbus JOSE + JWT

Trong dự án này, thư viện **Nimbus JOSE + JWT** được sử dụng thay thế cho JJWT vì:
1. **Chuẩn Enterprise:** Nimbus là thư viện chuẩn được chính Spring Security (OAuth2 / Resource Server / Authorization Server) tích hợp mặc định.
2. **Gói gọn dependency:** Chỉ cần 1 thư viện duy nhất `nimbus-jose-jwt`, không cần tách nhỏ nhiều module.
3. **Phân tách đối tượng rõ ràng:**
   - `JWSHeader`: Đại diện cho Header thuật toán (`HS256`, `RS256`, ...).
   - `JWTClaimsSet`: Đại diện cho Payload chứa các claims (`sub`, `iat`, `exp`, thông tin custom).
   - `JWSSigner` / `MACSigner`: Đối tượng thực hiện ký số HMAC.
   - `JWSVerifier` / `MACVerifier`: Đối tượng giải mã và xác thực tính hợp lệ của chữ ký.

---

## 📁 Cấu trúc thư mục dự án

```
BT10/
├── src/main/java/vn/iotstar/
│   ├── configs/
│   │   ├── ApplicationConfiguration.java     # Cấu hình UserDetailsService (hỗ trợ login bằng Email/Username), PasswordEncoder, AuthProvider
│   │   ├── SecurityConfiguration.java        # Cấu hình SecurityFilterChain, permitAll các endpoint công khai, cấu hình CORS
│   │   └── GlobalExceptionHandler.java       # Bắt và xử lý ngoại lệ toàn cục của Nimbus (JOSEException, ParseException, BadJWTException)
│   ├── controllers/
│   │   ├── AuthenticationController.java     # REST API: /auth/signup, /auth/login
│   │   ├── UserController.java               # REST API: /users/me, /users/
│   │   └── AuthController.java               # Web Controller: /login, /user/profile
│   ├── entity/
│   │   └── User.java                         # Entity ánh xạ bảng `users` trong MySQL, implements UserDetails
│   ├── filter/
│   │   └── JwtAuthenticationFilter.java      # Filter chặn request, trích xuất Bearer Token và nạp Authentication vào SecurityContext
│   ├── models/
│   │   ├── LoginResponse.java                # DTO trả về JWT Token và thời hạn
│   │   ├── LoginUserModel.java               # DTO nhận payload đăng nhập (email / username + password)
│   │   └── RegisterUserModel.java            # DTO nhận payload đăng ký tài khoản
│   ├── repository/
│   │   └── UserRepository.java               # JpaRepository (hỗ trợ tìm kiếm theo cả email và username)
│   ├── services/
│   │   ├── AuthenticationService.java        # Nghiệp vụ đăng ký và xác thực tài khoản
│   │   ├── JwtService.java                   # Nghiệp vụ sinh mã, mã hóa và xác thực JWT bằng Nimbus
│   │   └── UserService.java                  # Nghiệp vụ truy vấn danh sách người dùng
│   └── JwtSpringboot3Application.java        # Main class & CommandLineRunner tự động tạo tài khoản mẫu
├── src/main/resources/
│   ├── static/js/
│   │   └── mainjs.js                         # Xử lý AJAX đăng nhập, đăng ký và gửi Bearer Token gọi API profile
│   ├── templates/
│   │   ├── login.html                        # Giao diện Đăng nhập & Đăng ký chuyển đổi tab mượt mà
│   │   └── profile.html                      # Giao diện trang cá nhân hiển thị thông tin User
│   └── application.properties                # Cấu hình kết nối Database, Port và JWT Secret Key
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

# Cấu hình JWT Secret & Expiration Time
security.jwt.secret-key=3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b
# Thời hạn token: 1 giờ (3,600,000 ms)
security.jwt.expiration-time=3600000
```

---

## 🚀 Hướng dẫn khởi chạy

### 1. Tạo Database MySQL (Nếu chưa có):
```sql
CREATE DATABASE IF NOT EXISTS bt10;
```

### 2. Biên dịch & Chạy dự án:
```bash
mvn clean compile
mvn spring-boot:run
```
Ứng dụng sẽ chạy tại địa chỉ: `http://localhost:8005`

---

## 👤 Tài khoản mẫu tự động khởi tạo

Khi ứng dụng chạy lần đầu, `CommandLineRunner` trong [`JwtSpringboot3Application.java`](file:///d:/Documents/Web/BT10/src/main/java/vn/iotstar/JwtSpringboot3Application.java) sẽ tự động tạo sẵn tài khoản quản trị viên:
- **Email:** `admin@gmail.com`
- **Username:** `admin`
- **Mật khẩu:** `123456`
- **Họ tên:** `Quản trị viên (Admin)`

---

## 🌐 Hướng dẫn sử dụng Giao diện Web

1. **Trang Đăng nhập & Đăng ký (`http://localhost:8005/login` hoặc `http://localhost:8005/`):**
   - Hỗ trợ đăng nhập linh hoạt bằng **Email** (`admin@gmail.com`) hoặc **Username** (`admin`) kèm mật khẩu `123456`.
   - Có tab **Đăng ký** cho phép tạo nhanh tài khoản mới ngay trên trình duyệt.
2. **Trang Profile (`http://localhost:8005/user/profile`):**
   - Sau khi đăng nhập thành công, token được lưu vào `localStorage.token`.
   - Trang cá nhân tự động gửi Ajax kèm Header `Authorization: Bearer <token>` để nạp thông tin người dùng.
   - Bấm nút **Logout** để xóa token và quay về trang đăng nhập.

---

## 📮 Kiểm thử REST API (Postman / cURL)

### 1. Đăng ký tài khoản (`POST /auth/signup`)
- **URL:** `http://localhost:8005/auth/signup`
- **Method:** `POST`
- **Body (JSON):**
  ```json
  {
    "fullName": "Nguyen Huu Trung",
    "email": "trungnh@hcmute.edu.vn",
    "username": "trungnh",
    "password": "123456"
  }
  ```

---

### 2. Đăng nhập lấy Token (`POST /auth/login`)
- **URL:** `http://localhost:8005/auth/login`
- **Method:** `POST`
- **Body (JSON):** *(có thể dùng email hoặc username)*
  ```json
  {
    "email": "admin@gmail.com",
    "password": "123456"
  }
  ```
- **Response (200 OK):**
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE3Mzg...",
    "expiresIn": 3600000
  }
  ```

---

### 3. Lấy thông tin cá nhân (`GET /users/me`) - *Được bảo vệ bằng JWT*
- **URL:** `http://localhost:8005/users/me`
- **Method:** `GET`
- **Headers:**
  - `Authorization`: `Bearer <token_nhan_duoc_khi_login>`
- **Response (200 OK):** Trả về toàn bộ thông tin tài khoản hiện tại.

---

### 4. Lấy danh sách tất cả Users (`GET /users/`) - *Được bảo vệ bằng JWT*
- **URL:** `http://localhost:8005/users/`
- **Method:** `GET`
- **Headers:**
  - `Authorization`: `Bearer <token_nhan_duoc_khi_login>`

---

### 5. Xử lý ngoại lệ toàn cục (`GlobalExceptionHandler`)
- Khi gửi token sai định dạng, hết hạn hoặc không có quyền truy cập, hệ thống bắt lỗi qua Nimbus và trả về phản hồi chuẩn **RFC 7807 Problem Details** (HTTP 401 / 403 / 500).
