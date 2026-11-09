# ĐỐI CHIẾU LUỒNG VỚI WEBSITE ĐẶT PHÒNG THỰC TẾ

## Các nguồn tham khảo công khai

- Booking.com, trang trợ giúp thanh toán, chính sách hủy và thuế/phụ phí: https://secure.booking.com/faq.vi.html
- Booking.com Demand API, hướng dẫn mô tả các cách thanh toán, thời điểm trả tiền và mức phí tại bước xác nhận: https://developers.booking.com/demand/docs/payments/payments-display
- Agoda, Điều khoản dịch vụ, mô tả Book Now Pay Later và Pay at Hotel: https://www.agoda.com/info/termsofuse.html

## Yêu cầu UX đã áp dụng cho website mô phỏng

| Nội dung | Điểm tham khảo | Đã xây dựng |
|---|---|---|
| Trước khi trả tiền | Xem ngày, số đêm, loại phòng, khách, chính sách giá | Trang `/bookings/{id}/checkout` |
| Phân biệt thời điểm thu tiền | Trả ngay hoặc trả tại khách sạn phải rõ | Trả tại khách sạn không đánh dấu PAID |
| Tổng tiền minh bạch | Phân tách tiền phòng, phụ thu và tổng | Base + adjustment + tổng hiển thị trước nút xác nhận |
| Xác nhận lưu trú | Mã tham chiếu và hướng dẫn sau đặt phòng | `/bookings/{id}/confirmation` |
| Trạng thái tài khoản | Sau login hiển thị user và thao tác tài khoản | Avatar initials + menu đăng xuất; khách chưa login thấy Đăng nhập |
| Quản lý sau đặt phòng | Xem trạng thái và hủy theo chính sách | `/bookings` và thông báo trong ứng dụng |

**Phạm vi:** Đây là cách lấy cảm hứng từ luồng nghiệp vụ phổ biến, KHÔNG sao chép giao diện độc quyền, logo, nhãn hiệu hoặc kết nối với dịch vụ của Booking.com/Agoda. Quy trình thanh toán là **mô phỏng hoàn toàn**, không có đối tác hoặc nghiệp vụ nhận tiền thật.
