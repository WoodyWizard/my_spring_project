package com.woody.mydata;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Getter
@Setter
public class UserDTO {

    String username;
    List<GrantedAuthority> authorities;

    public UserDTO() {

    }

    public UserDTO(String username, List<GrantedAuthority> authorities) {
        this.username = username;
        this.authorities = authorities;
    }

}
