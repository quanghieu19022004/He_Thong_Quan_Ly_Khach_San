package com.example.hotel.web;
import java.security.Principal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
@ControllerAdvice
public class GlobalExceptionHandler {
 @ModelAttribute public void navigation(Model model,Principal principal){
  boolean loggedIn=principal!=null&& !"anonymousUser".equals(principal.getName());
  String name=loggedIn?principal.getName():"";
  model.addAttribute("isLoggedIn",loggedIn);
  model.addAttribute("currentUsername",name);
  model.addAttribute("currentInitial",loggedIn?name.substring(0,1).toUpperCase(java.util.Locale.ROOT):"");
  boolean admin=loggedIn && SecurityContextHolder.getContext().getAuthentication()!=null &&
   SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(a->"ROLE_ADMIN".equals(a.getAuthority()));
  model.addAttribute("isAdmin",admin);
 }
 @ExceptionHandler(SecurityException.class)
 @ResponseStatus(HttpStatus.FORBIDDEN)
 @ResponseBody public String forbidden(SecurityException e){return "403 - Không có quyền thực hiện thao tác";}
}
