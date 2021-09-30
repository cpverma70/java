package com.demo.persistance;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.demo.domain.Booking;

@Repository
public interface BookingRepository  extends  CrudRepository<Booking, Long> {
}
