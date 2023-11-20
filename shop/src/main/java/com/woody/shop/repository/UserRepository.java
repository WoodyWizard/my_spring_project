package com.woody.shop.repository;



import com.woody.mydata.User;
import com.woody.mydata.UserDT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("""
            SELECT u FROM User u WHERE u.username = :username
            """)
    Optional<User> findUserByUsername(String username) throws UsernameNotFoundException;


    @Query("SELECT new com.woody.mydata.UserDT(u.username, u.password) FROM User u WHERE u.username = :username")
    Optional<UserDT> findUsernameAndPasswordByUsername(@Param("username") String username);
}
