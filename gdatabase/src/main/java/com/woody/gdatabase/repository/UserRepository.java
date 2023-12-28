package com.woody.gdatabase.repository;

import com.woody.mydata.User;
import com.woody.mydata.UserDT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT new com.woody.mydata.UserDT(u.username, u.password) FROM User u WHERE u.username = :username")
    Optional<UserDT> findUsernameAndPasswordByUsername(@Param("username") String username);

    Optional<User> findByUsername(String username);

    //@Query("SELECT u FROM User u JOIN FETCH u.authorities WHERE u.username = :username")
    //Optional<User> findByUsername(@Param("username") String username);

}
