package org.prox.abmobemail.repository;

import org.prox.abmobemail.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<Email, Long> {
}
