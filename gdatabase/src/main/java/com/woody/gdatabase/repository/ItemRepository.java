package com.woody.gdatabase.repository;

import com.woody.mydata.menu.OrderItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<OrderItem, Long> {

    @Query(value = "SELECT * FROM item_entity ORDER BY RAND() LIMIT 4;", nativeQuery = true)
    List<OrderItem> findRandomFourEntities();

}
