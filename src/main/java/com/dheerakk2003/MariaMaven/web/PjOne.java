package com.dheerakk2003.MariaMaven.web;

import com.dheerakk2003.MariaMaven.models.User;
import com.dheerakk2003.MariaMaven.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
public class PjOne {

    private final UserService us;


    public PjOne(UserService us){
        this.us = us;
    }

    @GetMapping("/users")
    public Iterable<User> get(){
        return us.get();
    }

    @GetMapping("/user/{id}")
    public User get(@PathVariable Long id){
        return us.get(id);
    }

    @GetMapping("/checkuser/{email}")
    public User get(@PathVariable String email){
        return us.getByEmail(email);
    }

    @DeleteMapping("/user/{id}")
    public void delete(@PathVariable Long id){
        us.remove(id);
    }

    @PostMapping("/user")
    public Long save(@RequestBody User u){
        return us.save(u);
    }


}
