# Kịch bản demo tại lớp (khuyến nghị 10–15 phút)

1. Vào `/rooms`, xem giá theo đêm, chọn ngày nhận và trả phòng hợp lệ, lọc số khách.
2. Khi chưa đăng nhập, thanh header hiển thị nút **Đăng nhập**.
3. Đăng nhập `demo / Demo@12345`, quan sát **avatar chữ D và tên demo** thay nút Đăng nhập.
4. Đặt phòng A101 (ngày tương lai, tránh ngày trùng), chọn chính sách `WEEKEND`. Hệ thống mở `/bookings/{id}/checkout`.
5. Kiểm tra trên trang checkout các mục: ngày ở, số đêm, giá cơ bản, phụ thu gói, tổng cộng.
6. Chọn **Thanh toán tại khách sạn**, xác nhận. Xem trang xác nhận: `CONFIRMED` nhưng `DUE_AT_PROPERTY` **chưa thanh toán**.
7. Vào `/bookings`, hủy đơn này trước ngày nhận: trạng thái hủy và không còn phải trả tại khách sạn.
8. Đặt phòng B201 khác ngày, chọn **Ví điện tử demo** hoặc **Chuyển khoản demo**, xác nhận. Màn hình ghi rõ đã **thanh toán mô phỏng** (không có giao dịch thật).
9. Thử lại hành động đặt cùng phòng, cùng ngày bị từ chối. Mở `/notifications` quan sát Observer.
10. Đăng xuất để kiểm chứng nút Đăng nhập trở lại; đăng nhập `admin / Admin@12345`, vào `/admin` thêm phòng D401 và khóa khai thác phòng.
11. Mở VS Code, trình bày 6 pattern theo mã nguồn và chạy `mvn clean test`; lưu ảnh kết quả thật nếu thành công.

## Lưu ý
- Đây là sản phẩm học tập; ảnh và mức giá phòng chỉ minh họa, không phải cơ sở lưu trú thật.
- Bản demo không kết nối ngân hàng/ví, không xử lý hoàn tiền, không tự hết hạn đơn chờ.
- Ngày sử dụng phải là ngày tương lai theo máy đang chạy.
- Mật khẩu demo phải thay trước khi triển khai công khai.
