package com.demo.hotel;

import com.demo.domain.Hotel;
import com.demo.domain.Room;
import com.demo.domain.location.Address;
import com.demo.domain.location.Postcode;
import com.demo.domain.location.State;
import com.demo.persistance.HotelRepository;
import com.demo.persistance.predicates.RoomPredicates;
import com.demo.persistance.RoomRepository;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(BookingController.class)
@EnableSpringDataWebSupport
@ActiveProfiles("test")
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HotelRepository hotelRepository;

    @MockBean
    private RoomRepository roomRepository;

 


    @Test
    public void getAvailableHotelRooms_HotelIdNotFound_Throws404() throws Exception {
        long hotelId = 4;
        mockMvc.perform(get(String.format("/hotel/%d/rooms", hotelId)))
                .andExpect(status().isNotFound());
    }

    /**
     * Note: The model must contain the hotel so the UI can display detailed information.
     */
    @Test
    public void getAvailableHotelRooms_HotelHasAvailableRooms() throws Exception {
 

        // Dummy hotel to return in the mock.
        Address address = new Address("Xavier Hotel", "100 smith road", "",
                State.QLD, "Brisbane", new Postcode("4000"));
        Hotel hotel = new Hotel("Xavier Hotel", address, 4, "xavier@hotel.com");
        hotel.setId(3L);

        // Rather than recreate a new hotel room, setting total elements to 1 will achieve the same thing for testing.
//        PageImpl<Room> page = new PageImpl<>(List, PageRequest.of(0, 20), 1);
//        when(roomRepository.findAll()).thenReturn(page);

        when(hotelRepository.findById(hotel.getId())).thenReturn(Optional.of(hotel));

        mockMvc.perform(get(String.format("/hotel/%d/rooms", hotel.getId())))
                .andExpect(status().isOk())
                .andExpect(view().name("/hotel/rooms"))
                .andExpect(model().attribute("hotel", Matchers.isA(Hotel.class)));

        verify(roomRepository, times(1))
                .findAll();

        verify(hotelRepository, times(1)).findById(eq(hotel.getId()));
    }
}