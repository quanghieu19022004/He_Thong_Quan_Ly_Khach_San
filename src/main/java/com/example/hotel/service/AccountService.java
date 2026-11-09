package com.example.hotel.service;
import com.example.hotel.domain.*;import com.example.hotel.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Service
public class AccountService {
 private final UserRepository users;private final PasswordEncoder encoder;
 public AccountService(UserRepository users,PasswordEncoder encoder){this.users=users;this.encoder=encoder;}
 public AppUser findProfile(String username){return users.findByUsername(username).orElseThrow(()->new IllegalArgumentException("Không tìm thấy tài khoản"));}
 public void register(String username,String email,String password){
  if(username==null||!username.matches("[A-Za-z0-9_]{3,30}"))throw new IllegalArgumentException("Tên đăng nhập: 3–30 ký tự chữ, số hoặc _");
  if(email==null||!email.matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}"))throw new IllegalArgumentException("Email không hợp lệ");
  if(password==null||password.length()<8)throw new IllegalArgumentException("Mật khẩu phải có ít nhất 8 ký tự");
  if(users.existsByUsername(username)||users.existsByEmail(email))throw new IllegalArgumentException("Tài khoản hoặc email đã tồn tại");
  users.save(new AppUser(username,email,encoder.encode(password),Role.CUSTOMER));
 }
}
