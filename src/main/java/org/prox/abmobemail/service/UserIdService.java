package org.prox.abmobemail.service;

import org.prox.abmobemail.entity.UserId;
import org.prox.abmobemail.repository.UserIdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserIdService {

    @Autowired
    private UserIdRepository userIdRepository;

    public List<UserId> getAllUsers() {
        return userIdRepository.findAll();
    }

    public Optional<UserId> getUserById(Long id) {
        return userIdRepository.findById(id);
    }

    public UserId createUser(UserId userId) {
        return userIdRepository.save(userId);
    }

    public UserId updateUser(Long id, UserId userIdDetails) {
        Optional<UserId> optionalUserId = userIdRepository.findById(id);
        if (optionalUserId.isPresent()) {
            UserId userId = optionalUserId.get();
            userId.setName(userIdDetails.getName());
            userId.setDiscordId(userIdDetails.getDiscordId());
            return userIdRepository.save(userId);
        } else {
            throw new RuntimeException("User not found with id " + id);
        }
    }

    public void deleteUser(Long id) {
        userIdRepository.deleteById(id);
    }

    public UserId findByName(String name) {
        return userIdRepository.findByName(name);
    }
}