# Hủy đặt phòng dành cho khách hàng (bản cập nhật)

## Quy tắc

- Khách đăng nhập vào **Đơn đặt phòng** (`/bookings`) hoặc trang chi tiết xác nhận.
- Nếu đơn chưa đến ngày nhận phòng và chưa hủy, nút **Hủy đặt phòng** được hiển thị dù đơn đang **Chờ xác nhận** hay **Đã xác nhận**, kể cả khi đã thanh toán DEMO.
- Nhấn nút, hệ thống yêu cầu xác nhận để tránh thao tác nhầm.
- Chỉ chủ đơn mới được hủy đơn của mình. Endpoint `POST /bookings/{id}/cancel` có xác thực và CSRF của Spring Security.
- Khi hủy thành công, trạng thái booking → **Đã hủy**, phòng lại có thể được đặt cho các ngày đó, thông báo được tạo.
- Thanh toán khi nhận phòng (`DUE_AT_PROPERTY`) → `CANCELLED`; thanh toán DEMO (`SIMULATED_PAID`) → `SIMULATED_REFUNDED`. **Không có hoàn tiền thật**.
- Không thể hủy sau khi tới ngày nhận phòng hoặc hủy lặp.

## Thử nhanh

1. Đăng nhập `demo / Demo@12345` và đặt phòng ngày mai hoặc muộn hơn.
2. Chọn phương thức thanh toán demo hoặc trả tại khách sạn.
3. Mở `http://localhost:8080/bookings`, nhấn **Hủy đặt phòng** rồi đồng ý.
4. Kiểm tra trạng thái **Đã hủy**, nội dung thanh toán và thông báo.
5. Đăng nhập `admin / Admin@12345`; bảng quản lý đơn cũng hiển thị **Đã hủy**.

### Môi trường chạy

Chạy trong thư mục có `pom.xml`: `mvn clean test`, sau đó `mvn spring-boot:run`. Nếu cổng 8080 bận, tắt tiến trình cũ bằng Ctrl+C hoặc dùng `mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=8081"`.
