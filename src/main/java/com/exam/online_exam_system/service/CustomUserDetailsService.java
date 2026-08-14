package com.exam.online_exam_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import com.exam.online_exam_system.repository.UserRepository;
import java.util.Collections;
import com.exam.online_exam_system.entity.User;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        String roleStr = user.getRole() != null ? user.getRole().name() : "ROLE_STUDENT";

        if (!roleStr.toUpperCase().startsWith("ROLE_")) {
            roleStr = "ROLE_" + roleStr.toUpperCase();
        } else {
            roleStr = roleStr.toUpperCase();
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(roleStr)));
    }
}
