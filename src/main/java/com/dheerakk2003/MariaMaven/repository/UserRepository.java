package com.dheerakk2003.MariaMaven.repository;

import com.dheerakk2003.MariaMaven.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}

