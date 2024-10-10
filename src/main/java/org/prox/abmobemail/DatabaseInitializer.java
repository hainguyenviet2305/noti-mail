package org.prox.abmobemail;

import org.prox.abmobemail.entity.Role;
import org.prox.abmobemail.entity.User;
import org.prox.abmobemail.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByRole(Role.ADMIN).isEmpty()) {
            User admin = new User();
            admin.setEmail("admin");
            admin.setPassword(passwordEncoder.encode("123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }
    }
}
