package com.woody.gdatabase.repository;

import com.woody.mydata.User;
import com.woody.mydata.UserDT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT new com.woody.mydata.UserDT(u.username, u.password) FROM User u WHERE u.username = :username")
    Optional<UserDT> findUsernameAndPasswordByUsername(@Param("username") String username);
}
