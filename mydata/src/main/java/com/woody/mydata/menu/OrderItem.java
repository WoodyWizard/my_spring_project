package com.woody.mydata.menu;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "item_entity")
public abstract class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
