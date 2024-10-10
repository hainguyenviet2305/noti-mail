package org.prox.abmobemail.repository;

import org.prox.abmobemail.entity.Role;
import org.prox.abmobemail.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByRole(Role role);
    Optional<User> findByEmail(String email);
}
