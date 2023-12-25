package com.woody.mydata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.woody.mydata.menu.OrderItem;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_entity")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    private User customer;


    @ManyToOne
    @JoinColumn(name = "deliverer_id")
    @JsonIgnore
    private Deliverer deliverer;

    @NotBlank
    @NotNull
    private String address;

    private String status;

    private String paymentMethod;

    private String paymentStatus;

    @NotNull
    private Double total;

    @NotNull
    private Double discount;

    private Double shipping;

    private Boolean deliverChecker = false;

    private Boolean clientChecker = false;

    //@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "order_items",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "order_item_id")
    )
    private List<OrderItem> items = new ArrayList<>();

    public Boolean isValid() {
        if (items.size() == 0 && customer == null && address == null && status == null && total == null && discount == null && shipping == null) {
            return false;
        }
        return true;
    }

    public Boolean isPaid() {
        return paymentStatus.equals("Paid");
    }

    public Boolean isDelivered() {
        return deliverChecker;
    }

    public Boolean isClientAccept() {
        return clientChecker;
    }
}
