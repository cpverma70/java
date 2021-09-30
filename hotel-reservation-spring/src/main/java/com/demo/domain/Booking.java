package com.demo.domain;

import javax.persistence.*;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Booking {
    public static final double TAX_AMOUNT = 0.10;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private UUID reservationId = UUID.randomUUID();

    @OneToOne(mappedBy = "reservation")
    private Room room;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "reservation_guests",
            joinColumns = @JoinColumn(name = "reservation_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "guest_id", referencedColumnName = "id")
    )
    private Set<User> guests = new HashSet<>();

    @Embedded
    @Valid
    private Dates dates = new Dates();

 
  

    @Column(nullable = false)
    private LocalDateTime createdTime;

    /**
     * @return The time this {@code Reservation} was successfully paid for and persisted.
     */
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public Booking() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }



    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    /**
     * Use the utility functions add/remove guest to perform changes.
     *
     * @return The unmodifiable set of {@code Guest}s.
     */
    public Set<User> getGuests() {
        return Collections.unmodifiableSet(guests);
    }

    /**
     * Add a guest only if the room has free beds.
     *
     * @param guest The guest to add.
     */
    public void addGuest(User guest) {
        if (!isRoomFull()) {
            guests.add(guest);
        }
    }

    public void clearGuests() {
        guests.clear();
    }

    /**
     * Allows UI to easily remove a {@code Guest} in the 'add guest' page. Its easier for the UI to 'POST' a guest id
     * rather than provide the full {@code Guest} details that match {@code Guest.equals/hashCode}.
     *
     * @param guestId The temporarily assigned guest id.
     * @return {@code true} if the {@code Guest} was removed otherwise {@code false}.
     */
    public boolean removeGuestById(UUID guestId) {
        return guests.removeIf(guest -> guest.getTempId().equals(guestId));
    }

    public UUID getReservationId() {
        return reservationId;
    }

   

    public Dates getDates() {
        return dates;
    }

    public void setDates(Dates dates) {
        this.dates = dates;
    }

    public boolean isRoomFull() {
        return guests.size() >= room.getBeds();
    }

    public boolean hasGuests() {
        return !guests.isEmpty();
    }

    public boolean hasAtLeastOneAdultGuest() {
        return guests.stream().anyMatch(guest -> !guest.isChild());
    }

    /**
     * Calculates {@code Extra.Type} to correctly charge the food and general extras.
     *
     * @return Depending on the room type, return {@code Extra.Type.Premium/Basic}.
     */
    public Extra.Type getExtraPricingType() {
        switch (room.getRoomType()) {
            case Luxury:
            case Business:
                return Extra.Type.Premium;
            default:
                return Extra.Type.Basic;
        }
    }

    /**
     * Calculates the chargeable late fee price only if the user has selected the late checkout option.
     */
    public BigDecimal getChargeableLateCheckoutFee() {
        return dates.isLateCheckout() ? getLateCheckoutFee() : BigDecimal.ZERO;
    }

    /**
     * The late checkout fee depending on the type of room.
     * For the actual chargeable fee, use {@link #getChargeableLateCheckoutFee()}
     */
    public BigDecimal getLateCheckoutFee() {
        switch (room.getRoomType()) {
            case Luxury:
            case Business:
                return BigDecimal.ZERO;
            default:
                return room.getHotel().getLateCheckoutFee();
        }
    }

    /**
     * No late fee is considered.
     * Provided separately to allow break down to sub totals on invoices.
     *
     * @return Total nights * per night cost
     */
    public BigDecimal getTotalRoomCost() {
        long nights = dates.totalNights();
        if (nights == 0) {
            return BigDecimal.ZERO;
        }
        return room.getCostPerNight().multiply(BigDecimal.valueOf(nights));
    }

    /**
     * Provided separately to allow break down to sub totals on invoices.
     *
     * @return {@link #getTotalRoomCost} + {@link #getChargeableLateCheckoutFee}
     */
    public BigDecimal getTotalRoomCostWithLateCheckoutFee() {
        return getTotalRoomCost().add(getChargeableLateCheckoutFee());
    }

   

    /**
     * Total cost including everything!
     * Provided separately to allow break down to sub totals on invoices.
     */
    public BigDecimal getTotalCostExcludingTax() {
        return getTotalRoomCostWithLateCheckoutFee();
    }

    /**
     * Provided separately to allow break down to sub totals on invoices.
     *
     * @return The taxable amount from the total cost. Eg 10% of $100 = $10.
     */
    public BigDecimal getTaxableAmount() {
        return getTotalCostExcludingTax().multiply(BigDecimal.valueOf(TAX_AMOUNT));
    }

    /**
     * Provided separately to allow break down to sub totals on invoices.
     *
     * @return The total cost including tax.
     */
    public BigDecimal getTotalCostIncludingTax() {
        return getTotalCostExcludingTax().add(getTaxableAmount());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking that = (Booking) o;
        return Objects.equals(reservationId, that.reservationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationId);
    }
}
