# Quy trình duyệt đơn đặt phòng (cập nhật)

## Luồng trạng thái

```text
Khách chọn phòng → Tạo đơn PENDING (CHỜ XÁC NHẬN)
                   ↓
           Chọn thanh toán (tùy chọn)
           • CASH => DUE_AT_PROPERTY
           • MOCK_WALLET / MOCK_BANK => SIMULATED_PAID
                   ↓
           Booking vẫn PENDING
                   ↓
  Admin /admin → Xác nhận → CONFIRMED (ĐÃ XÁC NHẬN)
                   ↓
           Khách nhận thông báo
```

Có thể hủy đơn trước ngày nhận phòng, kể cả đã thanh toán demo. Khi hủy, thanh toán demo đổi sang SIMULATED_REFUNDED (chỉ ghi nhận hoàn tiền giả lập), còn DUE_AT_PROPERTY đổi sang CANCELLED.
Trạng thái Payment không được nhầm với BookingStatus.

## Vị trí mã nguồn

- `domain/Booking.java`: validate chuyển trạng thái `PENDING -> CONFIRMED`.
- `service/BookingService.java`: `confirmByAdmin(id)` khóa booking và phát sự kiện Observer.
- `pattern/facade/BookingFacade.java`: hàm facade chuyển tiếp đến dịch vụ duyệt đơn.
- `web/AdminController.java`: `POST /admin/bookings/{id}/confirm`.
- `config/SecurityConfig.java`: toàn bộ `/admin/**` yêu cầu ROLE_ADMIN.
- `service/PaymentService.java`: chọn thanh toán không gọi `booking.confirm()`.
- `templates/admin.html`, `bookings.html`, `confirmation.html`, `checkout.html`: giao diện thống nhất trạng thái.

## Demo hai trình duyệt

1. Trình duyệt A, đăng nhập `demo` / `Demo@12345`.
2. Chọn phòng trống trong tương lai, đặt phòng, chọn **Thanh toán tại khách sạn**.
3. Truy cập **Đơn đặt phòng**: đơn hiển thị **Chờ xác nhận**.
4. Trình duyệt B hoặc cửa sổ ẩn danh, đăng nhập `admin` / `Admin@12345`.
5. Vào `/admin`, tìm đơn và bấm **✓ Xác nhận**.
6. Quay lại trình duyệt A, tải lại `/bookings`: đơn hiển thị **Đã xác nhận**.
7. Khách vào `/notifications`: thấy thông báo admin xác nhận.
8. Kiểm tra khách không có quyền truy cập `/admin` hay POST `/admin/bookings/{id}/confirm`.

## Xác minh trước khi bàn giao

```powershell
mvn clean test
mvn spring-boot:run
```

Nếu chạy bằng MySQL có dữ liệu cũ: **sao lưu dữ liệu trước**. Những booking đã `CONFIRMED` từ bản cũ không tự chuyển về `PENDING`. Nếu muốn demo quy trình từ đầu, sử dụng profile H2 mặc định để seed dữ liệu mới.
