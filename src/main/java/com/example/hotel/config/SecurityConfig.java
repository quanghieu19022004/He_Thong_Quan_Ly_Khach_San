package com.example.hotel.config;
import com.example.hotel.repository.UserRepository;
import org.springframework.context.annotation.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
@Configuration
public class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
 @Bean UserDetailsService userDetailsService(UserRepository repository){return username->{
  var user=repository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("Không tìm thấy tài khoản"));
  return User.withUsername(user.getUsername()).password(user.getPasswordHash())
   .authorities(new SimpleGrantedAuthority("ROLE_"+user.getRole().name())).build();
 };}
 @Bean SecurityFilterChain chain(HttpSecurity http)throws Exception{
  return http.authorizeHttpRequests(auth->auth
   .requestMatchers("/","/rooms","/rooms/**","/register","/css/**","/js/**","/error").permitAll()
   .requestMatchers("/admin/**").hasRole("ADMIN")
   .anyRequest().authenticated())
   .formLogin(form->form.loginPage("/login").defaultSuccessUrl("/rooms",true).permitAll())
   .logout(logout->logout.logoutSuccessUrl("/rooms").permitAll())
   .build();
 }
}
