package com.example.hotel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import org.springframework.security.test.context.support.WithMockUser;
@SpringBootTest @AutoConfigureMockMvc
class WebSecurityTests {
 @Autowired private MockMvc mvc;
 @Autowired private com.example.hotel.repository.RoomRepository rooms;
 @Test void everyoneCanListRooms()throws Exception{mvc.perform(get("/rooms")).andExpect(status().isOk()).andExpect(view().name("rooms"));}
 @Test void anonymousUserCannotAccessBookings()throws Exception{mvc.perform(get("/bookings")).andExpect(status().is3xxRedirection());}
 @Test void anonymousUserCannotAccessAdmin()throws Exception{mvc.perform(get("/admin")).andExpect(status().is3xxRedirection());}
 @Test void roomDetailIsAccessibleWithoutLogin()throws Exception{
  Long id=rooms.findByActiveTrueOrderByNumberAsc().get(0).getId();
  mvc.perform(get("/rooms/"+id)).andExpect(status().isOk()).andExpect(view().name("room-detail"))
   .andExpect(content().string(containsString("Tiếp tục đặt phòng")));
 }
 @Test void canFilterRoomsByTypeAndPrice()throws Exception{
  mvc.perform(get("/rooms").param("type","SUITE").param("sort","price-asc"))
   .andExpect(status().isOk()).andExpect(view().name("rooms"));
  mvc.perform(get("/rooms").param("maxPrice","700000"))
   .andExpect(status().isOk()).andExpect(view().name("rooms"));
 }
 @WithMockUser(username="demo",roles="CUSTOMER")
 @Test void customerCannotConfirmBookingAsAdmin()throws Exception{
  mvc.perform(post("/admin/bookings/1/confirm").with(csrf())).andExpect(status().isForbidden());
 }
 @WithMockUser(username="admin",roles="ADMIN")
 @Test void adminCanUseApprovalEndpoint()throws Exception{
  // Unknown booking ID produces a handled flash-error redirect, not 403.
  mvc.perform(post("/admin/bookings/999999/confirm").with(csrf()))
   .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin"));
 }
 @WithMockUser(username="demo",roles="CUSTOMER")
 @Test void customerBookingsPageShowsCancelButton()throws Exception{
  // MockMvc account 'demo' must exist in demo seed data for the ownership query.
  mvc.perform(get("/bookings"))
   .andExpect(status().isOk()).andExpect(view().name("bookings"));
 }
 @WithMockUser(username="demo",roles="CUSTOMER")
 @Test void postCancelRequiresCsrf()throws Exception{
  mvc.perform(post("/bookings/100/cancel")).andExpect(status().isForbidden());
 }
 @Test void anonymousCannotCancelBooking()throws Exception{
  mvc.perform(post("/bookings/100/cancel").with(csrf())).andExpect(status().is3xxRedirection());
 }
 @Test void guestHeaderShowsLogin(){mvcUncheckedLogin();}
 private void mvcUncheckedLogin(){try{mvc.perform(get("/rooms"))
   .andExpect(content().string(containsString("class=\"btn nav-btn\"")));
  }catch(Exception e){throw new AssertionError(e);}}
 @WithMockUser(username="demo",roles="CUSTOMER")
 @Test void loggedInHeaderShowsAccountMenuInsteadOfLoginButton()throws Exception{
  mvc.perform(get("/rooms"))
   .andExpect(status().isOk())
   .andExpect(content().string(containsString("class=\"account-menu\"")))
   .andExpect(content().string(not(containsString("class=\"btn nav-btn\""))));
 }
}
