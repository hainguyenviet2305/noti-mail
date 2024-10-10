package org.prox.abmobemail.controller;

import org.prox.abmobemail.entity.UserId;
import org.prox.abmobemail.service.UserIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/userids")
public class UserIdController {

    @Autowired
    private UserIdService userIdService;

    @GetMapping
    public List<UserId> getAllUsers() {
        return userIdService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserId> getUserById(@PathVariable Long id) {
        Optional<UserId> userId = userIdService.getUserById(id);
        if (userId.isPresent()) {
            return ResponseEntity.ok(userId.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public UserId createUser(@RequestBody UserId userId) {
        return userIdService.createUser(userId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserId> updateUser(@PathVariable Long id, @RequestBody UserId userIdDetails) {
        try {
            UserId updatedUser = userIdService.updateUser(id, userIdDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userIdService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<UserId> getUserByName(@PathVariable String name) {
        UserId userId = userIdService.findByName(name);
        if (userId != null) {
            return ResponseEntity.ok(userId);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}