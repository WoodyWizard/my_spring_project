package com.woody.mydata;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Embeddable
public class MessengerType {

    private Long id;
    private String name;

    public MessengerType(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public MessengerType() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
