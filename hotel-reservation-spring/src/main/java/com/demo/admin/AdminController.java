package com.demo.admin;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.demo.domain.Hotel;
import com.demo.domain.Room;
import com.demo.dto.RoomDTO;
import com.demo.exceptions.NotFoundException;
import com.demo.persistance.RoomRepository;
import com.demo.persistance.predicates.RoomPredicates;

  @RequestMapping(value = "/admin")
  @RestController
  public class AdminController {

	  private RoomRepository roomRepository;

	  public AdminController(RoomRepository roomRepository) {
	        this.roomRepository = roomRepository;
	   }
	  
	  private static final Logger log = LoggerFactory.getLogger("AdminController");
		/*
		 * private Sort getSort(String sort) { switch (sort) { case "lowest": return new
		 * Sort(Sort.Direction.ASC, "price"); case "highest": return new
		 * Sort(Sort.Direction.DESC, "price"); case "name": return new
		 * Sort(Sort.Direction.ASC, "name"); default: return null; } }
		 */

		@PostMapping(value = "/createRoom")
		public ResponseEntity addRoom(@RequestBody RoomDTO roomDTO) {
			RoomDTO dto = new RoomDTO();
			Room room = dto.getRoom(roomDTO);
			
			if(room !=null ) {
				roomRepository.save(room);
			}
			return ResponseEntity.ok(room);
			
		}
	
		  @PutMapping("/{id}")
		  ResponseEntity updateRoom(@RequestBody RoomDTO newroom, @PathVariable Long id) {
			  RoomDTO dto = new RoomDTO();
				Room room = dto.getRoom(newroom);			
		     Optional<Room> existingRoom =  roomRepository.findById(id);
		      if (existingRoom.isPresent() ) {
		    	  room = dto.copyRoom(dto, existingRoom.get());
		    	  
		    		return ResponseEntity.ok(existingRoom.get()); 
		      }
		    return ResponseEntity.notFound().build(); 
		  }
		  
		  @DeleteMapping("/deleteRoom/{id}")
		  public ResponseEntity deleteRoom(@PathVariable Long id) {
			  roomRepository.deleteById(id);		
			  return ResponseEntity.ok().build();
		  }
		
		  @GetMapping("/getRoom/{id}")
		  Room one(@PathVariable Long id) throws NotFoundException {
		    
		    return roomRepository.findById(id)
		      .orElseThrow(() -> new NotFoundException(""+id));
		  }
		  	  
		  @GetMapping("/all")
		  List<Room> all() {
		    return (List<Room>) roomRepository.findAll();
		  }
		  
	

}
