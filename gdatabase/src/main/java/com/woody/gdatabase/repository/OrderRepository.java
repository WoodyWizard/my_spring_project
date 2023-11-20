package com.woody.gdatabase.repository;

import com.woody.mydata.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
