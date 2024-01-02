package com.woody.mydata;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.weaver.ast.Or;

import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "deliverer_entity")
public class Deliverer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String name;
    @Email
    private String email;
    @NotBlank
    private String phone;
    @Min(18)
    private Integer age;
    private Double balance = 0.0;
    private Double rating = 0.0;
    @OneToMany(mappedBy = "deliverer")
    private Set<Order> orders;

}
