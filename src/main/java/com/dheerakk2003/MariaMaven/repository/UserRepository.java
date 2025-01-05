package com.dheerakk2003.MariaMaven.repository;

import com.dheerakk2003.MariaMaven.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    @Modifying
    @Query("update User set fullName = ?1, email = ?2, password = ?3 where id = ?4")
    void updateUser(String fullName, String email, String password, Long id);
}

