package org.prox.abmobemail.service;

import lombok.AllArgsConstructor;
import org.prox.abmobemail.entity.Role;
import org.prox.abmobemail.entity.User;
import org.prox.abmobemail.exception.ResourceNotFoundException;
import org.prox.abmobemail.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User create(User user) {
        if (user.getRole() == null) {
            user.setRole(Role.USER); // Đặt vai trò mặc định là USER
        }
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Mã hóa mật khẩu
        return userRepository.save(user);
    }

    public User update(Long id, User user) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User updatedUser = existingUser.get();
            updatedUser.setEmail(user.getEmail());
            updatedUser.setPassword(passwordEncoder.encode(user.getPassword())); // Mã hóa mật khẩu
            updatedUser.setRole(user.getRole());
            return userRepository.save(updatedUser);
        } else {
            throw new ResourceNotFoundException("User not found with id " + id);
        }
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
//
//        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
//    }}


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            throw new UsernameNotFoundException(email);
        }
        User user = optionalUser.get(); // Lấy đối tượng User từ Optional
        Role role = user.getRole(); // Lấy role từ User
        GrantedAuthority authority = new SimpleGrantedAuthority(role.name()); // Sử dụng name() của enum Role để lấy tên
        System.out.println("User: " + user.getEmail() + ", Role: " + role.name());
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(authority) // Sử dụng Collections.singletonList để tạo một List chỉ chứa một phần tử
        );
    }
}

