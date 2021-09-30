package com.demo.domain;

import com.demo.domain.location.Address;
import com.demo.domain.location.Postcode;
import com.demo.domain.location.State;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookingTest {

    private Room createRoom() {
        Address address = new Address("Royal Hotel", "166 Albert Road", null,
                State.VIC, "Melbourne", new Postcode("3000"));

        Room room = new Room("ABC123", RoomType.Economy, 2, BigDecimal.valueOf(25.50));
        room.setHotel(new Hotel("Royal Hotel", address, 4, "royal@hotel.com"));
        return room;
    }

    /**
     * Confirm isRoomFull determines a room is fool when its guest limit is reached.
     */
    @Test
    public void isRoomFull() {
        Room room = createRoom();
        room.setBeds(2);

        Booking reservation = new Booking();
        reservation.setRoom(room);

        assertThat(reservation.isRoomFull()).isFalse();
        reservation.addGuest(new User("john", "smith", false));
        assertThat(reservation.isRoomFull()).isFalse();
        reservation.addGuest(new User("marie", "smith", false));
        assertThat(reservation.isRoomFull()).isTrue();
    }

    @Test
    public void hasGuests() {
        Room room = createRoom();
        room.setBeds(2);

        Booking reservation = new Booking();
        reservation.setRoom(room);

        assertThat(reservation.hasGuests()).isFalse();

        reservation.addGuest(new User("marie", "smith", false));
        assertThat(reservation.hasGuests()).isTrue();
    }

    @Test
    public void hasAtLeastOneAdultGuest() {
        Room room = createRoom();
        room.setBeds(2);

        Booking reservation = new Booking();
        reservation.setRoom(room);

        assertThat(reservation.hasAtLeastOneAdultGuest()).isFalse();

        reservation.addGuest(new User("marie", "smith", true));

        assertThat(reservation.hasAtLeastOneAdultGuest()).isFalse();

        reservation.addGuest(new User("marie", "smith", false));

        assertThat(reservation.hasAtLeastOneAdultGuest()).isTrue();
    }

    /**
     * You should not be able to add a guest to a room that is already full.
     */
    @Test
    public void addGuest_OnlyWhenThereAreFreeBeds() {
        Room room = createRoom();
        room.setBeds(2);

        Booking reservation = new Booking();
        reservation.setRoom(room);

        User john = new User("john", "smith", false);
        User sara = new User("sara", "smith", true);
        reservation.addGuest(john);
        reservation.addGuest(sara);

        reservation.addGuest(new User("marie", "smith", false));
        reservation.addGuest(new User("ryan", "smith", false));

        assertThat(reservation.getGuests().size()).isEqualTo(2);
        assertThat(reservation.getGuests()).containsExactlyInAnyOrder(john, sara);
    }

    @Test
    public void removeGuestById_NoGuestExists_HasNoEffect() {
        Booking reservation = new Booking();

        boolean removed = reservation.removeGuestById(UUID.randomUUID());
        assertThat(removed).isFalse();
        assertThat(reservation.getGuests()).isEmpty();
    }

    @Test
    public void removeGuestById_GuestExists_GuestIsRemoved() {
        Booking reservation = new Booking();
        Room room = createRoom();
        room.setBeds(2);
        reservation.setRoom(room);

        User guestA = new User("john", "smith", false);
        User guestB = new User("nicole", "smith", false);

        reservation.addGuest(guestA);
        reservation.addGuest(guestB);
        assertThat(reservation.getGuests()).containsExactlyInAnyOrder(guestA, guestB);

        boolean removed = reservation.removeGuestById(guestA.getTempId());

        assertThat(removed).isTrue();
        assertThat(reservation.getGuests()).containsExactly(guestB);
    }

 
    /**
     * When the room is luxury, {@code Extra.Type.Premium} is the type to base food and general extras charging from.
     */
    @Test
    public void getExtraPricingType_LuxuryRoomIsPremium() {
        Booking reservation = new Booking();
        Room room = createRoom();
        room.setRoomType(RoomType.Luxury);

        reservation.setRoom(room);

        assertThat(reservation.getExtraPricingType()).isEqualTo(Extra.Type.Premium);
    }

    /**
     * When the room is business, {@code Extra.Type.Premium} is the type to base food and general extras charging from.
     */
    @Test
    public void getExtraPricingType_BusinessRoomIsPremium() {
        Booking reservation = new Booking();
        Room room = createRoom();
        room.setRoomType(RoomType.Business);

        reservation.setRoom(room);

        assertThat(reservation.getExtraPricingType()).isEqualTo(Extra.Type.Premium);
    }

    /**
     * When the room is Balcony, {@code Extra.Type.Basic} is the type to base food and general extras charging from.
     */
    @Test
    public void getExtraPricingType_BalconyRoomIsBasic() {
        Booking reservation = new Booking();
        Room room = createRoom();
        room.setRoomType(RoomType.Balcony);

        reservation.setRoom(room);

        assertThat(reservation.getExtraPricingType()).isEqualTo(Extra.Type.Basic);
    }

    /**
     * When the room is Economy, {@code Extra.Type.Basic} is the type to base food and general extras charging from.
     */
    @Test
    public void getExtraPricingType_EconomyRoomIsBasic() {
        Booking reservation = new Booking();
        Room room = createRoom();
        room.setRoomType(RoomType.Economy);

        reservation.setRoom(room);

        assertThat(reservation.getExtraPricingType()).isEqualTo(Extra.Type.Basic);
    }

  

    /**
     * When a customer chooses the late checkout option, the late fee should be returned.
     */
    @Test
    public void getChargeableLateCheckoutFee_WhenLateCheckout_ChargeFee() {
        BigDecimal lateCheckoutFee = BigDecimal.valueOf(20.50);

        Room room = createRoom();
        room.getHotel().setLateCheckoutFee(lateCheckoutFee);
        Booking reservation = new Booking();
        reservation.setRoom(room);

        // enable late checkout
        reservation.getDates().setLateCheckout(true);

        assertThat(reservation.getChargeableLateCheckoutFee()).isEqualTo(lateCheckoutFee);
    }

    /**
     * When a customer does not select late checkout, the chargeable late fee is $0.00.
     */
    @Test
    public void getChargeableLateCheckoutFee_WhenNoLateCheckout_NoCharge() {
        BigDecimal lateCheckoutFee = BigDecimal.valueOf(20.50);

        Room room = createRoom();
        room.getHotel().setLateCheckoutFee(lateCheckoutFee);
        Booking reservation = new Booking();
        reservation.setRoom(room);

        // no late checkout = $0.00
        reservation.getDates().setLateCheckout(false);

        assertThat(reservation.getChargeableLateCheckoutFee()).isEqualTo(BigDecimal.ZERO);
    }

    /**
     * When a customer selects late checkout but have a {@code RoomType.Luxury} room, the late checkout fee is waived and
     * should return $0.00.
     */
    @Test
    public void getLateCheckoutFee_WhenLuxuryRoomType_NoLateCharge() {
        BigDecimal lateCheckoutFee = BigDecimal.valueOf(20.50);

        Room room = createRoom();
        room.setRoomType(RoomType.Luxury);
        room.getHotel().setLateCheckoutFee(lateCheckoutFee);

        Booking reservation = new Booking();
        reservation.setRoom(room);
        reservation.getDates().setLateCheckout(true);

        assertThat(reservation.getLateCheckoutFee()).isEqualTo(BigDecimal.ZERO);
    }

    /**
     * When a customer selects late checkout but have a {@code RoomType.Business}, the late checkout fee is waived and
     * should return $0.00.
     */
    @Test
    public void getLateCheckoutFee_WhenBusinessRoomType_NoLateCharge() {
        BigDecimal lateCheckoutFee = BigDecimal.valueOf(20.50);

        Room room = createRoom();
        room.setRoomType(RoomType.Business);
        room.getHotel().setLateCheckoutFee(lateCheckoutFee);

        Booking reservation = new Booking();
        reservation.setRoom(room);
        reservation.getDates().setLateCheckout(true);

        assertThat(reservation.getLateCheckoutFee()).isEqualTo(BigDecimal.ZERO);
    }

    /**
     * When a customer selects late checkout but have a {@code RoomType.Economy}, the late checkout fee is applied.
     */
    @Test
    public void getLateCheckoutFee_WhenEconomyRoomType_ApplyLateCharge() {
        BigDecimal lateCheckoutFee = BigDecimal.valueOf(20.50);

        Room room = createRoom();
        room.setRoomType(RoomType.Economy);
        room.getHotel().setLateCheckoutFee(lateCheckoutFee);

        Booking reservation = new Booking();
        reservation.setRoom(room);
        reservation.getDates().setLateCheckout(true);

        assertThat(reservation.getLateCheckoutFee()).isEqualTo(lateCheckoutFee);
    }

    /**
     * When a customer selects late checkout but have a {@code RoomType.Balcony}, the late checkout fee is applied.
     */
    @Test
    public void getLateCheckoutFee_WhenBalconyRoomType_ApplyLateCharge() {
        BigDecimal lateCheckoutFee = BigDecimal.valueOf(20.50);

        Room room = createRoom();
        room.setRoomType(RoomType.Balcony);
        room.getHotel().setLateCheckoutFee(lateCheckoutFee);

        Booking reservation = new Booking();
        reservation.setRoom(room);
        reservation.getDates().setLateCheckout(true);

        assertThat(reservation.getLateCheckoutFee()).isEqualTo(lateCheckoutFee);
    }

    /**
     * If the reservation was for 0 nights, the cost should be 0.
     * Technically this won't occur since the business rule of at least 1 night is validated, however is
     * tested as a sanity check to ensure room cost is being calculated correctly.
     */
    @Test
    public void getTotalRoomCost_ZeroNights_NoCost() {
        Room room = createRoom();
        room.setRoomType(RoomType.Economy);
        room.getHotel().setLateCheckoutFee(BigDecimal.valueOf(20.50));

        BigDecimal costPerNight = BigDecimal.valueOf(23.80);
        room.setCostPerNight(costPerNight);

        Booking reservation = new Booking();
        reservation.setRoom(room);

        reservation.getDates().setCheckInDate(LocalDate.of(2018, 1, 1));
        reservation.getDates().setCheckOutDate(LocalDate.of(2018, 1, 1));

        assertThat(reservation.getTotalRoomCost()).isEqualTo(BigDecimal.ZERO);
    }

    /**
     * A room with an active late fee should not be considered in the total room cost calculation.
     */
    @Test
    public void getTotalRoomCost_CalculatesCorrectCost() {
        Room room = createRoom();
        room.setRoomType(RoomType.Economy);
        room.getHotel().setLateCheckoutFee(BigDecimal.valueOf(20.50));

        BigDecimal costPerNight = BigDecimal.valueOf(23.80);
        room.setCostPerNight(costPerNight);

        Booking reservation = new Booking();
        reservation.setRoom(room);

        reservation.getDates().setCheckInDate(LocalDate.of(2018, 1, 1));
        reservation.getDates().setCheckOutDate(LocalDate.of(2018, 1, 4));

        // expected cost for 3 nights, note how no late fee is considered for this calculation.
        BigDecimal expectedCost = costPerNight.multiply(BigDecimal.valueOf(3));

        assertThat(reservation.getTotalRoomCost()).isEqualTo(expectedCost);
    }

    /**
     * When the late checkout is NOT enabled and the room type does not waive the late checkout fee,
     * the late checkout fee should NOT be included in the total room cost.
     */
    @Test
    public void getTotalRoomCostWithLateCheckoutFee_NoCheckoutFee_RoomCostOnly() {
        Room room = createRoom();
        room.setRoomType(RoomType.Economy);
        room.getHotel().setLateCheckoutFee(BigDecimal.valueOf(20.50));

        BigDecimal costPerNight = BigDecimal.valueOf(23.80);
        room.setCostPerNight(costPerNight);

        Booking reservation = new Booking();
        reservation.setRoom(room);
        reservation.getDates().setLateCheckout(false);

        reservation.getDates().setCheckInDate(LocalDate.of(2018, 1, 1));
        reservation.getDates().setCheckOutDate(LocalDate.of(2018, 1, 4));

        // expected cost for 3 nights
        BigDecimal expectedCost = costPerNight.multiply(BigDecimal.valueOf(3));

        assertThat(reservation.getTotalRoomCostWithLateCheckoutFee()).isEqualTo(expectedCost);
    }

    /**
     * When the late checkout is enabled and the room type does not waive the late checkout fee,
     * the late checkout fee should be included in the total room cost.
     */
    @Test
    public void getTotalRoomCostWithLateCheckoutFee_WithLateCheckoutFee_CorrectCost() {
        Room room = createRoom();
        room.setRoomType(RoomType.Economy);
        BigDecimal lateCheckoutFee = BigDecimal.valueOf(20.50);
        room.getHotel().setLateCheckoutFee(lateCheckoutFee);

        BigDecimal costPerNight = BigDecimal.valueOf(23.80);
        room.setCostPerNight(costPerNight);

        Booking reservation = new Booking();
        reservation.setRoom(room);
        reservation.getDates().setLateCheckout(true);

        reservation.getDates().setCheckInDate(LocalDate.of(2018, 1, 1));
        reservation.getDates().setCheckOutDate(LocalDate.of(2018, 1, 4));

        // expected cost for 3 nights + late fee
        BigDecimal expectedCost = costPerNight.multiply(BigDecimal.valueOf(3)).add(lateCheckoutFee);

        assertThat(reservation.getTotalRoomCostWithLateCheckoutFee()).isEqualTo(expectedCost);
    }

  

}