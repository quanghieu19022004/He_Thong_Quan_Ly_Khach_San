# Nội dung bổ sung Chương 4 – phiên bản giao diện và thanh toán mới

## 4.x. Đồng bộ thanh điều hướng với phiên đăng nhập

Tạo fragment dùng chung `templates/fragments/layout.html`. `GlobalExceptionHandler.java` sử dụng `@ModelAttribute` để đưa `isLoggedIn`, `currentUsername`, `currentInitial`, `isAdmin` vào model. Người dùng chưa đăng nhập thấy liên kết đăng ký và đăng nhập. Người dùng đã đăng nhập thấy avatar ký tự đầu của username, menu Hồ sơ, Đơn đặt phòng, Đăng xuất; quản trị viên có liên kết Quản trị. Mọi giá trị tên hiển thị qua `th:text` để được escape HTML.

## 4.x. Quy trình thanh toán có bước xem lại

Sau khi đặt, controller chuyển đến `/bookings/{id}/checkout`. Trang này có lịch lưu trú, số đêm, giá cơ bản, phụ thu từ `Strategy`, tổng tiền, quy định hủy và các cách thanh toán. Chủ sở hữu đơn được xác minh trước khi hiển thị hoặc xử lý, trạng thái đơn phải là `PENDING`.

## 4.x. Cập nhật Adapter và trạng thái thanh toán

`PaymentMethod`: CASH (trả tại khách sạn), MOCK_WALLET (ví DEMO), MOCK_BANK (chuyển khoản DEMO). `PaymentGatewayRouter` phân phối qua interface `PaymentGateway`; class mới `BankTransferDemoGateway`. `PaymentStatus`: DUE_AT_PROPERTY (chưa thu tiền), SIMULATED_PAID (thanh toán giả lập), CANCELLED. `PaymentService.pay()` gắn trạng thái tương ứng, không đánh dấu CASH là đã thanh toán. Giao dịch online không liên kết ngân hàng/ ví điện tử thật. Đơn được xác nhận sau khi chọn phương thức, nhưng tình trạng thu tiền được hiển thị độc lập.

## 4.x. Giao diện xác nhận

`/bookings/{id}/confirmation` ghi mã đơn, phòng, ngày lưu trú, số khách, hình thức thanh toán, trạng thái thanh toán và mã tham chiếu demo. Trang này không phải hóa đơn thu tiền hợp lệ.

## 4.x. Kịch bản kiểm thử cập nhật

- Đăng nhập tài khoản demo: header không còn nút Đăng nhập, thay avatar và username.
- Đăng xuất: username biến mất, nút Đăng nhập xuất hiện.
- Đặt phòng hợp lệ: chuyển đến checkout, hiển thị tổng tiền và số đêm.
- Chọn CASH: đơn CONFIRMED, PaymentStatus DUE_AT_PROPERTY, `paidAt` null.
- Chọn MOCK_WALLET: đơn CONFIRMED, PaymentStatus SIMULATED_PAID, mã `DEMO-...`.
- Chọn MOCK_BANK: tương tự, mã `DEMO-BANK-...`.
- Người dùng B không thể xem/thanh toán đơn của người dùng A.
- Đơn đã trả tại khách sạn có thể hủy trước ngày nhận; đơn mô phỏng đã trả bị chặn hủy tự động (chưa có quy trình hoàn tiền).
- Thử đăng ký, kiểm tra giá gói cuối tuần và thử đặt trùng ngày.

Các kịch bản này chỉ được đánh dấu Pass khi thực sự chạy trên máy của sinh viên.


## Bản thay đổi giao diện theo luồng đặt phòng thực tế

- Thêm `room-detail.html` và `site.js`, cập nhật `rooms.html`, `fragments/layout.html`, `style.css`.
- `PublicController` bổ sung bộ lọc loại phòng, giá, sắp xếp và trang chi tiết phòng.
- `SecurityConfig` cho phép khách chưa đăng nhập xem chi tiết.
- Dữ liệu demo tăng số lượng phòng lên 9 cho H2 lần khởi tạo mới.
- Code nghiệp vụ 6 Design Patterns tiếp tục được dùng trong các service hiện có.
- Ảnh phòng là minh họa, các giao dịch thanh toán trực tuyến vẫn là giả lập.


## Cập nhật nghiệp vụ: Admin xác nhận đơn (22/10/2026)

- Trạng thái khi tạo: **PENDING / Chờ xác nhận**.
- Trạng thái thanh toán lưu độc lập: **DUE_AT_PROPERTY** hoặc **SIMULATED_PAID** (giao dịch mô phỏng).
- Chọn phương thức thanh toán không tự cập nhật `BookingStatus.CONFIRMED`.
- Duy nhất chức năng phê duyệt ở `/admin/bookings/{id}/confirm` (chỉ ADMIN) được chuyển PENDING → CONFIRMED.
- Mọi thay đổi trạng thái phát thông báo qua Observer đến khách hàng.
- Khách xem mục Đơn đặt phòng và trạng thái Chờ xác nhận/Đã xác nhận; Admin xem danh sách và nút Xác nhận.
- Có thêm unit test `AdminBookingApprovalTests` và test kiểm soát quyền trong `WebSecurityTests`.
- Báo cáo Word cũ trong thư mục `Bao_cao` chưa được biên tập theo luồng duyệt mới; cần cập nhật khi nộp bản chính thức.
