package com.woody.gdatabase.repository;

import com.woody.mydata.Deliverer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DelivererRepository extends JpaRepository<Deliverer, Long> {
}
