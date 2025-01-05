package com.dheerakk2003.MariaMaven.web;

import com.dheerakk2003.MariaMaven.models.User;
import com.dheerakk2003.MariaMaven.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    public ResponseEntity<String> get(@PathVariable String email, @RequestHeader(value = "password", required = true) String password){
        User u = us.getByEmail(email);
        if(u != null) {
            System.out.println("password = " + password + u.getPassword());
            if (!u.getPassword().equals(password))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("wrong Password");
            return ResponseEntity.ok(u.getId().toString());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid user");
    }

    @DeleteMapping("/user/{id}")
    public void delete(@PathVariable Long id){
        us.remove(id);
    }

    @PostMapping("/user")
    public Long save(@RequestBody User u){
        return us.save(u);
    }

    @PutMapping("/user")
    public ResponseEntity edit(@RequestBody User u) {
        try{
            us.update(u);
            return ResponseEntity.ok("updated");
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
