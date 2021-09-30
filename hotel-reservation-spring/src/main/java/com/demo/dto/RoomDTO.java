package com.demo.dto;

import java.math.BigDecimal;

import org.springframework.beans.BeanUtils;

import com.demo.domain.Booking;
import com.demo.domain.Hotel;
import com.demo.domain.Room;
import com.demo.domain.RoomType;


public class RoomDTO {

	    private Long id;

	    private Hotel hotel;

	    private String roomNumber;

	    private RoomType roomType;

	    private int beds;

	    private BigDecimal costPerNight;

	    private Booking reservation;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Hotel getHotel() {
			return hotel;
		}

		public void setHotel(Hotel hotel) {
			this.hotel = hotel;
		}

		public String getRoomNumber() {
			return roomNumber;
		}

		public void setRoomNumber(String roomNumber) {
			this.roomNumber = roomNumber;
		}

		public RoomType getRoomType() {
			return roomType;
		}

		public void setRoomType(RoomType roomType) {
			this.roomType = roomType;
		}

		public int getBeds() {
			return beds;
		}

		public void setBeds(int beds) {
			this.beds = beds;
		}

		public BigDecimal getCostPerNight() {
			return costPerNight;
		}

		public void setCostPerNight(BigDecimal costPerNight) {
			this.costPerNight = costPerNight;
		}

		public Booking getReservation() {
			return reservation;
		}

		public void setReservation(Booking reservation) {
			this.reservation = reservation;
		}
	    
		
		public Room getRoom(RoomDTO dto) {
			
			if (dto.getBeds() !=0) {
				Room room = new Room();
			   BeanUtils.copyProperties(dto, room);
			 return room;
			}
			
			return null;
		}
	    
     public Room copyRoom(RoomDTO dto, Room room) {
			
			if (dto.getBeds() !=0) {
			   BeanUtils.copyProperties(dto, room);
			 return room;
			}
			
			return null;
		}
	    

}
