package com.demo.hotel;

	import java.time.LocalDate;
	import java.util.ArrayList;
	import java.util.List;
	import java.util.Map;
	import java.util.Optional;
	
	import org.springframework.http.ResponseEntity;
	import org.springframework.web.bind.annotation.GetMapping;
	import org.springframework.web.bind.annotation.ModelAttribute;
	import org.springframework.web.bind.annotation.PathVariable;
	import org.springframework.web.bind.annotation.PostMapping;
	import org.springframework.web.bind.annotation.RequestBody;
	import org.springframework.web.bind.annotation.RequestMapping;
	import org.springframework.web.bind.annotation.RequestParam;
	import org.springframework.web.bind.annotation.RestController;
	import com.demo.TimeProvider;
	import com.demo.domain.Booking;
import com.demo.domain.Dates;
import com.demo.domain.Room;
	import com.demo.dto.RoomDTO;
	import com.demo.exceptions.NotFoundException;
	import com.demo.persistance.BookingRepository;
	import com.demo.persistance.RoomRepository;


	  @RequestMapping(value = "/booking")
	  @RestController
	  public class BookingController {

		  private RoomRepository roomRepository;
		  private BookingRepository bookingRepository;
		  private TimeProvider timeProvider;

		    public BookingController(RoomRepository roomRepository,BookingRepository bookingRepository,
		                                 TimeProvider timeProvider) {
		        this.roomRepository = roomRepository;
		        this.bookingRepository = bookingRepository;
		        this.timeProvider = timeProvider;
		    }

		  //below method is used for specified room booking
		  @PostMapping(value = "/createRoomBooking")
		  public ResponseEntity createRoomBooking(@RequestBody Booking booking) {				
			 Optional<Room> existingRoom =  roomRepository.findById(booking.getRoom().getId());
			   if (existingRoom.isPresent() ) {			    	  
			     bookingRepository.save(booking);		    	
			       return ResponseEntity.ok(existingRoom.get()); 
			     } else {
			       return ResponseEntity.notFound().build();  
			    }			      						
			}
		
			//below method is used for random rooms booking on random dates
			@PostMapping(value = "/createRandomBooking")
			public ResponseEntity createRandomBooking(@RequestBody Booking booking) {		
				   List<Room> existingRooms = (List<Room>) roomRepository.findAll();
			       if (!existingRooms.isEmpty()) {
			    	   Optional<Room> existingRoom = existingRooms.parallelStream().findFirst();
			    	   booking.setRoom(existingRoom.get());
			    	   Dates reservationDatesA = new Dates();
			    	   booking.setDates(reservationDatesA);		    	   
			    	   bookingRepository.save(booking);
			    	
			    		return ResponseEntity.ok(existingRoom.get()); 
			      } else {
			    	  return ResponseEntity.notFound().build();  
			      }		      	
				
			}
	
			//An API to fetch all booked room
			@GetMapping("/rooms")
			public ResponseEntity getBookedRoom() {			
				List<Booking> bookings = (List<Booking>) bookingRepository.findAll();				
				List<Room> rooms= new ArrayList();			
				bookings.parallelStream().forEach(booking-> rooms.add(booking.getRoom()));
				
				 return ResponseEntity.ok().body(rooms);
				 
			}
			
//			public int getRandomNumber(int min, int max) {
//			    return (int) ((Math.random() * (max - min)) + min);
//			}	
			
	  }
