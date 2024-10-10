package org.prox.abmobemail.repository;

import org.prox.abmobemail.entity.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserIdRepository extends JpaRepository<UserId, Long> {
    UserId findByName(String name);
}
