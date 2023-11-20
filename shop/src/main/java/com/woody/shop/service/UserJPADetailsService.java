package com.woody.shop.service;

import com.woody.mydata.User;
import com.woody.shop.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.woody.mydata.UserDT;

import java.util.Optional;


@Service
@AllArgsConstructor
public class UserJPADetailsService implements UserDetailsService {

    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<UserDT> user = userRepository.findUsernameAndPasswordByUsername(username);
        return user.orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
