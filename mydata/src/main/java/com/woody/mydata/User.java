package com.woody.mydata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.*;

@Entity
@Data
@AllArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String firstName;
    private String lastName;
    @Basic(fetch = FetchType.LAZY)
    private String username;
    @Basic(fetch = FetchType.LAZY)
    private String password;
    private String email;
    private Integer age;

    @ElementCollection
    @CollectionTable(name = "user_messengers", joinColumns = @JoinColumn(name = "user_id"))
    private Set<MessengerType> messengers = new HashSet();
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @Lazy
    @JsonIgnore
    private List<Order> orders = new ArrayList();


}
