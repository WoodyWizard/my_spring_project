package com.woody.delivery.repository;

import com.woody.mydata.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Order, Long> {
}
