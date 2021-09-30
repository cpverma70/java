package com.demo.hotel;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.domain.Room;
import com.demo.persistance.RoomRepository;

@RequestMapping(value = "/users")
@RestController
public class UserController {

	  private RoomRepository roomRepository;

	  public UserController(RoomRepository roomRepository) {
	        this.roomRepository = roomRepository;
	   }
	  
	  private static final Logger log = LoggerFactory.getLogger("UserController");
	  
	 @GetMapping("/allRoom")
	  List<Room> all() {
	    return (List<Room>) roomRepository.findAll();
	  }
	 
}
