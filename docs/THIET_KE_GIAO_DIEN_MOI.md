# Giao diện Lumi Stay - phiên bản tham khảo quy trình OTA

## Trải nghiệm
1. Trang /rooms: hero ảnh, nhập ngày nhận/trả, số khách.
2. Bộ lọc: loại phòng, ngân sách, sắp xếp. Lọc chạy trên server (PublicController).
3. Thẻ phòng: ảnh, tên hạng phòng, tiện nghi, sức chứa, giá theo đêm.
4. Trang /rooms/{id}: gallery minh họa, điều cần biết, form chọn ngày/khách/gói giá.
5. Tiếp tục đặt phòng -> /bookings/{id}/checkout -> /confirmation.

## Đối chiếu
Nghiên cứu cách Traveloka tổ chức thao tác và thông tin; thiết kế này chủ ý dùng thương hiệu Lumi Stay, hệ màu riêng, nội dung độc lập, không mạo nhận liên kết với Traveloka. Không dùng đánh giá giả hoặc flash-sale không có cơ sở.

## Thiết kế và code
- `templates/rooms.html`, `templates/room-detail.html`: Thymeleaf.
- `templates/fragments/layout.html`: nav và footer thống nhất theo tài khoản.
- `static/css/style.css`: bản thiết kế responsive.
- `static/js/site.js`: chọn ngày và tính giá tham khảo; validation nghiệp vụ và chốt giá ở Java backend.
- `web/PublicController.java`: tìm kiếm, bộ lọc, chi tiết.
- `service/RoomService.java`: tìm phòng và xác minh phòng mở bán.
- `config/SecurityConfig.java`: cho phép xem chi tiết phòng khi chưa đăng nhập.

## Giới hạn
Ảnh được phục vụ từ Unsplash CDN, có thể không hiển thị nếu máy không có mạng. Giá/phòng minh họa trong H2, không phải sản phẩm của khách sạn thật. Thanh toán giả lập.
