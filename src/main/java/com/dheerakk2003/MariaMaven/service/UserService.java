package com.dheerakk2003.MariaMaven.service;

import com.dheerakk2003.MariaMaven.models.User;
import com.dheerakk2003.MariaMaven.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository ur;

    public UserService(UserRepository ur){
        this.ur = ur;
    }

    public Iterable<User> get(){
        return ur.findAll();
    }

    public User get(Long id){
        Optional<User> u = ur.findById(id);
        if(u.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return u.orElse(null);
    }

    public User getByEmail(String email){
        User u = ur.findByEmail(email);
        if(u == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return u;
    }

    public Long save(User u){
        if(u == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if(ur.findByEmail(u.getEmail()) != null){
            throw new ResponseStatusException(HttpStatus.IM_USED); // 226
        }
        User user = ur.save(u);
        return user.getId();
    }

    public void remove(Long id){
        ur.deleteById(id);
        throw new ResponseStatusException(HttpStatus.OK);
    }

    public void update(User u){
        ur.updateUser(u.getFullName(), u.getEmail(), u.getPassword(), u.getId());
    }
}
