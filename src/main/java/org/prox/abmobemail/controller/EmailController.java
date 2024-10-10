package org.prox.abmobemail.controller;

import lombok.AllArgsConstructor;
import org.prox.abmobemail.entity.Email;
import org.prox.abmobemail.service.EmailService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emails")
@AllArgsConstructor
@CrossOrigin("*")
public class EmailController {
    private EmailService emailService;


    @GetMapping
    public ResponseEntity<Page<Email>> getAllEmails(Pageable pageable) {
        Page<Email> emails = emailService.findAll(pageable);
        return new ResponseEntity<>(emails, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Email> createEmail(@RequestBody Email email) {
        Email createdEmail = emailService.create(email);
        return new ResponseEntity<>(createdEmail, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Email> updateEmail(@PathVariable Long id, @RequestBody Email email) {
        Email updatedEmail = emailService.update(id, email);
        return new ResponseEntity<>(updatedEmail, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmail(@PathVariable Long id) {
        emailService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
