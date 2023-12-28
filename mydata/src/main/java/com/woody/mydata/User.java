package com.woody.mydata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "user_entity")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Basic(fetch = FetchType.LAZY)
    private String username;

    @Basic(fetch = FetchType.LAZY)
    @NotBlank
    private String password;

    @Email
    private String email;

    @Min(16)
    private Integer age;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_messengers", joinColumns = @JoinColumn(name = "user_id"))
    private Set<MessengerType> messengers;

    @OneToMany(mappedBy = "customer")
    @JsonIgnore
    private List<Order> orders;

    @ManyToMany()
    @JoinTable(name = "user_authority", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id"))
    private Set<Authority> authorities;

}
