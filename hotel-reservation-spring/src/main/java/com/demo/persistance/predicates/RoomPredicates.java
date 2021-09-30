package com.demo.persistance.predicates;

import com.demo.domain.QRoom;
import com.querydsl.core.types.Predicate;

public final class RoomPredicates {

    private static final QRoom room = QRoom.room;

    private RoomPredicates() {
    }

    /**
     * Gets all the available rooms in the hotel identified by the supplied {@code hotelId}.
     * An available room means it has no {@code Reservation} assigned to it.
     */
    public static Predicate availableRoom(Long hotelId) {
        return room.hotel.id.eq(hotelId).and(room.reservation.isNull());
    }
}
