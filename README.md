# HE THONG QUAN LY DAT PHONG KHACH SAN

> Dự án minh họa học phần **Design Patterns và SOLID**. Tên thư mục và file ZIP được viết không dấu để thuận tiện khi mở trên Windows/VS Code.

- Java 17+ (đã hỗ trợ Java 21) · Spring Boot 3.5.16 · Thymeleaf · Spring Security · JPA · H2/MySQL · Maven.
- 6 Design Patterns: Factory Method, Builder, Adapter, Facade, Strategy, Observer.
- Website minh họa có tên **Lumi Stay (BOUTIQUE HOTEL (DEMO))**, không đại diện khách sạn có thật.

## CẬP NHẬT: Admin xác nhận đơn đặt phòng

- Khách gửi yêu cầu đặt phòng → trạng thái **PENDING / Chờ xác nhận**.
- Khách chọn thanh toán tại khách sạn hoặc thanh toán online **demo** → trạng thái **vẫn PENDING**. Thanh toán và xác nhận đơn là **hai trạng thái độc lập**.
- Admin đăng nhập `admin` / `Admin@12345`, mở **http://localhost:8080/admin**, tại **Quản lý đơn đặt phòng** chọn **Xác nhận** cho đơn đang chờ → `CONFIRMED / Đã xác nhận`.
- Khách mở `/bookings` hoặc `/bookings/{id}/confirmation` để xem trạng thái mới; mục `/notifications` có thông báo khi admin phê duyệt.
- Khách **không thể** gọi API phê duyệt (Spring Security chỉ cho phép `ROLE_ADMIN` truy cập `/admin/**`). Đơn đã xác nhận/hủy không thể duyệt lần nữa.
- Nếu Admin xác nhận trước khi khách chọn thanh toán, khách vẫn có thể quay lại trang **Chọn thanh toán**. Việc chọn thanh toán không bao giờ tự duyệt đơn.
- Đây là **bản demo**. Các khoản đã được đánh dấu thanh toán demo không phải giao dịch thật.

Xem thêm `docs/QUY_TRINH_ADMIN_XAC_NHAN.md`.

## 1. Cách mở trên VS Code (Windows)

1. Giải nén file ZIP phiên bản **admin xác nhận đơn**. ZIP đặt `pom.xml` ở ngay cấp gốc, không lồng thêm thư mục dự án.
2. Trong VS Code chọn **File > Open Folder**, mở thư mục vừa giải nén **có file `pom.xml` ngay bên trong**.
3. Đảm bảo có **JDK 17/21**, **Maven 3.6+**, Extension Pack for Java và Spring Boot Extension Pack.
4. Kiểm tra Java/Maven rồi build, chạy:

   ```powershell
   java -version
   mvn -version
   mvn clean test
   mvn spring-boot:run
   ```

5. Mở **http://localhost:8080/rooms**. Khi muốn dừng chạy: `Ctrl + C` ở Terminal.

Nếu VS Code chưa nhận `mvn` nhưng máy đã cài Maven vào `C:\dev\apache-maven-3.10.0`, chạy tạm lệnh sau trong Terminal rồi chạy `mvn -version`:

```powershell
$env:Path += ";C:\dev\apache-maven-3.10.0\bin"
```

Máy đã cài Maven ở đường dẫn ví dụ trên còn có thể chạy file `run-demo.ps1` để tự tìm Maven; file này không đòi hỏi `mvn` được nhận qua PATH.

Để thiết lập vĩnh viễn, bổ sung `C:\dev\apache-maven-3.10.0\bin` vào biến môi trường `Path` trong Windows và **mở lại** VS Code.

### Tài khoản DEMO

| Vai trò | Username | Mật khẩu |
|---|---|---|
| Khách hàng | `demo` | `Demo@12345` |
| Quản trị | `admin` | `Admin@12345` |

**Không dùng mật khẩu seed này khi đưa hệ thống lên Internet.**

## 2. Luồng sử dụng

1. `/rooms`: khách xem giao diện tìm kiếm, chọn ngày/khách; bộ lọc **loại phòng, mức giá, sắp xếp** thực hiện truy vấn theo tham số URL.
2. Nhấn **Xem phòng** để mở `/rooms/{id}`, xem ảnh minh họa, tiện nghi, chính sách, chọn ngày và gói giá; tổng giá **ước tính** cập nhật trong trình duyệt.
3. Đăng nhập (nếu chưa đăng nhập), nhấn **Tiếp tục đặt phòng** để tạo đơn `PENDING` (chờ Admin xác nhận).
4. `/bookings/{id}/checkout`: xem tổng số đêm, giá cơ bản, phụ thu cuối tuần và chọn cách thanh toán.
5. `CASH`: ghi nhận sẽ trả tại khách sạn, **DUE_AT_PROPERTY (chưa thu tiền)**; đơn vẫn `PENDING`.
6. `MOCK_WALLET` hoặc `MOCK_BANK`: mô phỏng thanh toán **SIMULATED_PAID**; đơn vẫn `PENDING`, không phát sinh giao dịch thật.
7. Admin vào `/admin`, bấm **Xác nhận** cho đơn `PENDING`; chỉ lúc này mới thành `CONFIRMED`. Khách xem `/bookings/{id}/confirmation` để theo dõi mã đơn, ngày ở, trạng thái đặt phòng và thanh toán.
8. `/bookings`: xem lịch sử, hủy đơn chưa thu tiền trước ngày nhận phòng. `/profile`: xem thông tin đăng nhập, `/notifications`: thông báo. `/admin`: xem danh sách phòng và đơn.

### Lưu ý về tiền và an toàn

- **Không tích hợp MoMo, ZaloPay, VNPay, Stripe hay ngân hàng.** Không thu thập số thẻ, OTP, QR, thông tin tài khoản nhận tiền.
- Phương thức `CASH` chỉ ghi nhận phương thức, để trạng thái **CHƯA thanh toán** và booking **PENDING**; không lưu `paidAt`.
- Hai phương thức online là **giả lập trong mã nguồn**, không có xử lý thanh toán thật, mã tham chiếu bắt đầu với `DEMO-`.
- Giá hiện tại là **giá mô phỏng**, tính theo số đêm và tùy chọn phụ thu cuối tuần. Không có tính thuế hay phí phát sinh của khách sạn thực.
- Đơn đã thanh toán demo bị khóa hủy tự động để không giả vờ hoàn tiền. Quy trình hoàn tiền thật chưa triển khai.
- Cơ chế đặt phòng trùng lịch dùng khóa bi quan, nhưng chưa có cơ chế hết hạn đơn PENDING và chưa hoàn thiện xử lý sản xuất (log/audit, timeout, migration, cổng thanh toán).

## 3. Các thay đổi so với bản đầu

| Vấn đề cũ | Bản cập nhật |
|---|---|
| Thanh menu luôn có cả Đăng nhập lẫn Đăng xuất | Trạng thái guest/account rõ ràng; sau login hiện avatar chữ đầu và username, menu Hồ sơ/Đăng xuất |
| Nhấn Thanh toán trong bảng đơn | Có trang **checkout** độc lập, hiển thị giá trước khi xác nhận |
| Chọn tiền mặt nhưng vẫn bị tính như đã thanh toán | `DUE_AT_PROPERTY`, booking vẫn chờ duyệt, chưa thu tiền |
| Chỉ có ví giả lập và tiền mặt | Thêm chuyển khoản giả lập, 3 lựa chọn giải thích rõ |
| Không có phiếu xác nhận | Có trang xác nhận đơn và mã giao dịch demo |
| Thẻ phòng ít thông tin | Giao diện responsive, bộ lọc ngày/khách, ảnh minh họa, tiện nghi và giá theo đêm |
| File ZIP tên khác đề tài | Tên ZIP mới `He_thong_quan_ly_dat_phong_khach_san_admin_xac_nhan.zip` |

## 4. Cấu trúc source

- `src/main/java/com/example/hotel/domain/`: thực thể, enum `PaymentStatus` và `PaymentMethod`.
- `pattern/factory/`, `pattern/adapter/`, `pattern/facade/`, `pattern/strategy/`, `pattern/observer/`: các mẫu thiết kế.
- `service/`: nghiệp vụ đặt phòng và thanh toán.
- `repository/`: JPA truy vấn; khóa khi đặt/trả.
- `web/`: controller và model attribute cho thanh điều hướng.
- `src/main/resources/templates/`: Thymeleaf, gồm `fragments/layout.html`, `checkout.html`, `confirmation.html`.
- `src/main/resources/static/css/style.css`: thiết kế giao diện.
- `src/test/`: các test JUnit, Mockito, MockMvc (kể cả PaymentServiceUnitTests mới).
- `docs/DOI_CHIEU_UX.md`: các nguồn đã tham khảo để cải thiện luồng UX.
- `Bao_cao/`: file Word Chương 3 và Chương 4 của dự án trước. **Nội dung Chương 4 chưa cập nhật đầy đủ thay đổi giao diện này**; xem `docs/CAP_NHAT_CHUONG_4.md` để chỉnh vào báo cáo.

## 5. Dùng MySQL thay H2

Mặc định sử dụng **H2 trong bộ nhớ**. Dữ liệu nhập mới **mất sau khi tắt ứng dụng**. Có thể dùng MySQL để lưu lâu dài:

1. Chạy `database/create_database.sql` trong MySQL.
2. Trên Terminal PowerShell:

```powershell
$env:DB_USER="root"
$env:DB_PASSWORD=""
mvn spring-boot:run "-Dspring-boot.run.profiles=mysql"
```

Nếu **đang dùng MySQL với dữ liệu của bản cũ**, nên **sao lưu DB** trước khi chuyển sang bản mới vì bảng `payments` thêm cột `status` và thay đổi ý nghĩa trạng thái thanh toán. Dùng H2 demo mới là lựa chọn nhanh nhất; không tự ý nâng cấp dữ liệu có sẵn bằng `ddl-auto=update` để chạy sản xuất.

## 6. Kiểm thử và giới hạn xác minh

Chạy `mvn clean test` tại thư mục chứa `pom.xml`. Kiểm tra thêm bằng trình duyệt: đăng nhập → tên thay nút Đăng nhập; đặt phòng → trang checkout; chọn trả tại khách sạn → xem đơn có trạng thái chưa trả; chọn ví demo → trạng thái thanh toán giả lập; thử hủy đơn đúng quy tắc; đăng xuất → nút Đăng nhập trở lại.

**Lưu ý:** Các thay đổi trong phiên bản cập nhật được kiểm tra cấu trúc/tĩnh khi đóng gói. Môi trường tạo gói không có Maven cùng bộ thư viện Spring Boot, nên chưa thể xác nhận `mvn clean test` đạt trên bản này. Cần chạy lệnh trên máy của bạn để xác nhận.

## 7. Ảnh tham khảo

Giao diện hiển thị ảnh mẫu từ Unsplash qua Internet (nếu offline sẽ hiển thị nền màu). Ảnh minh họa loại phòng, **không phải ảnh thực tế của khách sạn hoặc số phòng được quảng cáo**.

- https://unsplash.com/photos/white-bed-linen-on-bed-oxeCZrodz78
- https://unsplash.com/s/photos/hotel-bedroom



## 8. Giao diện cảm hứng từ website đặt phòng phổ biến (bản mới)

- Tái thiết kế **tất cả luồng tìm và chọn phòng** bằng giao diện riêng mang thương hiệu *Lumi Stay*: ảnh hero, thanh tìm phòng lớn, thông tin tiện nghi, bộ lọc, danh sách phòng dạng ngang, trang chi tiết và khu vực báo giá.
- Trang chủ: `/rooms`; trang chi tiết: `/rooms/{id}`. Hai trang hoạt động cho cả người dùng **chưa đăng nhập**; tạo đơn yêu cầu đăng nhập.
- Bộ lọc có xử lý **phía server** trong `PublicController`: `type=STANDARD|DELUXE|SUITE`, `maxPrice`, `sort=recommended|price-asc|price-desc|capacity`, kết hợp ngày và số khách; không phải bộ lọc giả.
- Trình tính giá **ước tính** sử dụng `src/main/resources/static/js/site.js` để tính ngày và gói cuối tuần. **Backend** tại `BookingService`/`PricingService` vẫn là nơi chốt giá cuối cùng.
- H2 mới khởi tạo **9 phòng mẫu** gồm Standard, Deluxe và Suite. Nếu DB MySQL đã có phòng, dữ liệu seed không thêm trùng. Có thể nhập thêm phòng trong trang quản trị.
- Ảnh minh họa tải từ **Unsplash CDN**: cần Internet để nhìn thấy ảnh. Nếu offline, màu nền dự phòng vẫn hiển thị. Ảnh không đại diện cho một khách sạn có thật; không dùng lại ảnh/nhận diện của Traveloka.
- Các chức năng đăng nhập, tài khoản, quản lý phòng, đặt phòng, tính giá theo Strategy, thanh toán demo và thông báo vẫn được giữ nguyên.

### Kiểm thử sau khi giải nén

```powershell
mvn clean test
mvn spring-boot:run
```

Vào `http://localhost:8080/rooms`. Thử các mục: chọn ngày → tìm phòng, lọc Suite, chọn giá thấp đến cao, xem chi tiết, thay ngày để cập nhật tổng dự kiến, đăng nhập để đặt, chọn thanh toán tại khách sạn và xem mã xác nhận.

**Chú ý:** Bản này được kiểm tra tĩnh HTML/JavaScript/cấu trúc đường dẫn; trong môi trường tạo file không cài sẵn Maven và kho thư viện Java, nên chưa xác nhận biên dịch và chạy kiểm thử. Hãy chạy `mvn clean test` trên máy bạn trước khi nộp.

**Tham khảo trải nghiệm:** https://www.traveloka.com/vi-vn/hotel (nghiên cứu luồng tìm kiếm, bộ lọc, thẻ phòng, không sao chép CSS/HTML/thương hiệu). Ảnh Unsplash theo https://unsplash.com/license.

## Cập nhật: Khách hàng tự hủy đặt phòng

Tại **Đơn đặt phòng** (`/bookings`) và **Xem chi tiết đơn**, khách có thể nhấn **Hủy đặt phòng** trước ngày nhận phòng. Có xác nhận thao tác. Các đơn thanh toán DEMO được chuyển sang trạng thái **đã hoàn tiền demo**, không phát sinh giao dịch thật. Xem `docs/HUY_DAT_PHONG_KHACH_HANG.md`.
